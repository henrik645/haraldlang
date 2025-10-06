package nu.henrikvester.haraldlang.codegen.ir.primitives.instructions;

import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRFrameSlot;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.List;

public record Load(IRTemp dst, IRFrameSlot src) implements IRInst {
    @Override
    public String toString() {
        return "load " + dst + " <- " + src;
    }

    @Override
    public List<IRValue> uses() {
        return List.of(src);
    }

    @Override
    public List<IRTemp> defs() {
        return List.of(dst);
    }
}
