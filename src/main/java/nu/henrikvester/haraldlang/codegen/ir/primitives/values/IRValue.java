package nu.henrikvester.haraldlang.codegen.ir.primitives.values;

public sealed interface IRValue permits IRConst, IRFrameSlot, IRParam, IRTemp, IRUndef {
}
