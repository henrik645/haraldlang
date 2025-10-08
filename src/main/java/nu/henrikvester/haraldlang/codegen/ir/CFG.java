package nu.henrikvester.haraldlang.codegen.ir;

import lombok.Getter;
import nu.henrikvester.haraldlang.codegen.ir.primitives.IRFunction;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;

import java.util.*;

public class CFG {
    private final IRFunction function;
    @Getter
    private final BasicBlock entry;
    private final Map<BasicBlock, Set<BasicBlock>> successors;
    @Getter
    private final Set<BasicBlock> blocks;
    private final Set<BasicBlock> reachable;

    // Block -> the blocks it is dominated by.
    private final Map<BasicBlock, Set<BasicBlock>> isDominatedBy;
    // Block -> the blocks it dominatess.
    private final Map<BasicBlock, Set<BasicBlock>> dominatedChildren;
    private final Map<BasicBlock, BasicBlock> idoms;
    private final Map<BasicBlock, Set<BasicBlock>> dominanceFrontier;
    private int nextTemp;

    public CFG(IRFunction function) {
        this.function = function;
        this.blocks = new LinkedHashSet<>(function.basicBlocks());
        if (blocks.isEmpty()) throw new IllegalArgumentException("Function must have at least one basic block");
        this.entry = blocks.stream().filter(b -> b.getLabel().equals(function.entry())).findFirst().orElseThrow();
        this.successors = computeSuccessors();
        this.reachable = computeReachable();
        this.isDominatedBy = computeDominatedBy();
        this.idoms = computeIdoms();
        this.dominatedChildren = computeDominatedChildren();
        this.dominanceFrontier = computeDominanceFrontier();
        this.nextTemp = function.nextTemp();
    }

    private Map<BasicBlock, Set<BasicBlock>> computeSuccessors() {
        var successors = new LinkedHashMap<BasicBlock, Set<BasicBlock>>();
        for (var block : function.basicBlocks()) {
            successors.put(block, new LinkedHashSet<>());
        }

        for (var successor : function.basicBlocks()) {
            for (var predecessor : successor.getPredecessors()) {
                successors.get(predecessor).add(successor);
            }
        }
        return successors;
    }

    private Set<BasicBlock> computeReachable() {
        var reachable = new LinkedHashSet<BasicBlock>();
        var queue = new ArrayDeque<BasicBlock>();
        queue.add(entry);
        while (!queue.isEmpty()) {
            var block = queue.pop();
            if (reachable.contains(block)) continue;
            reachable.add(block);
            queue.addAll(successorsFor(block));
        }
        return reachable;
    }

    private Map<BasicBlock, Set<BasicBlock>> computeDominatedBy() {
        Map<BasicBlock, Set<BasicBlock>> dominates = new LinkedHashMap<>();
        for (var block : reachable) {
            if (block == entry) {
                dominates.put(block, new LinkedHashSet<>(Set.of(block)));
            } else {
                dominates.put(block, new LinkedHashSet<>(reachable));
            }
        }

        boolean changed;
        do {
            changed = false;
            for (var block : reachable) {
                if (block == entry) continue;
                Set<BasicBlock> inter = new LinkedHashSet<>(reachable); // intersection
                for (var predecessor : block.getPredecessors()) {
                    if (reachable.contains(predecessor)) {
                        inter.retainAll(dominates.get(predecessor));
                    }
                }
                inter.add(block);

                if (!inter.equals(dominates.get(block))) {
                    dominates.put(block, inter);
                    changed = true;
                }
            }
        } while (changed);

        return dominates;
    }

    private Map<BasicBlock, Set<BasicBlock>> computeDominatedChildren() {
        Map<BasicBlock, Set<BasicBlock>> dominates = new LinkedHashMap<>();
        for (var dominator : reachable) {
            dominates.put(dominator, new LinkedHashSet<>());
        }
        for (var child : reachable) {
            var immediateDominator = idoms.get(child);
            if (immediateDominator == child) continue;
            dominates.get(immediateDominator).add(child);
        }
        return dominates;
    }

    private Map<BasicBlock, BasicBlock> computeIdoms() {
        Map<BasicBlock, BasicBlock> idoms = new LinkedHashMap<>();

        idoms.put(entry, entry);
        for (var block : reachable) {
            if (block == entry) continue;

            Set<BasicBlock> strictDominatedBy = new LinkedHashSet<>(isDominatedBy.get(block));
            strictDominatedBy.remove(block);

            BasicBlock immediate = null;
            outer:
            for (var d : strictDominatedBy) {
                for (var e : strictDominatedBy) {
                    if (d == e) continue;
                    if (!isDominatedBy.get(d).contains(e)) {
                        continue outer;
                    }
                }
                immediate = d;
                break;
            }
            idoms.put(block, immediate);
        }

        return idoms;
    }

    private Map<BasicBlock, Set<BasicBlock>> computeDominanceFrontier() {
        Map<BasicBlock, Set<BasicBlock>> frontier = new LinkedHashMap<>();
        for (var block : reachable) {
            frontier.put(block, new LinkedHashSet<>());
        }

        for (var block : reachable) {
            var preds = block.getPredecessors();
            if (preds.size() >= 2) {
                for (var pred : preds) {
                    if (!reachable.contains(pred)) continue; // skip unreachable predecessors
                    var runner = pred;
                    while (runner != idoms.get(block)) {
                        frontier.get(runner).add(block);
                        runner = idoms.get(runner);
                    }
                }
            }
        }

        return frontier;
    }

    public Set<BasicBlock> successorsFor(BasicBlock block) {
        return successors.get(block);
    }

    public Set<BasicBlock> dominanceFrontierFor(BasicBlock block) {
        return dominanceFrontier.get(block);
    }

    public Set<BasicBlock> dominatedChildrenFor(BasicBlock block) {
        return dominatedChildren.get(block);
    }

    public IRTemp nextTemp() {
        return new IRTemp(nextTemp++);
    }

    public Set<BasicBlock> getReachableBlocks() {
        return reachable;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("Function ").append(function.name()).append("\n");
        sb.append("Entry: ").append(entry.getLabel()).append("\n");
        for (var block : blocks) {
            sb.append(block).append("\n");
        }
        return sb.toString();
    }
}
