package nu.henrikvester.haraldlang.codegen.ir.primitives.terminators;

import nu.henrikvester.haraldlang.codegen.ir.Label;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.List;

public record Ret(IRValue value) implements IRTerminator {
    @Override
    public List<IRValue> uses() {
        return List.of();
    }

    @Override
    public List<Label> successors() {
        return List.of();
    }

    @Override
    public String toString() {
        return "ret " + value;
    }
}
