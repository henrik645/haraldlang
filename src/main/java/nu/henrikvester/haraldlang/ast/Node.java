package nu.henrikvester.haraldlang.ast;

import nu.henrikvester.haraldlang.core.SourceLocation;

public interface Node {
    SourceLocation getLocation();
}
