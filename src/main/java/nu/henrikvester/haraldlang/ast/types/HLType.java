package nu.henrikvester.haraldlang.ast.types;

import java.util.List;

public sealed interface HLType {
    String name();
}

record HLInt() implements HLType {
    @Override
    public String name() {
        return "int";
    }
}

record HLBool() implements HLType {
    @Override
    public String name() {
        return "boolean";
    }
}

record HLVoid() implements HLType {
    @Override
    public String name() {
        return "void";
    }
}

record HLUserDefinedType(String name) implements HLType {
}

record HLMethodSignature(HLType returnType, String methodName, List<HLType> parameterTypes) {
    public String signature() {
        var parameterStr = String.join(", ", parameterTypes.stream().map(HLType::name).toList());
        return returnType + " " + methodName + "(" + parameterStr + ")";
    }
}