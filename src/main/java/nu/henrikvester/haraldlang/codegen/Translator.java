package nu.henrikvester.haraldlang.codegen;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides block functionality while keeping track of variable names in the current scope
 */
public class Translator {
    private final Block block = new Block();
    // map variable name in current scope (String) to SSA variable id (Variable)
    private final Map<String, Variable> env = new HashMap<>();
    private final RegisterAllocator registerAllocator = new RegisterAllocator();

    public Map<String, Variable> getEnv() {
        return env;
    }

    public Block getBlock() {
        return block;
    }

    public RegisterAllocator getRegisterAllocator() {
        return registerAllocator;
    }

    public Variable emitConst(int value) {
        return block.emitConst(value);
    }

    public Variable emitAdd(Variable left, Variable right) {
        return block.emitAdd(left, right);
    }

    public void setVariable(String name, Variable variable) {
        env.put(name, variable);
        // TODO: should we emit some instructions here?
//            block.emitAssignment(name, variable);
    }

    public Variable getVariable(String name) {
        return env.get(name);
    }

    public void printDebugInfo() {
        var code = getBlock().getInstructions();

        System.out.println("IR: ");
        for (int i = 0; i < code.size(); i++) {
            System.out.printf("%-3d : %s%n", i, code.get(i));
        }

        var intervals = Liveness.liveIntervals(code);
        System.out.println();
        System.out.println("Live intervals:");
        System.out.println("id    def, last");
        for (var entry : intervals.entrySet()) {
            var id = entry.getKey().id();
            int[] interval = entry.getValue();
            boolean used = Liveness.isUsed(interval);
            if (used) {
                System.out.printf("v%-2d : [%d, %d]%n", id, interval[0], interval[1]);
                var registerNbr = registerAllocator.allocateRegister(entry.getKey());
//                System.out.println("Allocating r" + registerNbr + " to " + id);
            } else {
                System.out.printf("v%-2d : [%d, %d] (never used)%n", id, interval[0], interval[1]);
            }
        }

        System.out.println();
        System.out.println("Live sets per index:");
        for (int i = 0; i < code.size(); i++) {
            var codeLocation = i;
            var live = intervals.entrySet().stream()
                    .filter(e -> e.getValue()[0] <= codeLocation && codeLocation <= e.getValue()[1])
                    .map(e -> e.getKey().toString())
                    .toList();
            var liveStr = String.join(", ", live);
            var registerReport = live.size() > 4 ? " (memory needed)" : "";
            System.out.printf("    after %d: %s%s%n", i, liveStr, registerReport);
        }

        System.out.println();
        System.out.println("Bound variables:");
        for (var e : env.entrySet()) {
            System.out.println(e.getKey() + " = " + e.getValue());
        }

        System.out.println();
        System.out.println("Generated ASM:");
        for (var instruction : code) {
            System.out.println(instruction.mnemonic(registerAllocator));
        }

        System.out.println();
        System.out.println("Allocated registers:");
        for (var e : registerAllocator.getAllocations().entrySet()) {
            System.out.println("r" + e.getKey() + " -> " + e.getValue());
        }

        if (registerAllocator.hasAllocations()) {
            System.out.println();
            System.out.println("Warning: Not all registers have been freed!");
            for (var register : registerAllocator.getAllocations().keySet()) {
                System.out.println("r" + register + " still allocated");
            }
        }
    }
}
