package nu.henrikvester.haraldlang.ast.statements;

import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;
import nu.henrikvester.haraldlang.vm.HaraldMachine;

public interface Statement {
    void execute(HaraldMachine vm) throws HaraldMachineException;
}

