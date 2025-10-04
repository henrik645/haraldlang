package nu.henrikvester.haraldlang.codegen.ir;

import lombok.Getter;
import nu.henrikvester.haraldlang.codegen.ir.primitives.IRFunction;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.IRInst;
import nu.henrikvester.haraldlang.codegen.ir.primitives.terminators.IRTerminator;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * Pipeline:
 *
 * 1. Parse → AST
 * 2. Semantic checks (types/symbols) → annotate AST or side tables
 * 3. Lowering (AST → IR) using a visitor and Translator emitter
 * 4. SSA construction (insert φ + rename), then simple opts (const-prop, DCE)
 * 5. Instruction selection (IR → your ISA), register allocation, spill code
 * 6. Emit machine code / bytecode
 */

public class FunctionBuilder {
    private final String name;
    @Getter // TODO: remove getter -- all access to block should be from `finish` method
    private final Map<Label, BasicBlock> blocks = new LinkedHashMap<>();
    private int nextTemp = 0;
    private int nextLabel = 0;
    @Getter
    private BasicBlock currentBlock;

    public FunctionBuilder(String name) {
        this.name = name;
        var currentBlockLabel = newLabel("func_" + name);
        this.currentBlock = newBlock(currentBlockLabel);
        this.blocks.put(currentBlockLabel, this.currentBlock);
    }

    IRTemp newTemp() {
        return new IRTemp(nextTemp++);
    }

    Label newLabel(String purpose) {
        return new Label(nextLabel++, purpose);
    }

    private BasicBlock newBlock(Label label) {
        return new BasicBlock(label);
    }

    private BasicBlock getOrCreate(Label label) {
        return blocks.computeIfAbsent(label, this::newBlock);
    }

    void emit(IRInst instruction) {
        currentBlock.add(instruction);
    }

    void endWith(IRTerminator terminator) {
        currentBlock.setTerminator(terminator);
    }

    void mark(Label label) {
        // TODO should this check that the current block has ended? No, because of fallthrough and we might want to return to it later
        currentBlock = getOrCreate(label);
    }

    private BasicBlock getFirstBlock() {
        return blocks.values().iterator().next();
    }

    IRFunction finish() {
        if (!currentBlock.isClosed()) {
            throw new IllegalStateException("Current block is not closed");
        }
        return new IRFunction(name, getFirstBlock().getLabel(), new ArrayList<>(blocks.values()));
    }
}
