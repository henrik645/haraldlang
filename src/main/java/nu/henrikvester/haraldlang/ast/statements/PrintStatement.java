package nu.henrikvester.haraldlang.ast.statements;

import nu.henrikvester.haraldlang.ast.expressions.Expression;
import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;
import nu.henrikvester.haraldlang.vm.HaraldMachine;

public record PrintStatement(Expression expr) implements Statement {
    @Override
    public void execute(HaraldMachine vm) throws HaraldMachineException {
        System.out.println("[OUTPUT] " + expr.evaluate(vm.getEnvironment()));
    }
}
