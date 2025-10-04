package nu.henrikvester.haraldlang.exceptions;

import nu.henrikvester.haraldlang.core.SourceLocation;

// TODO: should this be a Diag class instead to continue parsing and collect multiple errors?
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
