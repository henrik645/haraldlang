package nu.henrikvester.haraldlang.ast.expressions;

import nu.henrikvester.haraldlang.core.SourceLocation;

// a use of a type by name
public record TypeUse(String typeName, SourceLocation location) {
}
