package nu.henrikvester.haraldlang.codegen.ir.primitives.instructions;

import nu.henrikvester.haraldlang.codegen.ir.primitives.Use;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.List;

public final class Mov implements IRInst {
    private final Use src;
    private IRTemp dst;

    public Mov(IRTemp dst, IRValue src) {
        this.dst = dst;
        this.src = new Use(this, 0, src);
    }

    public IRValue dst() {
        return dst;
    }

    public IRValue src() {
        return src.getValue();
    }
    @Override
    public String toString() {
        return dst + " <- " + src;
    }

    @Override
    public List<Use> operands() {
        return List.of(src);
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
