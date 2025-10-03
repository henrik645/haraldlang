package nu.henrikvester.haraldlang.codegen.ir;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

@RequiredArgsConstructor
public class FunctionBuilder {
    private final String name;
    @Getter // TODO: remove getter -- all access to block should be from `finish` method
    private final Map<Label, BasicBlock> blocks = new LinkedHashMap<>();
    private int nextTemp = 0;
    private int nextLabel = 0;
    BasicBlock current = newBlock(newLabel());

    IRTemp newTemp() {
        return new IRTemp(nextTemp++);
    }

    Label newLabel() {
        return new Label(nextLabel++);
    }

    BasicBlock newBlock(Label label) {
        var block = new BasicBlock(label);
        blocks.put(label, block);
        return block;
    }

    BasicBlock getOrCreate(Label label) {
        return blocks.computeIfAbsent(label, this::newBlock);
    }

    void emit(IRInst instruction) {
        current.add(instruction);
    }

    void endWith(IRTerminator terminator) {
        current.setTerminator(terminator);
    }

    void mark(Label label) {
        // TODO should this check that the current block has ended? No, because of fallthrough and we might want to return to it later
        current = getOrCreate(label);
    }

    private BasicBlock getFirstBlock() {
        return blocks.values().iterator().next();
    }

    IRFunction finish() {
        if (!current.isClosed()) {
            throw new IllegalStateException("Current block is not closed");
        }
        return new IRFunction(name, getFirstBlock().getLabel(), new ArrayList<>(blocks.values()));
    }
}
