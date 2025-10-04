package nu.henrikvester.haraldlang.codegen.ir;

import nu.henrikvester.haraldlang.ast.definitions.FunctionDefinition;
import nu.henrikvester.haraldlang.codegen.ir.primitives.IRFunction;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;

public class FunctionLowering {
    IRFunction lowerFunction(FunctionDefinition functionDefinition, Bindings bindings) throws HaraldLangException {
        var functionBuilder = new FunctionBuilder23(functionDefinition.name(), bindings, bindings.locals(functionDefinition));
//        var translator = new FunctionLowerer(functionBuilder);

//        var locals = bindings.locals(functionDefinition);
        var gen = new FunctionLowerer(functionBuilder);

        // TODO reimplement this. WHEN: CodeGenerator is complete
//        int i = 0;
//        for (var param : functionDefinition.parameters()) {
//            var slot = bindings.slot(param);
//            var frame = gen.frameOf(slot);
//            translator.store(frame, translator.param(i++));
//        }

        functionDefinition.body().accept(gen);

        if (!functionBuilder.getCurrentBlock().isClosed()) {
//            translator.returnVoid();
        }
        return functionBuilder.finish();
    }
}
