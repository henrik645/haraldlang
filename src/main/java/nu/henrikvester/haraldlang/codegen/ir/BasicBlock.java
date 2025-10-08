package nu.henrikvester.haraldlang.codegen.ir;

import lombok.Getter;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.IRInst;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.Phi;
import nu.henrikvester.haraldlang.codegen.ir.primitives.terminators.IRTerminator;

import java.util.ArrayList;
import java.util.List;

public class BasicBlock {
    @Getter
    private final Label label;
    @Getter
    private final List<Phi> phis = new ArrayList<>();

    // TODO should getInstructions return phis, too?
    @Getter
    private final ArrayList<IRInst> instructions = new ArrayList<>();
    @Getter
    private IRTerminator terminator;
    private final List<BasicBlock> predecessors = new ArrayList<>();

    public BasicBlock(Label label) {
        this.label = label;
    }

    void addPhi(Phi phi) {
        phis.add(phi);
    }

    void add(IRInst instruction) {
        if (this.terminator != null) {
            throw new IllegalStateException("Block already terminated");
        }
        if (instruction instanceof Phi phi) {
            addPhi(phi);
            return;
        }
        instructions.add(instruction);
    }

    void addBeforeTerminator(IRInst instruction) {
        if (isClosed()) {
            // add last
            instructions.add(instructions.size(), instruction);
        } else {
            add(instruction);
        }
    }
    
    void setTerminator(IRTerminator terminator) {
        if (this.terminator != null) {
            throw new IllegalStateException("Terminator already set");
        }
        this.terminator = terminator;
    }

    void addPredecessor(BasicBlock block) {
        if (!predecessors.contains(block)) {
            predecessors.add(block);
        }
    }

    List<BasicBlock> getPredecessors() {
        return List.copyOf(predecessors);
    }

    @Override
    public String toString() {
        var instructionStrings = instructions.stream().map(Object::toString).map(line -> "    " + line).toList();
        var instructionStr = instructionStrings.isEmpty() ? "    <none>" : String.join("\n", instructionStrings);
        var predsStr = predecessors.stream().map(block -> block.getLabel().toString()).toList();
        return label + ":\n  PREDS" + predsStr + "\n  PHI " + phis + "\n" + instructionStr + "\n  TERM " + terminator;
    }

    public boolean isClosed() {
        return terminator != null;
    }
}
