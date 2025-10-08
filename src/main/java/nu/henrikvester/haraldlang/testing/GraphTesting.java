package nu.henrikvester.haraldlang.testing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.*;
import java.util.stream.Collectors;

sealed interface MyInstruction {
}

sealed interface MyTerminator extends MyInstruction {
    Set<String> targets();
}

sealed interface MyValue {
}

sealed interface MyLValue extends MyValue { // target of a move
}

record MyMov(MyLValue destination, MyValue source) implements MyInstruction {
}

record Add(MyLValue destination, MyValue left, MyValue right) implements MyInstruction {
}

record MyPrint(MyValue value) implements MyInstruction {
}

record Jmp(String target) implements MyTerminator {
    @Override
    public Set<String> targets() {
        return Set.of(target);
    }
}

record Ret() implements MyTerminator {
    @Override
    public Set<String> targets() {
        return Set.of();
    }
}

record BranchIfZero(MyValue condition, String targetIfZero, String targetIfNotZero) implements MyTerminator {
    @Override
    public Set<String> targets() {
        return Set.of(targetIfZero, targetIfNotZero);
    }
}

record CodeVariable(String name) implements MyLValue {
}

record IRRegister(int number) implements MyLValue {
    static int nextTemp = 0;

    public IRRegister() {
        this(nextTemp++);
    }
}

record MyConstant(int value) implements MyValue {
}

@RequiredArgsConstructor
class MyBlock {
    @Getter
    private final String label;
    @Getter
    private final List<MyBlock> predecessors = new ArrayList<>();
    private final List<MyInstruction> instructions = new ArrayList<>();
    @Getter
    private MyTerminator terminator;

    public void emit(MyInstruction instruction) {
        if (instruction instanceof MyTerminator t) {
            this.terminator = t;
        } else {
            if (terminator != null) {
                throw new IllegalStateException("Terminator already set");
            }
            instructions.add(instruction);
        }
    }

    public void addPredecessor(MyBlock block) {
        predecessors.add(block);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyBlock myBlock = (MyBlock) o;
        return label.equals(myBlock.label);
    }

    @Override
    public int hashCode() {
        return label.hashCode();
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append(label).append(":\n");
        for (var i : instructions) {
            sb.append("  ").append(i).append("\n");
        }
        sb.append("  ").append(terminator).append("\n");
        return sb.toString();
    }
}

@RequiredArgsConstructor
class Function {
    private final String name;
    @Getter
    private final Map<String, MyBlock> blocks = new LinkedHashMap<>();
    private MyBlock currentBlock;
    @Getter
    private MyBlock entryBlock;

    public void mark(String label) {
        currentBlock = blocks.computeIfAbsent(label, MyBlock::new);
        if (entryBlock == null) entryBlock = currentBlock;
    }

    public MyBlock getBlock(String label) {
        return blocks.get(label);
    }

    public void emit(MyInstruction instruction) {
        currentBlock.emit(instruction);
    }

    public void finish() {
        for (var block : blocks.values()) {
            var term = block.getTerminator();
            if (term == null) {
                throw new IllegalStateException("Block " + block.getLabel() + " has no terminator");
            }
            term.targets().stream().map(blocks::get).forEach(target -> target.addPredecessor(block));
        }
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append(name).append(":\n");
        for (var b : blocks.values()) {
            sb.append(b).append("\n");
        }
        return sb.toString();
    }
}

@RequiredArgsConstructor
class CFG {
    private final Function function;

    @Getter
    private final Graph<MyBlock, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);

    public void calculate() {
        for (var block : function.getBlocks().values()) {
            graph.addVertex(block);
        }
        for (var block : function.getBlocks().values()) {
            for (var pred : block.getPredecessors()) {
                graph.addEdge(pred, block);
            }
        }
    }

    public void traverseGraph() {
        var iterator = new DepthFirstIterator<>(graph, function.getEntryBlock());
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

    public Set<MyBlock> getSuccessors(MyBlock block) {
        var ret = new LinkedHashSet<MyBlock>();
        var it = new DepthFirstIterator<>(graph, block);
        while (it.hasNext()) {
            ret.add(it.next());
        }
        ret.remove(block);
        return ret;
    }
}

public class GraphTesting {
    public static void main(String[] args) {
        Function f = new Function("main");
        f.mark("entry");
        var x = new CodeVariable("x");
        var y = new CodeVariable("x");
        var t1 = new IRRegister();
        f.emit(new Add(x, new MyConstant(5), t1));
        var t2 = new IRRegister();
        f.emit(new Add(y, t1, t2));
        f.emit(new MyPrint(t2));
        var t3 = new IRRegister();
        f.emit(new BranchIfZero(t2, "if_zero", "if_not_zero"));
        f.mark("if_zero");
        f.emit(new MyMov(y, new MyConstant(1)));
        f.emit(new Jmp("end"));
        f.mark("if_not_zero");
        f.emit(new MyMov(y, new MyConstant(6)));
        f.emit(new Jmp("end"));
        f.mark("end");
        f.emit(new Ret());

        f.finish();

        var cfg = new CFG(f);
        cfg.calculate();
        cfg.traverseGraph();

        System.out.println(cfg.getGraph());

        for (var block : f.getBlocks().values()) {
            System.out.println("Successors of: " + block.getLabel());
            for (var s : cfg.getSuccessors(block)) {
                System.out.println("  " + s.getLabel());
            }
            System.out.println();
        }

        var insp = new ConnectivityInspector<>(cfg.getGraph());
        for (var block : f.getBlocks().values()) {
            var connectedComponents = insp.connectedSetOf(block);
            System.out.println("Connected component of " + block.getLabel() + ": " + connectedComponents.stream().map(MyBlock::getLabel).collect(Collectors.joining(", ")));
        }
    }
}
