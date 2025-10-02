package nu.henrikvester.haraldlang.ast.statements;

import nu.henrikvester.haraldlang.ast.expressions.Expression;
import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;
import nu.henrikvester.haraldlang.vm.HaraldMachine;

public record Assignment(String identifier, Expression value) implements Statement {
    @Override
    public void execute(HaraldMachine vm) throws HaraldMachineException {
        vm.getEnvironment().set(identifier, value.evaluate(vm.getEnvironment()));
    }
}
