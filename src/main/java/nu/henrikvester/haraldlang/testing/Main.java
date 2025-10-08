package nu.henrikvester.haraldlang.testing;

import java.util.Set;

public class Main {
    public static void main(String[] args) {
        var b1 = new Block("1");
        var b2 = new Block("2");
        var b3 = new Block("3");
        var b4 = new Block("4");

        var x = new Variable("x");

        b1.instructions.add(new Store(new Constant(2), x));
        b2.instructions.add(new Store(new Constant(4), x));
        b3.instructions.add(new Store(new Constant(5), x));
        b4.instructions.add(new Print(x));

        b2.predecessors.add(b1);
        b3.predecessors.add(b1);
        b4.predecessors.add(b2);
        b4.predecessors.add(b3);

        var cfg = new Cfg(Set.of(b1, b2, b3, b4), b1);

        for (var block : cfg.blocks) {
            System.out.println("Block: " + block.label);
//            for (var inst : block.instructions){
//                System.out.println("  " + inst);
//            }
            System.out.println("dominated by: " + cfg.dominatorsFor(block));
        }
    }
}
