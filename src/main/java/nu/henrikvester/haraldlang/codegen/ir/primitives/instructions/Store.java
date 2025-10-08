package nu.henrikvester.haraldlang.codegen.ir.primitives.instructions;

import nu.henrikvester.haraldlang.codegen.ir.primitives.Use;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRFrameSlot;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.List;

public final class Store implements IRInst {
    private final IRFrameSlot dst;
    private final Use src;

    public Store(IRFrameSlot dst, IRValue src) {
        this.dst = dst;
        this.src = new Use(this, 0, src);
    }

    public IRFrameSlot dst() {
        return dst;
    }

    public IRValue src() {
        return src.getValue();
    }

    @Override
    public String toString() {
        return "store " + src + " -> " + dst;
    }

    @Override
    public List<Use> operands() {
        return List.of(src);
    }

    @Override
    public boolean definesTemp() {
        return false;
    }

    @Override
    public IRTemp definedTemp() {
        return null;
    }

    @Override
    public void setResult(IRTemp temp) {
        throw new IllegalStateException("Store does not define a temp");
    }

    @Override
    public List<IRTemp> defs() {
        return List.of();
    }
}
