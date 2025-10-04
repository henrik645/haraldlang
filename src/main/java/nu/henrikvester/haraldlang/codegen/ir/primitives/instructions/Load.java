package nu.henrikvester.haraldlang.codegen.ir.primitives.instructions;

import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.List;

public record Load(IRTemp dst, IRValue addr) implements IRInst {
    @Override
    public List<IRValue> inputs() {
        return List.of(addr);
    }
}
