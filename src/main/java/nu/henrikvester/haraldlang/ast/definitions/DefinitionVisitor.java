package nu.henrikvester.haraldlang.ast.definitions;

import nu.henrikvester.haraldlang.exceptions.HaraldLangException;

public interface DefinitionVisitor<R> {
    R visitFunctionDefinition(FunctionDefinition functionDefinition) throws HaraldLangException;
}
