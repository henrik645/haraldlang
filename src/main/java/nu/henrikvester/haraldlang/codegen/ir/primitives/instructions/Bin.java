package nu.henrikvester.haraldlang.codegen.ir.primitives.instructions;

import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.List;

public record Bin(IRTemp dst, BinOp op, IRValue lhs, IRValue rhs) implements IRInst {
    @Override
    public List<IRValue> inputs() {
        return List.of(lhs, rhs);
    }
}
