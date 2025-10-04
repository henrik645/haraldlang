package nu.henrikvester.haraldlang.codegen.ir.primitives.instructions;

import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.List;

public record Print(IRValue value) implements IRInst {
    @Override
    public IRTemp dst() {
        return null;
    }

    @Override
    public List<IRValue> inputs() {
        return List.of(value);
    }
}
