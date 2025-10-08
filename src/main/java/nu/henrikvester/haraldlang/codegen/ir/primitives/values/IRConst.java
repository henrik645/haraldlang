package nu.henrikvester.haraldlang.codegen.ir.primitives.values;

import lombok.Value;
import nu.henrikvester.haraldlang.codegen.ir.primitives.Use;

import java.util.ArrayList;
import java.util.List;

@Value
public class IRConst implements IRValue {
    int value;
    List<Use> uses = new ArrayList<>(); // TODO uses here for a constant???
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public List<Use> users() {
        return uses;
    }
}

