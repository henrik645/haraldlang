package nu.henrikvester.haraldlang.tokenizer;

import nu.henrikvester.haraldlang.core.TokenType;

public class IdentifierAndKeywordGobbler implements Gobbler {
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
            default -> TokenType.IDENTIFIER;
        };
    }
}
