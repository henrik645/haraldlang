package nu.henrikvester.haraldlang.exceptions;

public class TokenizerException extends CompilerException {
    private TokenizerException(String message) {
        super("While tokenizing: " + message);
    }
    
    public static TokenizerException unexpectedCharacter(char c) {
        return new TokenizerException("Unexpected character: " + c);
    }
}
