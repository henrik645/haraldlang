package nu.henrikvester.haraldlang.codegen.ir.primitives.values;

import lombok.Value;
import nu.henrikvester.haraldlang.codegen.ir.primitives.Use;

import java.util.ArrayList;
import java.util.List;

@Value
public class IRParam implements IRValue {
    int index;
    List<Use> uses = new ArrayList<>();
    
    @Override
    public String toString() {
        return "param(" + index + ")";
    }

    @Override
    public List<Use> users() {
        return uses;
    }
}
