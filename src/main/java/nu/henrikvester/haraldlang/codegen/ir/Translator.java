package nu.henrikvester.haraldlang.codegen.ir;

public interface Translator {
    IRConst constInt(int v);

    // Value creators
    IRTemp temp();

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
}

