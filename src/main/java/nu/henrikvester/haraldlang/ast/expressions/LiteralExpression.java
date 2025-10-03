package nu.henrikvester.haraldlang.ast.expressions;

public record LiteralExpression(int value) implements Expression {
    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visitLiteralExpression(this);
    }
}
