package nu.henrikvester.haraldlang.ast.expressions;

import nu.henrikvester.haraldlang.vm.Environment;
import nu.henrikvester.haraldlang.vm.Word;

public record LiteralExpression(int value) implements Expression {
    @Override
    public Word evaluate(Environment env) {
        return new Word(value);
    }
}
