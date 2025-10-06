package nu.henrikvester.haraldlang.codegen.ir.primitives.instructions;

import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.List;

public record Bin(IRTemp dst, BinOp op, IRValue lhs, IRValue rhs) implements IRInst {
    @Override
    public String toString() {
        return dst + " <- " + lhs + " " + op + " " + rhs;
    }

    @Override
    public List<IRValue> uses() {
        return List.of(lhs, rhs);
    }

    @Override
    public List<IRTemp> defs() {
        return List.of(dst);
    }
}
