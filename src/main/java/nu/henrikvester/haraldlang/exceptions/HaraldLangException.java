package nu.henrikvester.haraldlang.exceptions;

import nu.henrikvester.haraldlang.core.SourceLocation;

public class HaraldLangException extends Exception {
    private final SourceLocation location;

    public HaraldLangException(String message, SourceLocation location) {
        super(message);
        this.location = location;
    }

    public static HaraldLangException noMainFunction() {
        return new HaraldLangException("No main function found", SourceLocation.NONE);
    }

    public void printError(String code) {
        System.err.println("Error: " + getMessage());
        location.pointOut(code, getMessage());
    }
}
