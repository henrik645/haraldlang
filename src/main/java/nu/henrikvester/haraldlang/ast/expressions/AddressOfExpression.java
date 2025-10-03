package nu.henrikvester.haraldlang.ast.expressions;

import nu.henrikvester.haraldlang.core.SourceLocation;

public record AddressOfExpression(Var var, SourceLocation location) implements Expression {
    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visitAddressOfExpression(this);
    }

    @Override
    public SourceLocation getLocation() {
        return location;
    }
}
