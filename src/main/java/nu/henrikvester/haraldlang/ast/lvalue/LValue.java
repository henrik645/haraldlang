package nu.henrikvester.haraldlang.ast.lvalue;

import nu.henrikvester.haraldlang.ast.Node;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;

public interface LValue extends Node {
    <R> R accept(LValueVisitor<R> visitor) throws HaraldLangException;
}
