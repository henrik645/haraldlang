package nu.henrikvester.haraldlang.codegen.ir.primitives.instructions;

import nu.henrikvester.haraldlang.codegen.ir.Label;
import nu.henrikvester.haraldlang.codegen.ir.primitives.Use;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRFrameSlot;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Phi implements IRInst {

    private final IRFrameSlot slot;
    private final Map<Label, Use> incomings;
    private IRTemp dst;
    private int nextIndex = 0;

    public Phi(IRTemp dst, IRFrameSlot slot, Map<Label, IRTemp> incomings) {
        this.dst = dst;
        this.slot = slot;
        this.incomings = new LinkedHashMap<>();
        for (var incoming : incomings.entrySet()) {
            this.incomings.put(incoming.getKey(), new Use(this, nextIndex++, incoming.getValue()));
        }
    }
    public Phi(IRTemp dst, IRFrameSlot slot) {
        this(dst, slot, new LinkedHashMap<>());
    }

    public IRTemp dst() {
        return dst;
    }

    public IRFrameSlot slot() {
        return slot;
    }
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
        this.incomings.put(label, new Use(this, nextIndex++, op));
    }

    @Override
    public List<Use> operands() {
        return new ArrayList<>(incomings.values());
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
