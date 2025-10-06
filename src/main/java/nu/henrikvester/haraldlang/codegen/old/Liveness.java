package nu.henrikvester.haraldlang.codegen.old;

//public class Liveness {
//    static Map<IRTemp, int[]> liveIntervals(List<IRInst> instructions) {
//        int n = instructions.size();
//        // map from variable id to the index where it is defined
//        Map<IRTemp, Integer> defIndex = new HashMap<>();
//        // map from variable id to the index where it is last used
//        Map<IRTemp, Integer> lastUseIndex = new HashMap<>();
//        for (int codeLocation = 0; codeLocation < n; codeLocation++) {
//            var result = instructions.get(codeLocation).dst();
//            if (result != null) {
//                defIndex.put(result, codeLocation);
//            }
//        }
//
//        for (int codeLocation = n - 1; codeLocation >= 0; codeLocation--) {
//            for (var variableId : instructions.get(codeLocation).temps()) {
//                lastUseIndex.putIfAbsent(variableId, codeLocation);
//            }
//        }
//
//        // map from variable id to its live interval [defIndex, lastUse]
//        Map<IRTemp, int[]> liveIntervals = new TreeMap<>();
//        for (var inst : instructions) {
//            int def = defIndex.get(inst.dst());
//            // default value: last use said to be before definition if never used
//            int lastUse = lastUseIndex.getOrDefault(inst.dst(), def - 1);
//            liveIntervals.put(inst.dst(), new int[]{def, lastUse});
//        }
//        return liveIntervals;
//    }
//
//    static boolean isUsed(int[] interval) {
//        int definedAt = interval[0];
//        int lastUse = interval[1];
//        return lastUse >= definedAt;
//    }
//}
//
