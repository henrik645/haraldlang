package nu.henrikvester.haraldlang.ast.statements;

import nu.henrikvester.haraldlang.ast.Node;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;

public interface Statement extends Node {
    <R> R accept(StatementVisitor<R> visitor) throws HaraldLangException;
}

