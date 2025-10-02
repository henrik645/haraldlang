package nu.henrikvester.haraldlang.ast.statements;

import nu.henrikvester.haraldlang.ast.expressions.Expression;
import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;
import nu.henrikvester.haraldlang.vm.HaraldMachine;

public record WhileStatement(Expression condition, Statement body) implements Statement {
    @Override
    public void execute(HaraldMachine vm) throws HaraldMachineException {
        while (condition.evaluate(vm.getEnvironment()).isTruthy()) {
            body.execute(vm);
        }
    }
}
