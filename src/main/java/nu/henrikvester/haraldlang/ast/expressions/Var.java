package nu.henrikvester.haraldlang.ast.expressions;

import nu.henrikvester.haraldlang.ast.lvalue.LValue;
import nu.henrikvester.haraldlang.ast.lvalue.LValueVisitor;
import nu.henrikvester.haraldlang.core.SourceLocation;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;

public record Var(String identifier, SourceLocation location) implements Expression, LValue {
    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) throws HaraldLangException {
        return visitor.visitVar(this);
    }

    @Override
    public <R> R accept(LValueVisitor<R> visitor) throws HaraldLangException {
        return visitor.visitVar(this);
    }

    @Override
    public String toString() {
        return identifier;
    }

    @Override
    public SourceLocation getLocation() {
        return location;
    }
}
