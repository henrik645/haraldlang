package nu.henrikvester.haraldlang.ast.expressions;

import nu.henrikvester.haraldlang.core.SourceLocation;

public record LiteralExpression(int value, SourceLocation location) implements Expression {
    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visitLiteralExpression(this);
    }

    @Override
    public SourceLocation getLocation() {
        return location;
    }
}
