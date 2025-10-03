package nu.henrikvester.haraldlang.exceptions;

import nu.henrikvester.haraldlang.ast.expressions.Var;
import nu.henrikvester.haraldlang.core.SourceLocation;

public class CompilerException extends HaraldLangException {
    private CompilerException(String message, SourceLocation location) {
        super(message, location);
    }

    public static CompilerException undeclaredVariable(Var var) {
        return new CompilerException("Variable `" + var.identifier() + "` is not declared", var.location());
    }
}
