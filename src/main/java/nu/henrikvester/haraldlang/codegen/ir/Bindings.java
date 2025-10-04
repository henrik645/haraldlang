package nu.henrikvester.haraldlang.codegen.ir;

import nu.henrikvester.haraldlang.ast.definitions.FunctionDefinition;
import nu.henrikvester.haraldlang.ast.expressions.Var;
import nu.henrikvester.haraldlang.ast.statements.Declaration;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class Bindings {
    private final IdentityHashMap<Var, VarSlot> use2slot;
    private final IdentityHashMap<Declaration, VarSlot> decl2slot;
    private final Map<FunctionDefinition, List<VarSlot>> function2locals;

    Bindings(IdentityHashMap<Var, VarSlot> use2slot,
             IdentityHashMap<Declaration, VarSlot> decl2slot,
             Map<FunctionDefinition, List<VarSlot>> function2locals
    ) {
        this.use2slot = new IdentityHashMap<>(use2slot);
        this.decl2slot = new IdentityHashMap<>(decl2slot);
        this.function2locals = Map.copyOf(function2locals);
    }

    public VarSlot slot(Var use) {
        var s = use2slot.get(use);
        if (s == null) throw new IllegalArgumentException("No slot for var use: " + use);
        return s;
    }

    public VarSlot slot(Declaration decl) {
        var s = decl2slot.get(decl);
        if (s == null) throw new IllegalArgumentException("No slot for declaration: " + decl);
        return s;
    }

    public List<VarSlot> locals(FunctionDefinition functionDefinition) {
        return function2locals.getOrDefault(functionDefinition, List.of());
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("Bindings {\n");
        sb.append("  use2slot: {\n");
        use2slot.forEach((k, v) -> sb.append("    ").append(k).append(" at ").append(k.location()).append(" -> ").append(v).append("\n"));
        sb.append("  }\n");
        sb.append("  decl2slot: {\n");
        decl2slot.forEach((k, v) -> sb.append("    ").append(k).append(" -> ").append(v).append("\n"));
        sb.append("  }\n");
        sb.append("  function2locals: {\n");
        function2locals.forEach((k, v) -> sb.append("    ").append(k.name()).append(" -> ").append(v).append("\n"));
        sb.append("  }\n");
        sb.append("}");
        return sb.toString();
    }
}
