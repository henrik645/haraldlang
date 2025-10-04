package nu.henrikvester.haraldlang.codegen.ir;

import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.BinOp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRConst;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

/**
 * The translator slightly abstracts {@link FunctionBuilder} so that it is easier to use
 * by providing emission helpers and separating instructions vs. terminators.
 */
public interface Translator {
    IRConst constInt(int v);

    // Value creators
    Label label(String purpose);

    // Emission helpers
    IRTemp bin(BinOp op, IRValue a, IRValue b);

    IRTemp mov(IRValue src);

    IRTemp load(IRValue addr);

    // Just for debugging purposes
    void print(IRValue value);

    void store(IRValue addr, IRValue src);

    void brz(IRValue cond, Label ifZero, Label ifNotZero);

    void jmp(Label target);

    void hlt();

    void mark(Label label);

    IRValue param(int i);

    void returnVoid();

    void ret(IRValue returnValue);
}

