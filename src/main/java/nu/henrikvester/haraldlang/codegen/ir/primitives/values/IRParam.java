package nu.henrikvester.haraldlang.codegen.ir.primitives.values;

public record IRParam(int index) implements IRValue {
    @Override
    public String toString() {
        return "param(" + index + ")";
    }
}
