package nu.henrikvester.haraldlang.ast.statements;

import nu.henrikvester.haraldlang.ast.expressions.Expression;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;

public record WhileStatement(Expression condition, Statement body) implements Statement {
    @Override
    public <R> R accept(StatementVisitor<R> visitor) throws HaraldLangException {
        return visitor.visitWhileStatement(this);
    }
}
