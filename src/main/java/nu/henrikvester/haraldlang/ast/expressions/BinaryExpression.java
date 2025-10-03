package nu.henrikvester.haraldlang.ast.expressions;

import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;

public record BinaryExpression(Expression left, BinaryOperator op, Expression right) implements Expression {
    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) throws HaraldMachineException {
        return visitor.visitBinaryExpression(this);
    }
}
