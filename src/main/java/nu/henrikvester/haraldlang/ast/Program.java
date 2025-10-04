package nu.henrikvester.haraldlang.ast;

import nu.henrikvester.haraldlang.ast.definitions.FunctionDefinition;

import java.util.List;

public record Program(List<FunctionDefinition> functions) {
}
