package nu.henrikvester.haraldlang.codegen.ir;

import nu.henrikvester.haraldlang.ast.definitions.FunctionDefinition;
import nu.henrikvester.haraldlang.codegen.ir.primitives.IRFunction;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;

public class FunctionLowering {
    IRFunction lowerFunction(FunctionDefinition functionDefinition, Bindings bindings) throws HaraldLangException {
        var functionBuilder = new FunctionBuilder23(functionDefinition.name(), bindings, bindings.locals(functionDefinition));
        var gen = new FunctionLowerer(functionBuilder);
        functionDefinition.body().accept(gen);

        return functionBuilder.finish();
    }
}
