package nu.henrikvester.haraldlang.ast.statements;

import nu.henrikvester.haraldlang.ast.expressions.Expression;
import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;
import nu.henrikvester.haraldlang.vm.HaraldMachine;

public class IfStatement implements Statement {
    private final Expression condition;
    private final Statement thenBody;
    private final Statement elseBody;
    
    public IfStatement(Expression condition, Statement thenBody, Statement elseBody) {
        this.condition = condition;
        this.thenBody = thenBody;
        this.elseBody = elseBody;
    }
    
    public IfStatement(Expression condition, Statement thenBody) {
        this(condition, thenBody, null);
    }
    
    @Override
    public void execute(HaraldMachine vm) throws HaraldMachineException {
        var result = condition.evaluate(vm.getEnvironment());
        if (result.isTruthy()) {
            thenBody.execute(vm);
        } else if (elseBody != null) {
            elseBody.execute(vm);
        }
    }
}
