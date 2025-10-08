package nu.henrikvester.haraldlang.ast.types;

public record HLInt() implements HLType {
    @Override
    public String name() {
        return "int";
    }
}
