package nu.henrikvester.haraldlang.codegen;

import java.util.*;

public class IntermediateInstruction {

}

class SSA {
    enum Op { CONST, ADD }

    record Instr(int id, Op op, int[] uses, Integer imm) {
        @Override
            public String toString() {
                return switch (op) {
                    case CONST -> "v" + id + " = " + imm;
                    case ADD -> "v" + id + " = v" + uses[0] + " + v" + uses[1];
                };
            }
        }
    
    static final class Block {
        private final List<Instr> instructions = new ArrayList<>();
        private int nextId = 0;
        
        int emitConst(int k) {
            int id = nextId++;
            instructions.add(new Instr(id, Op.CONST, new int[0], k));
            return id;
        }
        
        int emitAdd(int a, int b) {
            int id = nextId++;
            instructions.add(new Instr(id, Op.ADD, new int[]{a, b}, null));
            return id;
        }
        
        List<Instr> getInstructions() {
            return instructions;
        }
    }
    
    static final class Translator {
        final Block block = new Block();
        final Map<String, Integer> env = new HashMap<>();
        
        void letConst(String name, int k) {
            int v = block.emitConst(k);
            env.put(name, v);
        }
        
        void letAdd(String name, String lhsVar, int rhsConst) {
            var lhs = env.get(lhsVar);
            int c = block.emitConst(rhsConst);
            int sum = block.emitAdd(lhs, c);
            env.put(name, sum);
        }
    }
    
    static final class Liveness {
        static Map<Integer, int[]> liveIntervals(List<Instr> code) {
            int n = code.size();
            // map from variable id to the index where it is defined
            Map<Integer, Integer> defIndex = new HashMap<>();
            // map from variable id to the index where it is last used
            Map<Integer, Integer> lastUseIndex = new HashMap<>();
            for (int codeLocation = 0; codeLocation < n; codeLocation++) {
                defIndex.put(code.get(codeLocation).id, codeLocation);
            }
            
            for (int codeLocation = n - 1; codeLocation >= 0; codeLocation--) {
                for (int variableId : code.get(codeLocation).uses) {
                    lastUseIndex.putIfAbsent(variableId, codeLocation);
                }
            }
            
            // map from variable id to its live interval [defIndex, lastUse]
            Map<Integer, int[]> liveIntervals = new TreeMap<>();
            for (var inst: code) {
                int def = defIndex.get(inst.id);
                // default value: last use said to be before definition if never used
                int lastUse = lastUseIndex.getOrDefault(inst.id, def - 1);
                liveIntervals.put(inst.id, new int[]{def, lastUse});
            }
            return liveIntervals;
        }
        
        static boolean isUsed(int[] interval) {
            int definedAt = interval[0];
            int lastUse = interval[1];
            return lastUse >= definedAt;
        }
    }
    
    public static void main(String[] args) {
        var tr = new Translator();
        
        tr.letConst("x", 5);
        tr.letConst("y", 10);
        tr.letAdd("y", "x", 10);
        
        var code = tr.block.instructions;
        
        System.out.println("IR: ");
        for (int i = 0; i < code.size(); i++) {
            System.out.printf("%-3d : %s%n", i, code.get(i));
        }

        var intervals = Liveness.liveIntervals(code);
        System.out.println("Live intervals:");
        System.out.println("id    def, last");
        for (var entry : intervals.entrySet()) {
            int id = entry.getKey();
            int[] interval = entry.getValue();
            boolean used = Liveness.isUsed(interval);
            if (used) {
                System.out.printf("v%-2d : [%d, %d]%n", id, interval[0], interval[1]);
            } else {
                System.out.printf("v%-2d : [%d, %d] (never used)%n", id, interval[0], interval[1]);
            }
        }
    }
}

/**
 * Defines a register or memory location
 * Register allocation ties one of these to a real register or, in the case of memory location,
 * a load plus a register.
 */
class Register {
}

/**
 * Defines a location in the code, for jumps and branches.
 */
class Location {
}

record Add(Register left, Register right) {
}

record Sub(Register left, Register right) {
}

record Load(Register dest, Register address) {
}

record Store(Register src, Register address) {
}

record LoadImmediate(Register dest, int value) {
}

record ShiftLeft(Register x) {
}

record Increment(Register x) {
}

record Decrement(Register x) {
}

record And(Register left, Register right) {
}

record Or(Register left, Register right) {
}

record Xor(Register left, Register right) {
}

record JumpIfZero(Register condition, Location location) {
}

record JumpIfNotZero(Register condition, Location location) {
}

record Jump(Location location) {
}

record Halt(Location location) {
}

record Noop() {
}
