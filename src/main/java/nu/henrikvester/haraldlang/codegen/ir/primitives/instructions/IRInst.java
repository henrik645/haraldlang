package nu.henrikvester.haraldlang.codegen.ir.primitives.instructions;

import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.List;

// TODO is dst, inputs, temps really needed?
public sealed interface IRInst permits Bin, Load, Mov, Phi, Print, Store {
    /**
     * Destination of this instruction, or null if none
     *
     * @return Destination temp or null
     */
    IRTemp dst();

    /**
     * All inputs to this instruction
     *
     * @return List of inputs
     */
    List<IRValue> inputs();

    /**
     * All inputs that are temps (i.e., not constants)
     *
     * @return List of input temps
     */
    default List<IRTemp> temps() {
        return inputs().stream().filter(IRTemp.class::isInstance).map(IRTemp.class::cast).toList();
    }
}

