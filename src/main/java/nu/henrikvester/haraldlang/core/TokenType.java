package nu.henrikvester.haraldlang.core;

public enum TokenType {
    // Keywords
    KEYWORD_FUN,
    KEYWORD_LET,
    KEYWORD_PRINT,
    KEYWORD_IF,
    KEYWORD_ELSE,
    KEYWORD_WHILE,
    KEYWORD_FOR,
    KEYWORD_DECLARE,

    // Symbols
    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    SEMICOLON,
    COMMA,
    PLUS,
    MINUS,
    EQUALS,
    NOT_EQUALS,
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL,
    LESSER_THAN,
    LESSER_THAN_OR_EQUAL,
    EXCLAMATION, // '!'
    AMPERSAND,
    ASTERISK,

    // Others
    IDENTIFIER,
    NUMBER,

    // End of file
    EOF
}
