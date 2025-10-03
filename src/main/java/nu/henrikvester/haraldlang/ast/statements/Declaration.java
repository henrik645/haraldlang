package nu.henrikvester.haraldlang.ast.statements;

import nu.henrikvester.haraldlang.ast.expressions.Expression;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;

/**
 * A variable declaration statement, e.g., `let x = 5;`
 *
 * @param identifier the name of the variable
 * @param expression the expression whose value will be assigned to the variable, can be null for uninitialized variables
 */
public record Declaration(String identifier, Expression expression) implements Statement {
    @Override
    public <R> R accept(StatementVisitor<R> visitor) throws HaraldLangException {
        return visitor.visitDeclaration(this);
    }
}
