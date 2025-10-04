package nu.henrikvester.haraldlang.codegen.ir;

import lombok.Getter;
import nu.henrikvester.haraldlang.ast.expressions.Var;
import nu.henrikvester.haraldlang.ast.statements.Declaration;
import nu.henrikvester.haraldlang.codegen.ir.primitives.IRFunction;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.IRInst;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.Mov;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.Phi;
import nu.henrikvester.haraldlang.codegen.ir.primitives.terminators.IRTerminator;
import nu.henrikvester.haraldlang.codegen.ir.primitives.terminators.RetVoid;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRParam;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.*;

public class FunctionBuilder23 {
    private final String name;
    private final Bindings bindings;
    private final List<VarSlot> params;
    private final Map<Label, BasicBlock> blocks = new LinkedHashMap<>();
    private final Map<BasicBlock, Map<VarSlot, IRValue>> block2var2value = new LinkedHashMap<>();
    private final Map<BasicBlock, Map<VarSlot, Phi>> incompletePhis = new LinkedHashMap<>();
    // maybe this needs to be ordered?
    private final Set<BasicBlock> sealed = new HashSet<>();

    // For each forward label that we haven't seen yet,
    // keep track of the label and the blocks that branch to it.
    // When we see the label and can associate it with a block,
    // add these blocks as predecessors to that block.

    // when we reach a terminator,
    // the terminator points to zero, one, or two successor blocks.
    // each of these successor blocks needs to know who their predecessors are.
    // for each successor of a just-terminated block:
    // pending.augment(successor, currentBlock);

    // when we start a new block (in mark),
    // we need to check if there are any predecessors that should be added to it.
    // we look up the new block's label in pending,
    // and add the list of predecessors to the block.

    private final Map<Label, List<BasicBlock>> pending = new LinkedHashMap<>();

    private int nextTemp = 0;
    private int nextLabel = 0;
    @Getter
    private BasicBlock currentBlock;

    public FunctionBuilder23(String name, Bindings bindings, List<VarSlot> params) {
        this.name = name;
        this.bindings = bindings;
        this.params = params;

        mark(newLabel("func_" + name));
        var i = 0;
        for (var param : params) {
            block2var2value.get(currentBlock).put(param, new IRParam(i));
            i++;
            System.out.println("param: " + i + " -> " + param);
        }
    }

    public IRTemp newTemp() {
        return new IRTemp(nextTemp++);
    }

    public Label newLabel(String purpose) {
        return new Label(nextLabel++, purpose);
    }

    // when we start a new block (in mark),
    // we need to check if there are any predecessors that should be added to it.
    // we look up the new block's label in `pending`,
    // and add the list of predecessors to the block.
    public void mark(Label label) {
        // create or get the new block
        currentBlock = blocks.computeIfAbsent(label, this::newBlock);

        // if there are any predecessors for this label,
        if (pending.containsKey(label)) {
            for (var pred : pending.get(label)) {
                currentBlock.addPredecessor(pred);
                onAddEdge(pred, currentBlock);
            }
            pending.remove(label);
        }
    }

    private void onAddEdge(BasicBlock from, BasicBlock to) {
        to.addPredecessor(from);
        for (var entry : incompletePhis.get(to).entrySet()) {
            VarSlot slot = entry.getKey();
            Phi phi = entry.getValue();
            IRValue pv = readVar(slot, from);
            IRTemp op = ensureMaterializedIn(from, pv);
            phi.addIncoming(from.getLabel(), op);
        }
    }

    public void emit(IRInst inst) {
        currentBlock.add(inst);
    }

    // when we reach a terminator,
    // the terminator points to zero, one, or two successor blocks.
    // each of these successor blocks needs to know who their predecessors are.
    // for each successor of a just-terminated block:
    // pending.augment(successor, currentBlock);
    public void endWith(IRTerminator terminator) {
        var from = currentBlock;
        currentBlock.setTerminator(terminator);
        for (var succLabel : terminator.successors()) {
            // first, ensure that there is a list
            if (!pending.containsKey(succLabel)) {
                pending.put(succLabel, new ArrayList<>());
            }
            pending.get(succLabel).add(currentBlock);

            var succ = blocks.get(succLabel);
            if (succ != null) {
                onAddEdge(from, succ);
            }
        }
    }

    public IRFunction finish() {
        // it's okay if the current block is open; just return void
        if (!currentBlock.isClosed()) {
            endWith(new RetVoid());
        }
        // ensure all other blocks are closed
        for (var block : blocks.values()) {
            if (!block.isClosed()) {
                throw new IllegalStateException("Block " + block.getLabel() + " is not closed");
            }
        }

        return new IRFunction(name, getFirstBlock().getLabel(), new ArrayList<>(blocks.values()));
    }

    public IRValue readVar(Var variable) {
        return readVar(bindings.slot(variable));
    }

    public IRValue readVar(VarSlot slot) {
        return readVar(slot, currentBlock);
    }

    private IRTemp ensureMaterializedIn(BasicBlock block, IRValue value) {
        if (value instanceof IRTemp temp) {
            return temp;
        }

        var temp = newTemp();
        block.addBeforeTerminator(new Mov(temp, value));
        return temp;
    }

    private IRValue readVar(VarSlot slot, BasicBlock block) {
//        var slot = bindings.slot(variable);
        IRValue value = block2var2value.get(block).get(slot);
        if (value != null) {
            return value; // found in the current block
        }
        // look for it in predecessors
        var preds = block.getPredecessors();
        if (preds.isEmpty()) {
            throw new IllegalStateException("When getting value for slot " + slot + " + in block " + block.getLabel() + ", the value was not found in the block and the block has no predecessors");
        }
        if (preds.size() == 1) {
            // get from successor
            return readVar(slot, preds.getFirst());
        }

        // insert phi

        var dst = newTemp();
        var incomings = new LinkedHashMap<Label, IRTemp>();
        for (var pred : preds) {
            value = readVar(slot, pred);
            var irTemp = ensureMaterializedIn(pred, value);
            incomings.put(pred.getLabel(), irTemp);
        }
        Phi phi = new Phi(dst, incomings);
        emit(phi);

        setVar(slot, dst);

        return dst;
    }

    public void setVar(Var variable, IRValue value) {
        var slot = bindings.slot(variable);
        setVar(slot, value);
    }

    public void setVar(VarSlot slot, IRValue value) {
        block2var2value.get(currentBlock).put(slot, value);
    }

    public void setVar(Declaration declaration, IRValue value) {
        var slot = bindings.slot(declaration);
        setVar(slot, value);
    }

    private BasicBlock newBlock(Label label) {
        var block = new BasicBlock(label);
        block2var2value.put(block, new LinkedHashMap<>());
        incompletePhis.put(block, new LinkedHashMap<>());
        return block;
    }

    private BasicBlock getFirstBlock() {
        return blocks.values().iterator().next();
    }
}
