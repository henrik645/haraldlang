package nu.henrikvester.haraldlang.codegen.ir;

public record Label(int id, String purpose) {
    @Override
    public String toString() {
        return "L" + id + "_" + purpose;
    }
}
