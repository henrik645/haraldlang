package nu.henrikvester.haraldlang.exceptions;

import nu.henrikvester.haraldlang.ast.expressions.Var;
import nu.henrikvester.haraldlang.ast.lvalue.LValue;
import nu.henrikvester.haraldlang.core.SourceLocation;

public class HaraldMachineException extends HaraldLangException {
    private HaraldMachineException(String message, SourceLocation location) {
        super(message, location);
    }

    public static HaraldMachineException undefinedVariable(LValue lvalue) {
        return new HaraldMachineException("`" + lvalue + "` is not defined", lvalue.getLocation());
    }

    public static HaraldLangException uninitializedVariable(Var var) {
        return new HaraldMachineException("`" + var.identifier() + "` is not initialized", var.location());
    }
}
