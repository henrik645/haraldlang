package nu.henrikvester.haraldlang.codegen.ir;

import nu.henrikvester.haraldlang.codegen.ir.primitives.IRFunction;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.Load;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.Phi;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.Store;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRFrameSlot;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;
import nu.henrikvester.haraldlang.exceptions.NotImplementedException;

import java.util.*;
import java.util.stream.Collectors;

public class SSA {
    public void convertToSSA(IRFunction function, Bindings bindings) {
        System.out.println(bindings);

        System.out.println("Promotable slots:");
        findPromotableSlots(function).forEach(System.out::println);


        var promotable = findPromotableSlots(function);
        if (promotable.isEmpty()) return;


        var defBlocks = computeDefBlocks(function, promotable);
        System.out.println("\nDef-blocks:");
        for (var e : defBlocks.entrySet()) {
            var slot = e.getKey();
            var blocks = e.getValue();
            System.out.println(slot + " defined in blocks: " + blocks.stream().map(BasicBlock::getLabel).toList());
        }

        var dominators = Dominators.compute(function);
        System.out.println("\nDominance frontier:");
        for (var dominator : dominators.domTree.entrySet()) {
            var node = dominator.getKey();
            var children = dominator.getValue();
            System.out.println(node.getLabel() + " loses dominance at " + children.stream().map(BasicBlock::getLabel).toList());
        }

        System.out.println("\nImmediate dominators:");
        for (var e : dominators.idom.entrySet()) {
            var node = e.getKey();
            var dominator = e.getValue();
            if (dominator == null) {
                System.out.println(node.getLabel() + " is the entry block");
            } else {
                System.out.println(node.getLabel() + " is immediately dominated by " + dominator.getLabel());
            }
        }
    }

    private Set<IRFrameSlot> findPromotableSlots(IRFunction function) {
        Map<IRFrameSlot, Boolean> ok = new HashMap<>();

        for (var block : function.basicBlocks()) {
            for (var inst : block.getInstructions()) {
                if (inst instanceof Store store) {
                    var frame = store.dst();
                    ok.merge(frame, true, Boolean::logicalAnd);
                } else if (inst instanceof Load load) {
                    var frame = load.src();
                    ok.merge(frame, true, Boolean::logicalAnd);
                } else {
                    for (var v : inst.uses()) {
                        // any other instruction that directly uses a frame slot is not promotable
                        if (v instanceof IRFrameSlot slot) {
                            ok.put(slot, false);
                        }
                    }
                }
            }
        }

        return ok.entrySet().stream().filter(e -> Boolean.TRUE.equals(e.getValue())).map(Map.Entry::getKey).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Returns a map from a frame slot to the set of basic blocks that define it.
     *
     * @param function the function to compute the def-blocks for
     * @param vars     the set of frame slots to compute the def-blocks for
     * @return a map from a frame slot to the set of basic blocks that define it
     */
    private Map<IRFrameSlot, Set<BasicBlock>> computeDefBlocks(IRFunction function, Set<IRFrameSlot> vars) {
        var defs = new HashMap<IRFrameSlot, Set<BasicBlock>>();
        for (var v : vars) {
            defs.put(v, new LinkedHashSet<>());
        }
        for (var block : function.basicBlocks()) {
            for (var inst : block.getInstructions()) {
                if (inst instanceof Store store && vars.contains(store.dst())) {
                    defs.get(store.dst()).add(block);
                }
            }
        }
        return defs;
    }

    /**
     * Places phis in the basic blocks
     *
     * @param vars               the set of frame slots to place phis for
     * @param defBlocks          the def-blocks for the frame slots
     * @param dominanceFrontiers the dominance frontiers for the basic blocks
     * @return a map from a basic block to a map from a frame slot to a phi instruction
     */
    private Map<BasicBlock, Map<IRFrameSlot, Phi>> placePhis
    (Set<IRFrameSlot> vars,
     Map<IRFrameSlot, Set<BasicBlock>> defBlocks,
     Map<BasicBlock, Set<BasicBlock>> dominanceFrontiers) {
        Map<BasicBlock, Map<IRFrameSlot, Phi>> placed = new HashMap<>();

        for (var v : vars) {
            Deque<BasicBlock> W = new ArrayDeque<>(defBlocks.getOrDefault(v, Set.of()));
            Set<BasicBlock> hasAlready = new HashSet<>();
            while (!W.isEmpty()) {
                var X = W.pop();
                for (var Y : dominanceFrontiers.getOrDefault(X, Set.of())) {
                    if (!phiPresent(placed, Y, v)) {
                        Phi phi = new Phi(nextTemp(), Map.of());
                        placed.computeIfAbsent(Y, k -> new LinkedHashMap<>()).put(v, phi);
                        Y.addPhi(phi);
                        if (!hasAlready.contains(Y)) {
                            hasAlready.add(Y);
                            W.push(Y);
                        }
                    }
                }
            }
        }

        return placed;
    }

    private IRTemp nextTemp() {
        throw new NotImplementedException();
    }

    private boolean phiPresent(Map<BasicBlock, Map<IRFrameSlot, Phi>> placed, BasicBlock block, IRFrameSlot v) {
        return placed.containsKey(block) && placed.get(block).containsKey(v);
    }

    private record Dominators(Map<BasicBlock, BasicBlock> idom, Map<BasicBlock, Set<BasicBlock>> domTree) {

        static Map<BasicBlock, BasicBlock> computeIdoms(IRFunction function) {
                var blocks = function.basicBlocks();
                if (blocks.isEmpty()) return Map.of();
                BasicBlock entry = entry(function);
    
                Map<BasicBlock, List<BasicBlock>> succ = buildSuccessors(blocks);
    
                Set<BasicBlock> V = reachable(entry, succ);
    
                Map<BasicBlock, Set<BasicBlock>> dom = new LinkedHashMap<>();
                for (var block : V) {
                    if (block == entry) {
                        dom.put(block, new LinkedHashSet<>(Set.of(block)));
                    } else {
                        dom.put(block, new LinkedHashSet<>(V));
                    }
                }
    
                boolean changed;
                do {
                    changed = false;
                    for (var block : V) {
                        if (block == entry) continue;
                        Set<BasicBlock> inter = new LinkedHashSet<>(V);
                        for (var p : block.getPredecessors()) {
                            if (V.contains(p)) {
                                inter.retainAll(dom.get(p));
                            }
                        }
                        inter.add(block);
    
                        if (!inter.equals(dom.get(block))) {
                            dom.put(block, inter);
                            changed = true;
                        }
                    }
                } while (changed);
    
                Map<BasicBlock, BasicBlock> idom = new LinkedHashMap<>();
                idom.put(entry, entry);
    
                for (var b : V) {
                    if (b == entry) continue;
                    Set<BasicBlock> strict = new LinkedHashSet<>(dom.get(b));
                    strict.remove(b);
    
                    BasicBlock imm = null;
                    outer:
                    for (var d : strict) {
                        for (var e : strict) {
                            if (e == d) {
                                continue;
                            }
                            if (!dom.get(d).contains(e)) {
                                continue outer;
                            }
                        }
                        imm = d;
                        break;
                    }
                    idom.put(b, imm);
                }
    
                return idom;
            }
    
            private static Map<BasicBlock, List<BasicBlock>> buildSuccessors(List<BasicBlock> blocks) {
                Map<BasicBlock, List<BasicBlock>> succ = new LinkedHashMap<>();
                for (var block : blocks) {
                    succ.putIfAbsent(block, new ArrayList<>());
                }
                for (var to : blocks) {
                    for (var from : blocks) {
                        succ.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
                    }
                }
                return succ;
            }
    
            private static Set<BasicBlock> reachable(BasicBlock entry, Map<BasicBlock, List<BasicBlock>> succ) {
                Set<BasicBlock> seen = new HashSet<>();
                Deque<BasicBlock> st = new ArrayDeque<>();
                st.push(entry);
                while (!st.isEmpty()) {
                    var block = st.pop();
                    if (!seen.add(block)) continue;
                    for (var s : succ.getOrDefault(block, List.of())) {
                        st.push(s);
                    }
                }
                return seen;
            }
    
            static Dominators compute(IRFunction function) {
                // gets the immediate dominator of a block
                Map<BasicBlock, BasicBlock> idom = computeIdoms(function);
                // set of nodes where a basic block's dominance stops
                // i.e., where we need to insert phis
                Map<BasicBlock, Set<BasicBlock>> frontier = new HashMap<>();
    
                for (var block : function.basicBlocks()) {
                    frontier.put(block, new HashSet<>());
                }
                for (var block : function.basicBlocks()) {
                    var preds = block.getPredecessors();
                    if (preds.size() >= 2) {
                        for (var pred : preds) {
                            var runner = pred;
                            while (runner != idom.get(block)) {
                                frontier.get(runner).add(block);
                                runner = idom.get(runner);
                            }
                        }
                    }
                }
    
                return new Dominators(idom, frontier);
            }
    
            private static BasicBlock entry(IRFunction function) {
                return function.basicBlocks().stream().filter(b -> b.getLabel().equals(function.entry())).findFirst().orElseThrow();
            }
    
            public @Override String toString() {
                return "Dominators{" +
                        "idom=" + idom +
                        ", domTree=" + domTree +
                        '}';
            }
        }
}
