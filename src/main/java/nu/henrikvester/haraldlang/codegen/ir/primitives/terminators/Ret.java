package nu.henrikvester.haraldlang.codegen.ir.primitives.terminators;

import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.List;

public record Ret(IRValue value) implements IRTerminator {
    @Override
    public List<IRValue> inputs() {
        return List.of();
    }
}
