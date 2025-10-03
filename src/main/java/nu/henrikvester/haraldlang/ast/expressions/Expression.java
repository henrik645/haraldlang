package nu.henrikvester.haraldlang.ast.expressions;

import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;

public interface Expression {
    <R> R accept(ExpressionVisitor<R> visitor) throws HaraldMachineException;
}

