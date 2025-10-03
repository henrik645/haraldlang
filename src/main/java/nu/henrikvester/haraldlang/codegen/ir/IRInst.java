package nu.henrikvester.haraldlang.codegen.ir;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

enum BinOp {ADD, SUB, SHL, AND, OR, XOR}

public sealed interface IRInst {
    /**
     * Destination of this instruction, or null if none
     *
     * @return Destination temp or null
     */
    IRTemp dst();

    /**
     * All inputs to this instruction
     *
     * @return List of inputs
     */
    List<IRValue> inputs();

    /**
     * All inputs that are temps (i.e., not constants)
     *
     * @return List of input temps
     */
    default List<IRTemp> temps() {
        return inputs().stream().filter(IRTemp.class::isInstance).map(IRTemp.class::cast).toList();
    }
}

record Bin(IRTemp dst, BinOp op, IRValue lhs, IRValue rhs) implements IRInst {
    @Override
    public List<IRValue> inputs() {
        return List.of(lhs, rhs);
    }
}

record Mov(IRTemp dst, IRValue src) implements IRInst {
    @Override
    public List<IRValue> inputs() {
        return List.of(src);
    }
}

record Load(IRTemp dst, IRValue addr) implements IRInst {
    @Override
    public List<IRValue> inputs() {
        return List.of(addr);
    }
}

record Store(IRValue addr, IRValue src) implements IRInst {
    @Override
    public IRTemp dst() {
        return null;
    }

    @Override
    public List<IRValue> inputs() {
        return List.of(addr, src);
    }
}

record BrZ(IRValue cond, Label ifZero, Label ifNonZero) implements IRInst {
    @Override
    public IRTemp dst() {
        return null;
    }

    @Override
    public List<IRValue> inputs() {
        return List.of(cond);
    }
}

record Jmp(Label target) implements IRInst {
    @Override
    public IRTemp dst() {
        return null;
    }

    @Override
    public List<IRValue> inputs() {
        return List.of();
    }
}

record Phi(IRTemp dst, Map<Label, IRValue> incomings) implements IRInst {
    @Override
    public List<IRValue> inputs() {
        return new ArrayList<>(incomings.values());
    }
}

