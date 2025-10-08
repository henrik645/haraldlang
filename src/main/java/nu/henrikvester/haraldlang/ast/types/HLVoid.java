package nu.henrikvester.haraldlang.ast.types;

public record HLVoid() implements HLType {
    @Override
    public String name() {
        return "void";
    }
}
