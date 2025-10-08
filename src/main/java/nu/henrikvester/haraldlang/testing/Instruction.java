package nu.henrikvester.haraldlang.testing;

import nu.henrikvester.haraldlang.codegen.ir.Label;

import java.util.Map;
import java.util.Set;

sealed interface Instruction {
    Set<Value> uses();

    Set<Variable> defs();
}

sealed interface Value {
}

record Store(Value value, Variable variable) implements Instruction {
    @Override
    public Set<Value> uses() {
        return Set.of();
    }

    @Override
    public Set<Variable> defs() {
        return Set.of();
    }
}

record Print(Value value) implements Instruction {
    @Override
    public Set<Value> uses() {
        return Set.of(value);
    }

    @Override
    public Set<Variable> defs() {
        return Set.of();
    }
}

record Phi(Variable variable, Map<Label, Variable> sources) implements Instruction {
    @Override
    public Set<Value> uses() {
        return Set.of(sources.values().toArray(new Value[0]));
    }

    @Override
    public Set<Variable> defs() {
        return Set.of(variable);
    }
}

record Variable(String name) implements Value {
}

record Constant(int value) implements Value {
}