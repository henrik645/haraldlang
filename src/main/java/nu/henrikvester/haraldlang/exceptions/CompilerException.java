package nu.henrikvester.haraldlang.exceptions;

import nu.henrikvester.haraldlang.ast.expressions.Expression;
import nu.henrikvester.haraldlang.ast.expressions.Var;
import nu.henrikvester.haraldlang.ast.types.HLType;
import nu.henrikvester.haraldlang.core.SourceLocation;

public class CompilerException extends HaraldLangException {
    private CompilerException(String message, SourceLocation location) {
        super(message, location);
    }

    public static CompilerException undeclaredVariable(Var var) {
        return new CompilerException("Variable `" + var.identifier() + "` is not declared", var.location());
    }

    public static CompilerException unknownType(String typeName, SourceLocation location) {
        return new CompilerException("Unknown type `" + typeName + "`", location);
    }

    public static CompilerException typeMismatch(HLType expected, HLType actual, Expression offender) {
        return new CompilerException("Expected expression to have type " + expected.name() + ", but got " + actual.name(), offender.getLocation());
    }
}
