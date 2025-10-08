package nu.henrikvester.haraldlang.codegen.ir.primitives.instructions;

import nu.henrikvester.haraldlang.codegen.ir.primitives.Use;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.List;

public final class Bin implements IRInst {
    private final BinOp op;
    private final Use lhs;
    private final Use rhs;
    private IRTemp dst;

    public Bin(IRTemp dst, BinOp op, IRValue lhs, IRValue rhs) {
        this.dst = dst;
        this.op = op;
        this.lhs = new Use(this, 0, lhs);
        this.rhs = new Use(this, 1, rhs);
    }

    public IRTemp dst() {
        return dst;
    }

    public BinOp op() {
        return op;
    }

    public IRValue lhs() {
        return lhs.getValue();
    }

    public IRValue rhs() {
        return rhs.getValue();
    }

    @Override
    public String toString() {
        return dst + " <- " + lhs + " " + op + " " + rhs;
    }

    @Override
    public List<Use> operands() {
        return List.of(lhs, rhs);
    }

    @Override
    public boolean definesTemp() {
        return true;
    }

    @Override
    public IRTemp definedTemp() {
        return dst;
    }

    @Override
    public void setResult(IRTemp temp) {
        this.dst = temp;
    }

    @Override
    public List<IRTemp> defs() {
        return List.of(dst);
    }
}