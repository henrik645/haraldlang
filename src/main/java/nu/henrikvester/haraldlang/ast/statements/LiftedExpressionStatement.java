package nu.henrikvester.haraldlang.ast.statements;

import nu.henrikvester.haraldlang.ast.expressions.Expression;
import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;
import nu.henrikvester.haraldlang.vm.HaraldMachine;

public record LiftedExpressionStatement(Expression expression) implements Statement {
    @Override
    public void execute(HaraldMachine vm) throws HaraldMachineException {
        // executing a lifted expression statement means evaluating the expression
        expression.evaluate(vm.getEnvironment());
    }
}
