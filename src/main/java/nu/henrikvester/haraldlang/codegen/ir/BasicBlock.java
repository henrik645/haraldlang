package nu.henrikvester.haraldlang.codegen.ir;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class BasicBlock {
    @Getter
    private final Label label;
    @Getter
    private final List<IRInst> phis = new ArrayList<>();
    @Getter
    private final List<IRInst> instructions = new ArrayList<>();
    @Getter
    private IRTerminator terminator;

    public BasicBlock(Label label) {
        this.label = label;
    }

    void addPhi(IRInst phi) {
        phis.add(phi);
    }

    void add(IRInst instruction) {
        if (this.terminator != null) {
            throw new IllegalStateException("Block already terminated");
        }
        instructions.add(instruction);
    }

    void setTerminator(IRTerminator terminator) {
        if (this.terminator != null) {
            throw new IllegalStateException("Terminator already set");
        }
        this.terminator = terminator;
    }

    @Override
    public String toString() {
        return label + ":\n  PHI " + phis + "\n  " + instructions + "\n  TERM " + terminator;
    }

    public boolean isClosed() {
        return terminator != null;
    }
}
