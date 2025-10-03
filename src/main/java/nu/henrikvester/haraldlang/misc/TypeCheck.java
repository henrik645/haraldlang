package nu.henrikvester.haraldlang.misc;

//final class TypeCheck implements ExprVisitor<Type>, StmtVisitor<Void> {
//    private final Deque<Map<String,Type>> scopes = new ArrayDeque<>();
//    private final List<Diag> diags = new ArrayList<>();
//    private Type currentReturn;
//
//    void enterScope() { scopes.push(new HashMap<>()); }
//    void exitScope()  { scopes.pop(); }
//
//    @Override public Type visit(Var v) {
//        var t = lookup(v.name());
//        if (t == null) diags.add(Diag.undefined(v.name(), v.span()));
//        return t != null ? t : Type.ERROR;
//    }
//    // ...
//}

//final class Lowering implements ExprVisitor<IRValue>, StmtVisitor<Void> {
//    private final FunctionBuilder fb;
//    private final Map<String, IRValue> env = new HashMap<>();
//    private BasicBlock cur;
//
//    Lowering(FunctionBuilder fb) { this.fb = fb; this.cur = fb.entry(); }
//
//    @Override public IRValue visit(BinOp n) {
//        var a = n.left().accept(this);
//        var b = n.right().accept(this);
//        var t = fb.newTemp();
//        fb.emit(new Bin(t, opOf(n.op()), a, b));
//        return t;
//    }
//    // ...
//}
