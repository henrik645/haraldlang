package nu.henrikvester.haraldlang.ast.expressions;

import nu.henrikvester.haraldlang.vm.Word;

public interface BinaryOperator {
    Word apply(Word left, Word right);

    String symbol();
}

