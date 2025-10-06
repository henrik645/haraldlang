package nu.henrikvester.haraldlang.codegen.ir.primitives;

import nu.henrikvester.haraldlang.codegen.ir.BasicBlock;
import nu.henrikvester.haraldlang.codegen.ir.Label;

import java.util.List;

// TODO: nextTemp should not be passed like this
public record IRFunction(String name, Label entry, List<BasicBlock> basicBlocks, int nextTemp) {
    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("function ").append(name).append(" {\n");
        for (var bb : basicBlocks) {
            sb.append(bb).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
