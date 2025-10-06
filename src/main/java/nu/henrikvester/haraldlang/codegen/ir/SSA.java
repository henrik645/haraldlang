package nu.henrikvester.haraldlang.codegen.ir;

import nu.henrikvester.haraldlang.codegen.ir.primitives.IRFunction;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.Load;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.Phi;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.Store;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRFrameSlot;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.*;
import java.util.stream.Collectors;

public class SSA {
    private final IRFunction function;
    private final Bindings bindings;
    private int nextTemp;

    public SSA(IRFunction function, Bindings bindings) {
        this.function = function;
        this.bindings = bindings;
        this.nextTemp = function.nextTemp();
    }

    public void convertToSSA() {
        System.out.println(bindings);

        System.out.println("Promotable slots:");
        findPromotableSlots(function).forEach(System.out::println);


        var promotable = findPromotableSlots(function);
        if (promotable.isEmpty()) return;


        var defBlocks = computeDefBlocks(function, promotable);
        System.out.println("\nDef-blocks:");
        for (var e : defBlocks.entrySet()) {
            var slot = e.getKey();
            var blocks = e.getValue();
            System.out.println(slot + " defined in blocks: " + blocks.stream().map(BasicBlock::getLabel).toList());
        }

        var dominators = Dominators.compute(function);
        System.out.println("\nDominance frontier:");
        for (var dominator : dominators.domTree.entrySet()) {
            var node = dominator.getKey();
            var children = dominator.getValue();
            System.out.println(node.getLabel() + " loses dominance at " + children.stream().map(BasicBlock::getLabel).toList());
        }

        System.out.println("\nImmediate dominators:");
        for (var e : dominators.idom.entrySet()) {
            var node = e.getKey();
            var dominator = e.getValue();
            if (dominator == null) {
                System.out.println(node.getLabel() + " is the entry block");
            } else {
                System.out.println(node.getLabel() + " is immediately dominated by " + dominator.getLabel());
            }
        }

        System.out.println("\nPlacing phis:");
        Map<BasicBlock, Map<IRFrameSlot, Phi>> result = placePhis(promotable, defBlocks, dominators.domTree());
        for (var e : result.entrySet()) {
            var block = e.getKey();
            var phiMap = e.getValue();
            for (Map.Entry<IRFrameSlot, Phi> e2 : phiMap.entrySet()) {
                var slot = e2.getKey();
                var phi = e2.getValue();
                System.out.println("Placing phi for " + slot + " in block " + block.getLabel() + ": " + phi);
            }
        }
    }

    private Set<IRFrameSlot> findPromotableSlots(IRFunction function) {
        Map<IRFrameSlot, Boolean> ok = new HashMap<>();

        for (var block : function.basicBlocks()) {
            for (var inst : block.getInstructions()) {
                if (inst instanceof Store store) {
                    var frame = store.dst();
                    ok.merge(frame, true, Boolean::logicalAnd);
                } else if (inst instanceof Load load) {
                    var frame = load.src();
                    ok.merge(frame, true, Boolean::logicalAnd);
                } else {
                    for (var v : inst.uses()) {
                        // any other instruction that directly uses a frame slot is not promotable
                        if (v instanceof IRFrameSlot slot) {
                            ok.put(slot, false);
                        }
                    }
                }
            }
        }

        return ok.entrySet().stream().filter(e -> Boolean.TRUE.equals(e.getValue())).map(Map.Entry::getKey).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Returns a map from a frame slot to the set of basic blocks that define it.
     *
     * @param function the function to compute the def-blocks for
     * @param vars     the set of frame slots to compute the def-blocks for
     * @return a map from a frame slot to the set of basic blocks that define it
     */
    private Map<IRFrameSlot, Set<BasicBlock>> computeDefBlocks(IRFunction function, Set<IRFrameSlot> vars) {
        var defs = new HashMap<IRFrameSlot, Set<BasicBlock>>();
        for (var v : vars) {
            defs.put(v, new LinkedHashSet<>());
        }
        for (var block : function.basicBlocks()) {
            for (var inst : block.getInstructions()) {
                if (inst instanceof Store store && vars.contains(store.dst())) {
                    defs.get(store.dst()).add(block);
                }
            }
        }
        return defs;
    }

    /**
     * Places phis in the basic blocks
     *
     * @param vars               the set of frame slots to place phis for
     * @param defBlocks          the def-blocks for the frame slots
     * @param dominanceFrontiers the dominance frontiers for the basic blocks
     * @return a map from a basic block to a map from a frame slot to a phi instruction
     */
    private Map<BasicBlock, Map<IRFrameSlot, Phi>> placePhis
    (Set<IRFrameSlot> vars,
     Map<IRFrameSlot, Set<BasicBlock>> defBlocks,
     Map<BasicBlock, Set<BasicBlock>> dominanceFrontiers) {
        var placed = new HashMap<BasicBlock, Map<IRFrameSlot, Phi>>();
        for (var v : vars) {
            var W = new ArrayDeque<>(defBlocks.get(v));
            while (!W.isEmpty()) {
                // X is each block that defines v, and that we need to consider
                var X = W.pop();
                var Y = dominanceFrontiers.get(X);
                for (var y : Y) {
                    if (!phiPresent(placed, y, v)) {
                        var temp = nextTemp();
                        var phi = new Phi(temp);
                        // place placeholder, empty phi in the block
                        placed.computeIfAbsent(y, k -> new HashMap<>()).put(v, phi);
                    }
                    if (!defBlocks.get(v).contains(y)) {
                        // Y didn't already define v
                        // push Y to W (because phi is now a new def site)
                        W.push(y);
                    }
                }
            }
        }

        return placed;
    }

    private IRTemp nextTemp() {
        return new IRTemp(nextTemp++);
    }

    static class Rename {
        private final Set<IRFrameSlot> promotables;
        Map<IRFrameSlot, Deque<IRValue>> stack = new HashMap<>();

        Rename(Set<IRFrameSlot> promotables) {
            this.promotables = promotables;

            for (var p : promotables) {
                stack.put(p, new ArrayDeque<>());
            }
        }

        void rename(BasicBlock block) {
            Map<IRFrameSlot, Integer> depth = depthSnapshot(stack);
        }
    }

    private boolean phiPresent(Map<BasicBlock, Map<IRFrameSlot, Phi>> placed, BasicBlock block, IRFrameSlot v) {
        return placed.containsKey(block) && placed.get(block).containsKey(v);
    }

    private record Dominators(Map<BasicBlock, BasicBlock> idom, Map<BasicBlock, Set<BasicBlock>> domTree) {

        static Map<BasicBlock, BasicBlock> computeIdoms(IRFunction function) {
                var blocks = function.basicBlocks();
                if (blocks.isEmpty()) return Map.of();
                BasicBlock entry = entry(function);
    
                Map<BasicBlock, List<BasicBlock>> succ = buildSuccessors(blocks);
    
                Set<BasicBlock> V = reachable(entry, succ);
    
                Map<BasicBlock, Set<BasicBlock>> dom = new LinkedHashMap<>();
                for (var block : V) {
                    if (block == entry) {
                        dom.put(block, new LinkedHashSet<>(Set.of(block)));
                    } else {
                        dom.put(block, new LinkedHashSet<>(V));
                    }
                }
    
                boolean changed;
                do {
                    changed = false;
                    for (var block : V) {
                        if (block == entry) continue;
                        Set<BasicBlock> inter = new LinkedHashSet<>(V);
                        for (var p : block.getPredecessors()) {
                            if (V.contains(p)) {
                                inter.retainAll(dom.get(p));
                            }
                        }
                        inter.add(block);
    
                        if (!inter.equals(dom.get(block))) {
                            dom.put(block, inter);
                            changed = true;
                        }
                    }
                } while (changed);
    
                Map<BasicBlock, BasicBlock> idom = new LinkedHashMap<>();
                idom.put(entry, entry);
    
                for (var b : V) {
                    if (b == entry) continue;
                    Set<BasicBlock> strict = new LinkedHashSet<>(dom.get(b));
                    strict.remove(b);
    
                    BasicBlock imm = null;
                    outer:
                    for (var d : strict) {
                        for (var e : strict) {
                            if (e == d) {
                                continue;
                            }
                            if (!dom.get(d).contains(e)) {
                                continue outer;
                            }
                        }
                        imm = d;
                        break;
                    }
                    idom.put(b, imm);
                }
    
                return idom;
            }
    
            private static Map<BasicBlock, List<BasicBlock>> buildSuccessors(List<BasicBlock> blocks) {
                Map<BasicBlock, List<BasicBlock>> succ = new LinkedHashMap<>();
                for (var block : blocks) {
                    succ.putIfAbsent(block, new ArrayList<>());
                }
                for (var to : blocks) {
                    for (var from : blocks) {
                        succ.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
                    }
                }
                return succ;
            }
    
            private static Set<BasicBlock> reachable(BasicBlock entry, Map<BasicBlock, List<BasicBlock>> succ) {
                Set<BasicBlock> seen = new HashSet<>();
                Deque<BasicBlock> st = new ArrayDeque<>();
                st.push(entry);
                while (!st.isEmpty()) {
                    var block = st.pop();
                    if (!seen.add(block)) continue;
                    for (var s : succ.getOrDefault(block, List.of())) {
                        st.push(s);
                    }
                }
                return seen;
            }
    
            static Dominators compute(IRFunction function) {
                // gets the immediate dominator of a block
                Map<BasicBlock, BasicBlock> idom = computeIdoms(function);
                // set of nodes where a basic block's dominance stops
                // i.e., where we need to insert phis
                Map<BasicBlock, Set<BasicBlock>> frontier = new HashMap<>();
    
                for (var block : function.basicBlocks()) {
                    frontier.put(block, new HashSet<>());
                }
                for (var block : function.basicBlocks()) {
                    var preds = block.getPredecessors();
                    if (preds.size() >= 2) {
                        for (var pred : preds) {
                            var runner = pred;
                            while (runner != idom.get(block)) {
                                frontier.get(runner).add(block);
                                runner = idom.get(runner);
                            }
                        }
                    }
                }
    
                return new Dominators(idom, frontier);
            }
    
            private static BasicBlock entry(IRFunction function) {
                return function.basicBlocks().stream().filter(b -> b.getLabel().equals(function.entry())).findFirst().orElseThrow();
            }
    
            public @Override String toString() {
                return "Dominators{" +
                        "idom=" + idom +
                        ", domTree=" + domTree +
                        '}';
            }
        }
}
