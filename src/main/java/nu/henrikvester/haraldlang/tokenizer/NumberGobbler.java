package nu.henrikvester.haraldlang.tokenizer;

import nu.henrikvester.haraldlang.core.TokenType;

public class NumberGobbler implements Gobbler {
    @Override
    public boolean isStart(char c) {
        return Character.isDigit(c);
    }

    @Override
    public boolean isPart(char c) {
        return Character.isDigit(c);
    }

    @Override
    public TokenType getTokenType(String lexeme) {
        return TokenType.NUMBER;
    }
}
