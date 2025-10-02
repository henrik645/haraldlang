package nu.henrikvester.haraldlang.core;

public enum TokenType {
    // Keywords
    KEYWORD_FUN,
    KEYWORD_LET,
    
    // Symbols
    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    SEMICOLON,
    COMMA,
    
    // Others
    IDENTIFIER,
    NUMBER,
    
    // End of file
    EOF
}
