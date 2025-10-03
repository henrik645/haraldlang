package nu.henrikvester.haraldlang.ast.expressions;

import com.sun.java.accessibility.util.Translator;
import nu.henrikvester.haraldlang.core.SourceLocation;
import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;
import nu.henrikvester.haraldlang.exceptions.NotImplementedException;
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

    @Override
    public void lower(Translator tr) {
        throw new NotImplementedException();
    }
}
