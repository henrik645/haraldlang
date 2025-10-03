package nu.henrikvester.haraldlang.codegen;

import java.util.Map;
import java.util.TreeMap;

public class RegisterAllocator {
    private final static int NUM_REGISTERS = 4;
    // Map which register number currently contains which SSA variable
    private final Map<Integer, Variable> allocations = new TreeMap<>();

    public Map<Integer, Variable> getAllocations() {
        return allocations;
    }

    /**
     * Allocates a register for an SSA variable.
     *
     * @return the register number.
     */
    public int allocateRegister(Variable variable) {
        for (int reg = 0; reg < NUM_REGISTERS; reg++) {
            if (!allocations.containsKey(reg)) {
                allocations.put(reg, variable);
                return reg;
            }
        }

        throw new IllegalStateException("No more registers available");
    }

    public int getRegisterForVariable(Variable variable) {
        for (var entry : allocations.entrySet()) {
            if (entry.getValue().equals(variable)) {
                return entry.getKey();
            }
        }
        throw new IllegalStateException("Variable " + variable + " not allocated to any register");
    }

    /**
     * Swaps the register allocation from oldVar to newVar.
     *
     * @param oldVar the old SSA variable to swap out.
     * @param newVar the new SSA variable to swap in.
     */
    public void swapVariables(Variable oldVar, Variable newVar) {
        int reg = getRegisterForVariable(oldVar);
        allocations.put(reg, newVar);
    }

    /**
     * Deallocates the register for the given SSA variable.
     *
     * @param variable the SSA variable to deallocate the register for.
     */
    public void deallocateRegisterForVariable(Variable variable) {
        allocations.values().removeIf(v -> v.equals(variable));
    }

    public boolean hasAllocations() {
        return !allocations.isEmpty();
    }
}
