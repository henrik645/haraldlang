package nu.henrikvester.haraldlang.ast.types;

public sealed interface HLType permits HLBool, HLInt, HLUserDefinedType, HLVoid {
    String name();
}
