package nu.henrikvester.haraldlang.codegen.ir.primitives.instructions;

import nu.henrikvester.haraldlang.codegen.ir.Label;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record Phi(IRTemp dst, Map<Label, IRTemp> incomings) implements IRInst {
    @Override
    public String toString() {
        var incomingsStrs = new ArrayList<String>();
        for (var incoming : incomings.entrySet()) {
            incomingsStrs.add(incoming.getKey() + ": " + incoming.getValue());
        }
        var incomingStr = String.join(", ", incomingsStrs);
        return dst + " <- Φ(" + incomingStr + ")";
    }

    public void addIncoming(Label label, IRTemp op) {
        this.incomings.put(label, op);
    }

    @Override
    public List<IRValue> uses() {
        return new ArrayList<>(incomings.values());
    }

    @Override
    public List<IRTemp> defs() {
        return List.of(dst);
    }
}
