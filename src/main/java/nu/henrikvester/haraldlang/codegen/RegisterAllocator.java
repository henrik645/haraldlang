package nu.henrikvester.haraldlang.codegen;

import nu.henrikvester.haraldlang.codegen.ir.IRTemp;

import java.util.Map;
import java.util.TreeMap;

public class RegisterAllocator {
    private final static int NUM_REGISTERS = 4;
    // Map which register number currently contains which SSA variable
    private final Map<Integer, IRTemp> allocations = new TreeMap<>();

    public Map<Integer, IRTemp> getAllocations() {
        return allocations;
    }

    /**
     * Allocates a register for an SSA variable.
     *
     * @return the register number.
     */
    public int allocateRegister(IRTemp temp) {
        for (int reg = 0; reg < NUM_REGISTERS; reg++) {
            if (!allocations.containsKey(reg)) {
                allocations.put(reg, temp);
                return reg;
            }
        }

        throw new IllegalStateException("No more registers available");
    }

    public int getRegisterForVariable(IRTemp temp) {
        for (var entry : allocations.entrySet()) {
            if (entry.getValue().equals(temp)) {
                return entry.getKey();
            }
        }
        throw new IllegalStateException("Variable " + temp + " not allocated to any register");
    }

    /**
     * Swaps the register allocation from oldVar to newVar.
     *
     * @param oldVar the old SSA variable to swap out.
     * @param newVar the new SSA variable to swap in.
     */
    public void swapVariables(IRTemp oldVar, IRTemp newVar) {
        int reg = getRegisterForVariable(oldVar);
        allocations.put(reg, newVar);
    }

    /**
     * Deallocates the register for the given SSA variable.
     *
     * @param temp the SSA variable to deallocate the register for.
     */
    public void deallocateRegisterForVariable(IRTemp temp) {
        allocations.values().removeIf(v -> v.equals(temp));
    }

    public boolean hasAllocations() {
        return !allocations.isEmpty();
    }
}
