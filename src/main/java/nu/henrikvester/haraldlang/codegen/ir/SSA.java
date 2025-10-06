package nu.henrikvester.haraldlang.codegen.ir;

import nu.henrikvester.haraldlang.codegen.ir.primitives.IRFunction;

public class SSA {
    public void convertToSSA(IRFunction function, Bindings bindings) {
        System.out.println(bindings);
        for (var block : function.basicBlocks()) {
            System.out.println(block.getLabel() + ":");
            block.getInstructions().forEach(i -> System.out.println("    " + i));
            System.out.println("    " + block.getTerminator());
            System.out.println();
        }
    }
}
