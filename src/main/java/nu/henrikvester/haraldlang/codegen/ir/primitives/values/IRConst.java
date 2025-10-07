package nu.henrikvester.haraldlang.codegen.ir.primitives.values;

public record IRConst(int value) implements IRValue {
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

