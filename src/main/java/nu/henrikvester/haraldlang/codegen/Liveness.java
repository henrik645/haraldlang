package nu.henrikvester.haraldlang.codegen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Liveness {
    static Map<Variable, int[]> liveIntervals(List<Instr> instructions) {
        int n = instructions.size();
        // map from variable id to the index where it is defined
        Map<Variable, Integer> defIndex = new HashMap<>();
        // map from variable id to the index where it is last used
        Map<Variable, Integer> lastUseIndex = new HashMap<>();
        for (int codeLocation = 0; codeLocation < n; codeLocation++) {
            defIndex.put(instructions.get(codeLocation).result(), codeLocation);
        }

        for (int codeLocation = n - 1; codeLocation >= 0; codeLocation--) {
            for (var variableId : instructions.get(codeLocation).inputs()) {
                lastUseIndex.putIfAbsent(variableId, codeLocation);
            }
        }

        // map from variable id to its live interval [defIndex, lastUse]
        Map<Variable, int[]> liveIntervals = new TreeMap<>();
        for (var inst : instructions) {
            int def = defIndex.get(inst.result());
            // default value: last use said to be before definition if never used
            int lastUse = lastUseIndex.getOrDefault(inst.result(), def - 1);
            liveIntervals.put(inst.result(), new int[]{def, lastUse});
        }
        return liveIntervals;
    }

    static boolean isUsed(int[] interval) {
        int definedAt = interval[0];
        int lastUse = interval[1];
        return lastUse >= definedAt;
    }
}

