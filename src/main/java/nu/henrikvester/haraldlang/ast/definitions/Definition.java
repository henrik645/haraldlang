package nu.henrikvester.haraldlang.ast.definitions;

import nu.henrikvester.haraldlang.ast.Node;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;

public interface Definition extends Node {
    <R> R accept(DefinitionVisitor<R> visitor) throws HaraldLangException;
}
