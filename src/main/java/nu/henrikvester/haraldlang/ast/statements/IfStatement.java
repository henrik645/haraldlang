package nu.henrikvester.haraldlang.ast.statements;

import nu.henrikvester.haraldlang.ast.expressions.Expression;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;

public record IfStatement(Expression condition, Statement thenBody, Statement elseBody) implements Statement {
    public IfStatement(Expression condition, Statement thenBody) {
        this(condition, thenBody, null);
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) throws HaraldLangException {
        return visitor.visitIfStatement(this);
    }
}
