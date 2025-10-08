package nu.henrikvester.haraldlang.ast.types;

import java.util.LinkedHashMap;
import java.util.Map;

public class BaseTypes {
    private static Map<String, HLType> types;

    public static Map<String, HLType> getBaseTypes() {
        if (types != null) return types;
        types = new LinkedHashMap<>();
        types.put("int", new HLInt());
        types.put("void", new HLVoid());
        types.put("boolean", new HLBool());
        return types;
    }
}
