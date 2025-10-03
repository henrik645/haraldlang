package nu.henrikvester.haraldlang.codegen.old;

/**
 * Represents a variable in SSA form
 *
 * @param id
 */
public record Variable(int id) implements Comparable<Variable> {
    @Override
    public String toString() {
        return "v" + id;
    }

    @Override
    public int compareTo(Variable o) {
        return Integer.compare(this.id, o.id);
    }

}
