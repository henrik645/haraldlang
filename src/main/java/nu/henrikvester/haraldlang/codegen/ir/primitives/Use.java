package nu.henrikvester.haraldlang.codegen.ir.primitives;

import lombok.Getter;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.IRInst;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

public final class Use {
    @Getter
    private final IRInst owner;
    @Getter
    private final int index;
    @Getter
    private IRValue value;

    public Use(IRInst owner, int index, IRValue value) {
        this.owner = owner;
        this.index = index;
        this.value = value;
        value.addUse(this);
    }

    public void replaceWith(IRValue newVal) {
        if (newVal == value) return;
        value.removeUse(this);
        value = newVal;
        newVal.addUse(this);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
