package nu.henrikvester.haraldlang.codegen.ir;

import lombok.Getter;
import nu.henrikvester.haraldlang.ast.expressions.Var;
import nu.henrikvester.haraldlang.ast.statements.Declaration;
import nu.henrikvester.haraldlang.codegen.ir.primitives.IRFunction;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.IRInst;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.Load;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.Mov;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.Store;
import nu.henrikvester.haraldlang.codegen.ir.primitives.terminators.IRTerminator;
import nu.henrikvester.haraldlang.codegen.ir.primitives.terminators.RetVoid;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRFrameSlot;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FunctionBuilder {
    private final String name;
    private final Bindings bindings;
    private final Map<Label, BasicBlock> blocks = new LinkedHashMap<>();
    private final Map<Label, List<BasicBlock>> label2predecessors = new LinkedHashMap<>();
    private final Map<VarSlot, IRFrameSlot> var2frame = new LinkedHashMap<>();

    private int nextTemp = 0;
    private int nextLabel = 0;
    @Getter
    private BasicBlock currentBlock;

    public FunctionBuilder(String name, Bindings bindings, List<VarSlot> locals) {
        this.name = name;
        this.bindings = bindings;

        mark(newLabel("func_" + name));
        var i = 0;
        for (var param : locals) {
            var2frame.put(param, new IRFrameSlot(i));
            System.out.println("Created frame slot for param " + param + " at index " + i);
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
        System.out.println("Marking block " + label);
        // create or get the new block
        currentBlock = blocks.computeIfAbsent(label, this::newBlock);

        // if there are any predecessors for this label,
        if (label2predecessors.containsKey(label)) {
            var predecessors = label2predecessors.get(label);
            for (var pred : predecessors) {
                currentBlock.addPredecessor(pred);
            }
            label2predecessors.remove(label);
            System.out.println(" - Added " + predecessors.size() + " pending predecessors to block " + currentBlock.getLabel());
        }
    }

    public void emit(IRInst inst) {
        emit(inst, currentBlock);
    }

    private void emit(IRInst inst, BasicBlock block) {
        block.add(inst);
    }

    public void endWith(IRTerminator terminator) {
        System.out.println("Ending block " + currentBlock.getLabel());
        currentBlock.setTerminator(terminator);
        for (var successor : terminator.successors()) {
            if (blocks.containsKey(successor)) {
                // we have already seen the successor and can add directly
                blocks.get(successor).addPredecessor(currentBlock);
                System.out.println(" - Added predecessor " + currentBlock.getLabel() + " to " + successor);
            } else {
                // we haven't seen the successor yet; add to pending for addition when we see it (in mark method)
                var predecessors = label2predecessors.computeIfAbsent(successor, k -> new ArrayList<>());
                predecessors.add(currentBlock);
                System.out.println(" - Added pending predecessor " + currentBlock.getLabel() + " to " + successor);
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

        return new IRFunction(name, getFirstBlock().getLabel(), new ArrayList<>(blocks.values()), nextTemp);
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
        var tmp = newTemp();
        var frame = var2frame.get(slot);
        emit(new Load(tmp, frame));
        return tmp;
//        IRValue value = block2var2value.get(block).get(slot);
//        if (value != null) {
//            return value; // found in the current block
//        }
//        // look for it in predecessors
//        var preds = block.getPredecessors();
//        if (preds.isEmpty()) {
//            throw new IllegalStateException("When getting value for slot " + slot + " + in block " + block.getLabel() + ", the value was not found in the block and the block has no predecessors");
//        }
//        if (preds.size() == 1) {
//            // get from the only predecessor
//            var v = readVar(slot, preds.getFirst());
//            setVar(slot, v);
//            return ensureMaterializedIn(currentBlock, v);
//        }
//
//        // Multiple predecessors -- insert phi.
//        // Map from each predecessor label to the corresponding temp.
//        var incomings = new LinkedHashMap<Label, IRTemp>();
//        for (var pred : preds) {
//            value = readVar(slot, pred);
//            // ensure materialized in the predecessor
//            var irTemp = ensureMaterializedIn(pred, value);
//            incomings.put(pred.getLabel(), irTemp);
//        }
//        var dst = newTemp();
//        Phi phi = new Phi(dst, incomings);
//        block.addPhi(phi);
////        emit(phi);
//
//        // associate variable slot with the phi-returned value in this block
//        setVar(slot, dst);
//        return dst;
    }

    public void setVar(Var variable, IRValue value) {
        var slot = bindings.slot(variable);
        setVar(slot, value);
    }

    public void setVar(VarSlot slot, IRValue value) {
        var frame = var2frame.get(slot);
        emit(new Store(frame, value));
    }

    public void setVar(Declaration declaration, IRValue value) {
        var slot = bindings.slot(declaration);
        setVar(slot, value);
    }

    private BasicBlock newBlock(Label label) {
        return new BasicBlock(label);
    }

    private BasicBlock getFirstBlock() {
        return blocks.values().iterator().next();
    }
}
