package nu.henrikvester.haraldlang.exceptions;

import nu.henrikvester.haraldlang.core.SourceLocation;

public class TokenizerException extends HaraldLangException {
    private TokenizerException(String message, SourceLocation location) {
        super("While tokenizing: " + message, location);
    }
    
    public static TokenizerException unexpectedCharacter(char c, SourceLocation location) {
        return new TokenizerException("Unexpected character: " + c, location);
    }
    
    public static TokenizerException expectedCharacter(char c, SourceLocation location) {
        return new TokenizerException("Expected character: " + c, location);
    }
}
