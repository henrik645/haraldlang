package nu.henrikvester.haraldlang.codegen.ir;

public record Label(int id) {
    @Override
    public String toString() {
        return "L" + id;
    }
}
