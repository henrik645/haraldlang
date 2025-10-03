package nu.henrikvester.haraldlang.codegen;

public record Instr(Variable result, Op op, Variable[] inputs, Integer imm) {
    @Override
    public String toString() {
        return switch (op) {
            case CONST -> result + " = " + imm;
            case ADD -> result + " = " + inputs[0] + " + " + inputs[1];
        };
    }

    public String mnemonic(RegisterAllocator allocator) {
        return switch (op) {
            case CONST -> {
                int reg = allocator.getRegisterForVariable(result);
                yield "LDI r" + reg + ", " + imm;
            }
            case ADD -> {
                int reg1 = allocator.getRegisterForVariable(inputs[0]);
                int reg2 = allocator.getRegisterForVariable(inputs[1]);
                // WARNING: can we really deallocate here? SSA just means it can't be reassigned, not re-used
                // assigning registers should probably be a more top-down process based on liveness analysis
                allocator.deallocateRegisterForVariable(inputs[1]);
                // after an ADD, the register tied to the left variable needs to shift from left to the result
                allocator.swapVariables(inputs[0], result);
                yield "ADD r" + reg1 + ", r" + reg2;
            }
        };
    }
}
