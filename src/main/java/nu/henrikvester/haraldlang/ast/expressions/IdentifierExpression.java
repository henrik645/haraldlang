package nu.henrikvester.haraldlang.ast.expressions;

import nu.henrikvester.haraldlang.core.SourceLocation;
import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;

public record IdentifierExpression(String identifier, SourceLocation location) implements Expression {
    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) throws HaraldMachineException {
        return visitor.visitIdentifierExpression(this);
    }
}
