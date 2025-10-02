package nu.henrikvester.haraldlang.core;

public record Token(TokenType type, String lexeme, SourceLocation location) {
}

