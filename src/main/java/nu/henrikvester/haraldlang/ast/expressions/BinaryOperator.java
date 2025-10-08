package nu.henrikvester.haraldlang.ast.expressions;

import nu.henrikvester.haraldlang.ast.types.HLType;
import nu.henrikvester.haraldlang.vm.Word;

public interface BinaryOperator {
    Word apply(Word left, Word right); // used in the interpreter

    HLType leftType();

    HLType rightType();

    HLType resultType();

    String symbol();
}

