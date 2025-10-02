package nu.henrikvester.haraldlang.core;

public record Token(TokenType type, String lexeme, SourceLocation location) {
    public void pointOut(String code) {
        System.out.println(this);
        location.pointOut(code);
    }
}

