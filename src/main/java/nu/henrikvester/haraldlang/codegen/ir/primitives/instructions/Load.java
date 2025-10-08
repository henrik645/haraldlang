package nu.henrikvester.haraldlang.codegen.ir.primitives.instructions;

import nu.henrikvester.haraldlang.codegen.ir.primitives.Use;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRFrameSlot;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.List;

public final class Load implements IRInst {
    private final Use src;
    private IRTemp dst;

    public Load(IRTemp dst, IRFrameSlot src) {
        this.dst = dst;
        this.src = new Use(this, 0, src);
    }

    public IRTemp dst() {
        return dst;
    }

    public IRFrameSlot src() {
        IRValue value = src.getValue();
        if (!(value instanceof IRFrameSlot slot)) {
            throw new IllegalStateException("Expected IRFrameSlot, got " + value.getClass().getSimpleName());
        }
        return slot;
    }

    @Override
    public String toString() {
        return "load " + dst + " <- " + src;
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
