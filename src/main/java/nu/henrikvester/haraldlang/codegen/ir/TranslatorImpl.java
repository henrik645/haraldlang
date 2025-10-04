package nu.henrikvester.haraldlang.codegen.ir;

import lombok.RequiredArgsConstructor;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.*;
import nu.henrikvester.haraldlang.codegen.ir.primitives.terminators.*;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRConst;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRParam;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRTemp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;

import java.util.Objects;

@RequiredArgsConstructor
public class TranslatorImpl implements Translator {
    private final FunctionBuilder fb;

    @Override
    public IRConst constInt(int v) {
        return new IRConst(v);
    }

    @Override
    public IRTemp temp() {
        return fb.newTemp();
    }

    @Override
    public Label label(String purpose) {
        return fb.newLabel(purpose);
    }

    @Override
    public IRTemp bin(BinOp op, IRValue a, IRValue b) {
        var t = fb.newTemp();
        fb.emit(new Bin(t, op, a, b));
        return t;
    }

    @Override
    public IRTemp mov(IRValue src) {
        var t = fb.newTemp();
        fb.emit(new Mov(t, src));
        return t;
    }

    @Override
    public IRTemp load(IRValue addr) {
        var t = fb.newTemp();
        fb.emit(new Load(t, addr));
        return t;
    }

    @Override
    public void store(IRValue addr, IRValue src) {
        fb.emit(new Store(Objects.requireNonNull(addr), Objects.requireNonNull(src)));
    }

    @Override
    public void brz(IRValue cond, Label ifZero, Label ifNotZero) {
        fb.endWith(new BrZ(cond, ifZero, ifNotZero));
    }

    @Override
    public void jmp(Label target) {
        fb.endWith(new Jmp(target));
    }

    @Override
    public void hlt() {
        fb.endWith(new Hlt());
    }

    @Override
    public void mark(Label label) {
        fb.mark(label);
    }

    @Override
    public void print(IRValue value) {
        fb.emit(new Print(value));
    }

    /**
     * Get the i'th parameter (0-based)
     *
     * @param i index of the parameter
     * @return an IRValue representing the parameter
     */
    @Override
    public IRValue param(int i) {
        return new IRParam(i);
    }

    @Override
    public void returnVoid() {
        fb.endWith(new RetVoid());
    }

    @Override
    public void ret(IRValue returnValue) {
        fb.endWith(new Ret(returnValue));
    }
}
