package nu.henrikvester.haraldlang.codegen.ir;

import nu.henrikvester.haraldlang.ast.definitions.FunctionDefinition;
import nu.henrikvester.haraldlang.codegen.ir.primitives.IRFunction;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;

public class FunctionLowering {
    IRFunction lowerFunction(FunctionDefinition functionDefinition, Bindings bindings) throws HaraldLangException {
        var functionBuilder = new FunctionBuilder(functionDefinition.name());
        var translator = new TranslatorImpl(functionBuilder);

        var locals = bindings.locals(functionDefinition);
        var gen = new CodeGenerator(translator, bindings, locals);

        int i = 0;
        for (var param : functionDefinition.parameters()) {
            var slot = bindings.slot(param);
            var frame = gen.frameOf(slot);
            translator.store(frame, translator.param(i++));
        }

        functionDefinition.body().accept(gen);

        if (!functionBuilder.getCurrentBlock().isClosed()) {
            translator.returnVoid();
        }
        return functionBuilder.finish();
    }
}
