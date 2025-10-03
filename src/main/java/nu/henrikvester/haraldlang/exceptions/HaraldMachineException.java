package nu.henrikvester.haraldlang.exceptions;

import nu.henrikvester.haraldlang.core.SourceLocation;

public class HaraldMachineException extends HaraldLangException {
    private HaraldMachineException(String message, SourceLocation location) {
        super(message, location);
    }

    public static HaraldMachineException undefinedVariable(String name, SourceLocation usageLocation) {
        return new HaraldMachineException("Undefined variable `" + name + "`", usageLocation);
    }
}
