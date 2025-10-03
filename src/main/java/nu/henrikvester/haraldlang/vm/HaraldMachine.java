package nu.henrikvester.haraldlang.vm;

import nu.henrikvester.haraldlang.ast.statements.Statement;
import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;

public class HaraldMachine {
    private final Environment environment = new Environment();
    private final Interpreter interpreter = new Interpreter(environment);

    public Environment getEnvironment() {
        return environment;
    }

    public void run(Statement statement) throws HaraldMachineException {
        statement.accept(interpreter);
    }
}
