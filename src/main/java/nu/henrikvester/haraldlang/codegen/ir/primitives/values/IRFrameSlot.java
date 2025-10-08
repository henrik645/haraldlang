package nu.henrikvester.haraldlang.codegen.ir.primitives.values;

import lombok.Value;
import nu.henrikvester.haraldlang.codegen.ir.primitives.Use;

import java.util.ArrayList;
import java.util.List;

/**
 * An IR-level representation of the current stack frame that backs a local variable.
 * Codegen maps this to actual stack offsets during code generation.
 * If a promotable local gets eliminated (replaced by SSA temps), the FrameSlot loads and stores disappear.
 */
@Value
public class IRFrameSlot implements IRValue {
    int id;
    List<Use> uses = new ArrayList<>();
    @Override
    public String toString() {
        return "[f" + id + "]";
    }

    @Override
    public List<Use> users() {
        return uses;
    }
}
