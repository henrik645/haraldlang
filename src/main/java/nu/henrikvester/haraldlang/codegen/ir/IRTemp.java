package nu.henrikvester.haraldlang.codegen.ir;

public record IRTemp(int id) implements IRValue, Comparable<IRTemp> {
    @Override
    public int compareTo(IRTemp o) {
        return Integer.compare(this.id, o.id);
    }

    @Override
    public String toString() {
        return "t" + id;
    }
}
