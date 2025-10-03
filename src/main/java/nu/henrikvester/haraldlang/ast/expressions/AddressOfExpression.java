package nu.henrikvester.haraldlang.ast.expressions;

public record AddressOfExpression(String variableName) implements Expression {
    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visitAddressOfExpression(this);
    }
}
