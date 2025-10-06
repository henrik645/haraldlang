package nu.henrikvester.haraldlang.codegen.ir.primitives.instructions;

import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.List;

public record Print(IRValue value) implements IRInst {
    @Override
    public String toString() {
        return "print " + value;
    }

    @Override
    public List<IRValue> uses() {
        return List.of(value);
    }

    @Override
    public List<IRTemp> defs() {
        return List.of();
    }
}
