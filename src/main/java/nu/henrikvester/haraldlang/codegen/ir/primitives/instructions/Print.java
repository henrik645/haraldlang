package nu.henrikvester.haraldlang.codegen.ir.primitives.instructions;

import nu.henrikvester.haraldlang.codegen.ir.primitives.Use;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.List;

public final class Print implements IRInst {
    private final Use value;

    public Print(IRValue value) {
        this.value = new Use(this, 0, value);
    }

    public IRValue value() {
        return value.getValue();
    }
    
    @Override
    public String toString() {
        return "print " + value;
    }

    @Override
    public List<Use> operands() {
        return List.of(value);
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
        throw new IllegalStateException("Print does not define a temp");
    }

    @Override
    public List<IRTemp> defs() {
        return List.of();
    }
}
