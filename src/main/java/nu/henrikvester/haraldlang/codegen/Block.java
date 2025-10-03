package nu.henrikvester.haraldlang.codegen;

import java.util.ArrayList;
import java.util.List;

/**
 * A basic block of instructions in SSA form
 */
public class Block {
    private final List<Instr> instructions = new ArrayList<>();
    private int nextId = 0;

    private Variable nextVariable() {
        return new Variable(nextId++);
    }

    Variable emitConst(int constant) {
        var id = nextVariable();
        instructions.add(new Instr(id, Op.CONST, new Variable[]{}, constant));
        return id;
    }

    Variable emitAdd(Variable left, Variable right) {
        var id = nextVariable();
        instructions.add(new Instr(id, Op.ADD, new Variable[]{left, right}, null));
        return id;
    }

    void emitAssignment(String name, Variable variable) {
        // TODO
    }

    List<Instr> getInstructions() {
        return instructions;
    }
}
