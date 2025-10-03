package nu.henrikvester.haraldlang.ast.statements;

import nu.henrikvester.haraldlang.ast.expressions.Expression;
import nu.henrikvester.haraldlang.core.SourceLocation;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;

public record IfStatement(Expression condition, Statement thenBody, Statement elseBody, SourceLocation location) implements Statement {
    public IfStatement(Expression condition, Statement thenBody, SourceLocation location) {
        this(condition, thenBody, null, location);
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) throws HaraldLangException {
        return visitor.visitIfStatement(this);
    }

    @Override
    public SourceLocation getLocation() {
        return location;
    }
}
