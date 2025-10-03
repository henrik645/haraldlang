package nu.henrikvester.haraldlang.ast.expressions;

import com.sun.java.accessibility.util.Translator;
import nu.henrikvester.haraldlang.exceptions.NotImplementedException;
import nu.henrikvester.haraldlang.vm.Environment;
import nu.henrikvester.haraldlang.vm.Word;

public record LiteralExpression(int value) implements Expression {
    @Override
    public Word evaluate(Environment env) {
        return new Word(value);
    }

    @Override
    public void lower(Translator tr) {
        throw new NotImplementedException();
    }
}
