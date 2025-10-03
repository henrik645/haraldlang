package nu.henrikvester.haraldlang.tokenizer;

import nu.henrikvester.haraldlang.core.TokenType;

class IdentifierAndKeywordGobbler implements Gobbler {
    @Override
    public boolean isStart(char c) {
        return Character.isLetter(c);
    }

    @Override
    public boolean isPart(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    @Override
    public TokenType getTokenType(String lexeme) {
        return switch (lexeme) {
            case "let" -> TokenType.KEYWORD_LET;
            case "fun" -> TokenType.KEYWORD_FUN;
            case "print" -> TokenType.KEYWORD_PRINT;
            case "if" -> TokenType.KEYWORD_IF;
            case "else" -> TokenType.KEYWORD_ELSE;
            case "while" -> TokenType.KEYWORD_WHILE;
            case "for" -> TokenType.KEYWORD_FOR;
            case "declare" -> TokenType.KEYWORD_DECLARE;
            default -> TokenType.IDENTIFIER;
        };
    }
}
