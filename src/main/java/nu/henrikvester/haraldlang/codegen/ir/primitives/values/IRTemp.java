package nu.henrikvester.haraldlang.codegen.ir.primitives.values;

import lombok.Value;
import nu.henrikvester.haraldlang.codegen.ir.primitives.Use;

import java.util.ArrayList;
import java.util.List;

@Value
public class IRTemp implements IRValue, Comparable<IRTemp> {
    int id;
    List<Use> uses = new ArrayList<>();
    
    @Override
    public int compareTo(IRTemp o) {
        return Integer.compare(this.id, o.id);
    }

    @Override
    public String toString() {
        return "t" + id;
    }

    @Override
    public List<Use> users() {
        return uses;
    }
}
