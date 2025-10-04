package nu.henrikvester.haraldlang.ast.definitions;

import nu.henrikvester.haraldlang.ast.statements.Declaration;
import nu.henrikvester.haraldlang.ast.statements.Statement;
import nu.henrikvester.haraldlang.core.SourceLocation;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;

import java.util.List;

// TODO: parameters: previously List<Var>, should we use Declaration instead?
public record FunctionDefinition(String name, List<Declaration> parameters, Statement body,
                                 SourceLocation location) implements Definition {
    @Override
    public <R> R accept(DefinitionVisitor<R> visitor) throws HaraldLangException {
        return visitor.visitFunctionDefinition(this);
    }

    @Override
    public SourceLocation getLocation() {
        return location;
    }
}
