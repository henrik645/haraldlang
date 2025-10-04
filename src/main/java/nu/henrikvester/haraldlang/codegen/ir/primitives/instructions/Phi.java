package nu.henrikvester.haraldlang.codegen.ir.primitives.instructions;

import nu.henrikvester.haraldlang.codegen.ir.Label;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record Phi(IRTemp dst, Map<Label, IRValue> incomings) implements IRInst {
    @Override
    public List<IRValue> inputs() {
        return new ArrayList<>(incomings.values());
    }
}
