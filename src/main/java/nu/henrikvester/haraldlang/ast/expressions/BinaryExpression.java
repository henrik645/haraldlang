package nu.henrikvester.haraldlang.ast.expressions;

import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;
import nu.henrikvester.haraldlang.vm.Environment;
import nu.henrikvester.haraldlang.vm.Word;

public record BinaryExpression(Expression left, BinaryOperator op, Expression right) implements Expression {
    @Override
    public Word evaluate(Environment env) throws HaraldMachineException {
        return op.apply(left.evaluate(env), right.evaluate(env));
    }
}
