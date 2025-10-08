package nu.henrikvester.haraldlang.codegen.ir;

import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.IRInst;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.Load;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.Phi;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.Store;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRFrameSlot;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.*;
import java.util.stream.Collectors;

public class PhiRecorder {
    private final CFG cfg;
    private final Set<IRFrameSlot> eligibleVars;
    private final Map<IRFrameSlot, Set<BasicBlock>> var2definingBlocks;
    // TODO this does not need to keep track of the slot now that we have it in the phi?
    private final Map<BasicBlock, Map<IRFrameSlot, Phi>> recordedPhis;
    /* RENAMER */
    Map<IRFrameSlot, Deque<IRTemp>> slot2nameStack = new HashMap<>();
    // record of which variables got new names while visiting a block
    Map<BasicBlock, List<IRFrameSlot>> pushedInBlock = new HashMap<>();

    public PhiRecorder(CFG cfg) {
        this.cfg = cfg;
        this.recordedPhis = new LinkedHashMap<>();
        this.eligibleVars = findEligibleVars();
        this.var2definingBlocks = computeDefiningBlocks();
    }

    private Set<IRFrameSlot> findEligibleVars() {
        Map<IRFrameSlot, Boolean> ok = new HashMap<>();
        for (var block : cfg.getBlocks()) {
            for (var inst : block.getInstructions()) {
                if (inst instanceof Store store) {
                    var frame = store.dst();
                    ok.merge(frame, true, Boolean::logicalAnd);
                } else if (inst instanceof Load load) {
                    var frame = load.src();
                    ok.merge(frame, true, Boolean::logicalAnd);
                } else {
                    for (var v : inst.operands()) {
                        // any other instruction that directly uses a frame slot is not promotable
                        if (v.getValue() instanceof IRFrameSlot slot) {
                            ok.put(slot, false);
                        }
                    }
                }
            }
        }
        return ok.entrySet().stream().filter(e -> Boolean.TRUE.equals(e.getValue())).map(Map.Entry::getKey).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private boolean phiForSlotPresentInBlock(IRFrameSlot slot, BasicBlock block) {
        return recordedPhis.containsKey(block) && recordedPhis.get(block).containsKey(slot);
    }

    private void recordPhiForSlotInBlock(IRFrameSlot variable, BasicBlock block, Phi phi) {
        if (!recordedPhis.containsKey(block)) {
            recordedPhis.put(block, new LinkedHashMap<>());
        }
        recordedPhis.get(block).put(variable, phi);
    }

    private Map<IRFrameSlot, Set<BasicBlock>> computeDefiningBlocks() {
        Map<IRFrameSlot, Set<BasicBlock>> definingBlocks = new HashMap<>();
        for (var v : eligibleVars) {
            definingBlocks.put(v, new LinkedHashSet<>());
        }
        for (var block : cfg.getBlocks()) {
            for (var inst : block.getInstructions()) {
                if (inst instanceof Store store) {
                    var frame = store.dst();
                    definingBlocks.get(frame).add(block);
                }
            }
        }
        return definingBlocks;
    }

    private Set<BasicBlock> definingBlocksFor(IRFrameSlot slot) {
        return var2definingBlocks.get(slot);
    }

    private boolean slotIsDefinedInBlock(IRFrameSlot slot, BasicBlock block) {
        return definingBlocksFor(slot).contains(block);
    }

    public void recordPhis() {
        for (var variable : eligibleVars) {
            var definingBlocks = new ArrayDeque<>(definingBlocksFor(variable));
            var inWork = new HashSet<>(definingBlocks);
            while (!definingBlocks.isEmpty()) {

                // the block that defines the variable
                var definingBlock = definingBlocks.pop();
                inWork.remove(definingBlock);
                var dominanceFrontierBlocks = cfg.dominanceFrontierFor(definingBlock);
                // definingBlock loses dominance in each dominanceFrontierBlock
                for (var dominanceFrontierBlock : dominanceFrontierBlocks) {
                    boolean hadNonPhiDef = slotIsDefinedInBlock(variable, dominanceFrontierBlock);
                    boolean insertedPhi = false;
                    // For each block where the defining block loses dominance, we need to place a phi for the variable.
                    if (!phiForSlotPresentInBlock(variable, dominanceFrontierBlock)) {
                        var temp = cfg.nextTemp();
                        var phi = new Phi(temp, variable);
                        recordPhiForSlotInBlock(variable, dominanceFrontierBlock, phi);
                        insertedPhi = true;
                    }
                    if (insertedPhi && !hadNonPhiDef && inWork.add(dominanceFrontierBlock)) {
                        // The dominance frontier block did not define the variable.
                        // Furthermore, we added a phi.
                        // We need to add this block for consideration as a defining block.
                        definingBlocks.push(dominanceFrontierBlock);
                    }
                }
            }
        }
    }

    public boolean blockHasRecordedPhis(BasicBlock block) {
        return !recordedPhis.get(block).isEmpty();
    }

    public Map<IRFrameSlot, Phi> getRecordedPhis(BasicBlock block) {
        return recordedPhis.getOrDefault(block, Map.of());
    }

    /**
     * Registers that the block now defines a new SSA name for the variable the temp refers to.
     *
     * @param slot  the variable that the temp refers to
     * @param temp  the SSA name for the variable
     * @param block the block that defines the variable
     */
    private void define(IRFrameSlot slot, IRTemp temp, BasicBlock block) {
        slot2nameStack.computeIfAbsent(slot, k -> new ArrayDeque<>()).push(temp);
        pushedInBlock.computeIfAbsent(block, k -> new ArrayList<>()).add(slot);
    }

    private IRTemp currentNameFor(IRFrameSlot slot) {
        var stack = slot2nameStack.get(slot);
        if (stack == null || stack.isEmpty()) {
            throw new IllegalStateException("No value for slot " + slot + " found");
        }
        return stack.peek();
    }

    public void materializePhiNodes() {
        for (var block : cfg.getReachableBlocks()) {
            for (var e : getRecordedPhis(block).entrySet()) {
                var phi = e.getValue();

                block.addPhi(phi);

                define(phi.slot(), phi.dst(), block);
            }
        }
    }

    public void ssaRename() {
        renameDFS(cfg.getEntry());
    }

    private void renameDFS(BasicBlock block) {
        List<IRInst> toRemove = new ArrayList<>();

        for (var instruction : block.getInstructions()) {
            // rename all uses
            for (var use : instruction.operands()) {
                // if isAddressOperand(inst, i) continue;
                if (use.getValue() instanceof IRFrameSlot slot) {
                    use.replaceWith(currentNameFor(slot));
                }
            }

            // These are the only instructions that do not make use of temps already, and we need to replace them
            if (instruction instanceof Store store) {
                var slot = store.dst();
                var t = cfg.nextTemp();
                define(slot, t, block);
                // TODO
                toRemove.add(store);
                System.out.println("Store " + store + " replaced with " + t);
            } else if (instruction instanceof Load load) {
                IRFrameSlot slot = load.src();
                IRTemp val = load.dst();
                IRTemp name = currentNameFor(slot);
                replaceAllUsesOf(val, name);
                // TODO
                toRemove.add(load);
                System.out.println("Load " + load + " replaced with " + name);
            }
        }

        for (var toRemoveInst : toRemove) {
            block.getInstructions().remove(toRemoveInst);
        }

        for (var successor : cfg.successorsFor(block)) {
            onTraverseEdge(block, successor);
        }

        for (var child : cfg.dominatedChildrenFor(block)) {
            renameDFS(child);
        }

        // pop everything defined in block (phi results and instruction definitions (?))
        var list = pushedInBlock.getOrDefault(block, List.of());
        for (int i = list.size() - 1; i >= 0; --i) {
            var v = list.get(i);
            slot2nameStack.get(v).pop();
        }
    }

    private void replaceAllUsesOf(IRTemp oldValue, IRValue newValue) {
        var uses = List.copyOf(oldValue.users());
        for (var use : uses) {
            use.replaceWith(newValue);
        }
    }

//    private boolean isAddressOperand(IRInst inst, int idx) {
//        if (inst instanceof Store) return idx == 0; // dst address
//        if (inst instanceof Load)  return idx == 0; // address
//        return false;
//    }

    // at the moment we leave the predecessor heading for the successor,
    // the top of each stack is the value **exiting P**.
    // We use this value for the successor's phis.
    void onTraverseEdge(BasicBlock pred, BasicBlock succ) {
        for (var e : getRecordedPhis(succ).entrySet()) {
            Phi phi = e.getValue();
            IRFrameSlot slot = phi.slot();
            IRTemp incoming = currentNameFor(slot);
            phi.addIncoming(pred.getLabel(), incoming);
        }
    }
}
