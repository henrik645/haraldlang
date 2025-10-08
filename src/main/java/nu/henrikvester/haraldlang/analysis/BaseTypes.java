package nu.henrikvester.haraldlang.analysis;

import nu.henrikvester.haraldlang.ast.types.HLBool;
import nu.henrikvester.haraldlang.ast.types.HLInt;
import nu.henrikvester.haraldlang.ast.types.HLType;
import nu.henrikvester.haraldlang.ast.types.HLVoid;

import java.util.LinkedHashMap;
import java.util.Map;

// TODO should this be in this package?
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
