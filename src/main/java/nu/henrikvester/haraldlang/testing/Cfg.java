package nu.henrikvester.haraldlang.testing;

import nu.henrikvester.haraldlang.exceptions.NotImplementedException;

import java.util.LinkedHashSet;
import java.util.Set;

class Cfg {
    Set<Block> blocks;
    Block entry;

    Cfg(Set<Block> blocks, Block entry) {
        if (!blocks.contains(entry)) throw new IllegalArgumentException("Entry block must be in blocks");
        this.blocks = new LinkedHashSet<>(blocks);
        this.entry = entry;
    }

    Block idom(Block block) {
        throw new NotImplementedException();
    }

    Set<Block> dominatorsFor(Block block) {
        if (block.equals(entry)) return Set.of(block);
        if (block.predecessors.isEmpty()) return Set.of(block);
        Set<Block> doms = new LinkedHashSet<>(dominatorsFor(block.predecessors.stream().findFirst().orElseThrow()));
        for (var pred : block.predecessors) {
            doms.retainAll(dominatorsFor(pred)); // intersection
        }
        doms.add(block); // finally, add this block
        return doms;
    }

    Set<Block> domFrontier(Block block) {
        throw new NotImplementedException();
    }

    Set<Variable> defs(Block block) {
        throw new NotImplementedException();
    }

    Set<Block> definedIn(Variable variable) {
        throw new NotImplementedException();
    }

    Set<Block> uses(Variable variable) {
        throw new NotImplementedException();
    }
}
