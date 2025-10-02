package nu.henrikvester.haraldlang.ast.expressions;

import nu.henrikvester.haraldlang.core.SourceLocation;
import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;
import nu.henrikvester.haraldlang.vm.Environment;
import nu.henrikvester.haraldlang.vm.Word;

public record IdentifierExpression(String identifier, SourceLocation location) implements Expression {
    @Override
    public Word evaluate(Environment env) throws HaraldMachineException {
        var ret = env.get(identifier);
        if (ret == null) {
            throw HaraldMachineException.undefinedVariable(identifier, location);
        }
        return ret;
    }
}
