package nu.henrikvester.haraldlang.ast.statements;

import nu.henrikvester.haraldlang.exceptions.HaraldLangException;

public interface Statement {
    <R> R accept(StatementVisitor<R> visitor) throws HaraldLangException;
}

