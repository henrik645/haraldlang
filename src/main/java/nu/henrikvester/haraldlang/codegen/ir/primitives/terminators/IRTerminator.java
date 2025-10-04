package nu.henrikvester.haraldlang.codegen.ir.primitives.terminators;

import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.List;

public sealed interface IRTerminator permits BrZ, Hlt, Jmp, Ret, RetVoid {
    List<IRValue> inputs();
}
