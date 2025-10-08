package nu.henrikvester.haraldlang.codegen.ir.primitives.values;

import nu.henrikvester.haraldlang.codegen.ir.primitives.Use;

import java.util.List;

public sealed interface IRValue permits IRConst, IRFrameSlot, IRParam, IRTemp {
    List<Use> users();

    default void addUse(Use use) {
        users().add(use);
    }

    default void removeUse(Use use) {
        users().remove(use);
    }

    default void replaceAllUsesWith(IRValue newValue) {
        if (this == newValue) return;
        // iterate copy since replace mutates use lists
        for (var use : List.copyOf(users())) {
            use.replaceWith(newValue);
        }
    }
}
