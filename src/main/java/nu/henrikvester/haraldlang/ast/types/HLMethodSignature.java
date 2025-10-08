package nu.henrikvester.haraldlang.ast.types;

import java.util.List;

public record HLMethodSignature(HLType returnType, String methodName, List<HLType> parameterTypes) {
    public String signature() {
        var parameterStr = String.join(", ", parameterTypes.stream().map(HLType::name).toList());
        return returnType + " " + methodName + "(" + parameterStr + ")";
    }
}
