package nu.henrikvester.haraldlang.vm;

import nu.henrikvester.haraldlang.ast.statements.Statement;
import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;

import java.util.List;

public class HaraldMachine {
    private final Environment environment = new Environment();
    
    public Environment getEnvironment() {
        return environment;
    }
    
    public void run(Statement statement) throws HaraldMachineException {
        statement.execute(this);
    }
}
