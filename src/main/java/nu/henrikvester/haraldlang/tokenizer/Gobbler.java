package nu.henrikvester.haraldlang.tokenizer;

import nu.henrikvester.haraldlang.core.TokenType;

public interface Gobbler {
    /**
     * Checks if the character can be the start of a token.
     * @param c the character to check
     * @return true if the character can be the start of a token, false otherwise
     */
    boolean isStart(char c);

    /**
     * Checks if the character can be part of a token.
     * @param c the character to check
     * @return true if the character can be part of a token, false otherwise
     */
    boolean isPart(char c);

    /**
     * Returns the TokenType for the given lexeme.
     * @param lexeme the lexeme to check
     * @return the TokenType for the given lexeme
     */
    TokenType getTokenType(String lexeme);
}
