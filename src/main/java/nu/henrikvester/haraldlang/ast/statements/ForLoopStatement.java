package nu.henrikvester.haraldlang.ast.statements;

import nu.henrikvester.haraldlang.ast.expressions.Expression;
import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;
import nu.henrikvester.haraldlang.vm.HaraldMachine;

public record ForLoopStatement(Statement initial, Expression condition, Statement update,
                               Statement body) implements Statement {
    @Override
    public void execute(HaraldMachine vm) throws HaraldMachineException {
        vm.run(initial); // TODO: scope this
        while (condition.evaluate(vm.getEnvironment()).isTruthy()) {
            vm.run(body);
            vm.run(update);
        }
    }
}
