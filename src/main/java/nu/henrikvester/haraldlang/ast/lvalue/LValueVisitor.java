package nu.henrikvester.haraldlang.ast.lvalue;

import nu.henrikvester.haraldlang.ast.expressions.Var;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;

public interface LValueVisitor<R> {
    R visitVar(Var var) throws HaraldLangException;
}
