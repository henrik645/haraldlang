package nu.henrikvester.haraldlang.codegen.ir;

import nu.henrikvester.haraldlang.codegen.ir.primitives.IRFunction;

public class SSA {
    public void convertToSSA(IRFunction function) {
        for (var block : function.basicBlocks()) {
            // find variable references
            // see if they can be replaced with temps
        }
    }
}
