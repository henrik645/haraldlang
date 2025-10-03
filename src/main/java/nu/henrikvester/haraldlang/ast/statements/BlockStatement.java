package nu.henrikvester.haraldlang.ast.statements;

import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;

import java.util.List;

public record BlockStatement(List<Statement> statements) implements Statement {
    @Override
    public <R> R accept(StatementVisitor<R> visitor) throws HaraldMachineException {
        return visitor.visitBlockStatement(this);
    }
}
