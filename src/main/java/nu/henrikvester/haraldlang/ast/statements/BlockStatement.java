package nu.henrikvester.haraldlang.ast.statements;

import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;
import nu.henrikvester.haraldlang.vm.HaraldMachine;

import java.util.List;

public record BlockStatement(List<Statement> statements) implements Statement {
    @Override
    public void execute(HaraldMachine vm) throws HaraldMachineException {
        for (Statement statement : statements) {
            statement.execute(vm);
        }
    }
}
