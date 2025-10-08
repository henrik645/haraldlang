package nu.henrikvester.haraldlang.ast.expressions;

import nu.henrikvester.haraldlang.ast.types.HLType;
import nu.henrikvester.haraldlang.vm.Word;

public interface BinaryOperator {
    // TODO should this really be here?
    Word apply(Word left, Word right);

    HLType leftType();

    HLType rightType();

    HLType resultType();

    String symbol();
}

