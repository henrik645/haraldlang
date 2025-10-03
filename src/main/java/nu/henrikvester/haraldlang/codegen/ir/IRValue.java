package nu.henrikvester.haraldlang.codegen.ir;

public sealed interface IRValue permits IRConst, IRTemp, IRFrameSlot {
}
