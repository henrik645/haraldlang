package nu.henrikvester.haraldlang.exceptions;

import nu.henrikvester.haraldlang.core.SourceLocation;

public class ParserException extends HaraldLangException {
    private ParserException(String message, SourceLocation location) {
        super(message, location);
    }

    public static ParserException unexpectedToken(String expected, String actual, SourceLocation location) {
        return new ParserException("Expected " + expected + ", but got " + actual, location);
    }

    public static ParserException unexpectedEndOfInput(SourceLocation location) {
        return new ParserException("Unexpected end of input", location);
    }

    public static ParserException notImplementedYet(String feature, SourceLocation location) {
        return new ParserException("Not yet implemented: " + feature, location);
    }
}
