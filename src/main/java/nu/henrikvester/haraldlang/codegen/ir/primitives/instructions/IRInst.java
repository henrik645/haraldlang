package nu.henrikvester.haraldlang.codegen.ir.primitives.instructions;

import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.List;

public sealed interface IRInst permits Bin, Load, Mov, Phi, Print, Store {
    List<IRValue> uses();

    List<IRTemp> defs();
}

