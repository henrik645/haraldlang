package nu.henrikvester.haraldlang.ast.types;

// TODO new HLBool() seems weird
public record HLBool() implements HLType {
    @Override
    public String name() {
        return "boolean";
    }
}
