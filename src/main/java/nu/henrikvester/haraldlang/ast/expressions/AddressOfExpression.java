package nu.henrikvester.haraldlang.ast.expressions;

import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;
import nu.henrikvester.haraldlang.vm.Environment;
import nu.henrikvester.haraldlang.vm.Word;

public record AddressOfExpression(String variableName) implements Expression {
    @Override
    // TODO: evaluate probably needs access to entire VM, not just environment. Or is environment responsible for memory, too?
    public Word evaluate(Environment env) throws HaraldMachineException {
        return null;
    }
}
