package nu.henrikvester.haraldlang.codegen.ir.primitives.instructions;

import nu.henrikvester.haraldlang.codegen.ir.primitives.Use;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;

import java.util.List;

public sealed interface IRInst permits Bin, Load, Mov, Phi, Print, Store {
    // TODO implement equals and hashCode? Where are instructions compared or stored in sets/maps?
    List<Use> operands();

    boolean definesTemp(); // a temp as a result of this instruction

    IRTemp definedTemp();

    void setResult(IRTemp temp);

    List<IRTemp> defs();
}
