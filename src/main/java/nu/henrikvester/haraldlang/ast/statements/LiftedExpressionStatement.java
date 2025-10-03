package nu.henrikvester.haraldlang.ast.statements;

import nu.henrikvester.haraldlang.ast.expressions.Expression;
import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;

public record LiftedExpressionStatement(Expression expression) implements Statement {
    @Override
    public <R> R accept(StatementVisitor<R> visitor) throws HaraldMachineException {
        return visitor.visitLiftedExpressionStatement(this);
    }
}
