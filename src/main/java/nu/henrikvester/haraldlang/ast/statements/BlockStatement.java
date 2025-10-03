package nu.henrikvester.haraldlang.ast.statements;

import nu.henrikvester.haraldlang.exceptions.HaraldLangException;

import java.util.List;

public record BlockStatement(List<Statement> statements) implements Statement {
    @Override
    public <R> R accept(StatementVisitor<R> visitor) throws HaraldLangException {
        return visitor.visitBlockStatement(this);
    }
}
