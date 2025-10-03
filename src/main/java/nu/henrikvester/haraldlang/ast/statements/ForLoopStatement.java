package nu.henrikvester.haraldlang.ast.statements;

import nu.henrikvester.haraldlang.ast.expressions.Expression;
import nu.henrikvester.haraldlang.core.SourceLocation;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;

public record ForLoopStatement(Statement initial, Expression condition, Statement update,
                               Statement body, SourceLocation location) implements Statement {
    @Override
    public <R> R accept(StatementVisitor<R> visitor) throws HaraldLangException {
        return visitor.visitForLoopStatement(this);
    }

    @Override
    public SourceLocation getLocation() {
        return location;
    }
}
