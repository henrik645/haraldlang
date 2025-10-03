package nu.henrikvester.haraldlang.ast.expressions;

import nu.henrikvester.haraldlang.ast.Node;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;

public interface Expression extends Node {
    <R> R accept(ExpressionVisitor<R> visitor) throws HaraldLangException;
}

