package nu.henrikvester.haraldlang.codegen.ir.primitives.terminators;

import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.List;

public record RetVoid() implements IRTerminator {
    @Override
    public List<IRValue> inputs() {
        return List.of();
    }
}
