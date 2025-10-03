package nu.henrikvester.haraldlang.codegen;

import nu.henrikvester.haraldlang.codegen.ir.IRInst;
import nu.henrikvester.haraldlang.codegen.ir.IRTemp;

import java.util.ArrayList;
import java.util.List;

/**
 * A basic block of instructions in SSA form
 */
public class Block {
    private final List<IRInst> instructions = new ArrayList<>();
    private int nextId = 0;

    private IRTemp getTemp() {
        return new IRTemp(nextId++);
    }

    List<IRInst> getInstructions() {
        return instructions;
    }
}
