package nu.henrikvester.haraldlang.ast.statements;

import nu.henrikvester.haraldlang.ast.expressions.Expression;
import nu.henrikvester.haraldlang.ast.lvalue.LValue;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;

public record Assignment(LValue lvalue, Expression value) implements Statement {
    @Override
    public <R> R accept(StatementVisitor<R> visitor) throws HaraldLangException {
        return visitor.visitAssignment(this);
    }
}
