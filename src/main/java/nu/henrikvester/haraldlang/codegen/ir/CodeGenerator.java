package nu.henrikvester.haraldlang.codegen.ir;

import nu.henrikvester.haraldlang.ast.expressions.*;
import nu.henrikvester.haraldlang.ast.statements.*;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;
import nu.henrikvester.haraldlang.exceptions.NotImplementedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeGenerator implements StatementVisitor<Void>, ExpressionVisitor<IRValue> {
    private final Translator tr;
    private final Map<VarSlot, IRFrameSlot> variableMap = new HashMap<>();
    private final NameResolver nameResolver;

    public CodeGenerator(Translator translator, NameResolver resolver, List<VarSlot> locals) {
        this.tr = translator;
        this.nameResolver = resolver;
        // generate frame slots for all local variables
        for (var local : locals) {
            variableMap.put(local, new IRFrameSlot(local.id()));
        }
    }

    @Override
    public IRValue visitAddressOfExpression(AddressOfExpression expr) {
        throw new NotImplementedException();
    }

    @Override
    public IRValue visitLiteralExpression(LiteralExpression expr) {
        return tr.constInt(expr.value());
    }

    @Override
    public IRValue visitBinaryExpression(BinaryExpression expr) throws HaraldLangException {
        var a = expr.left().accept(this);
        var b = expr.right().accept(this);
        return tr.bin(BinOp.fromString(expr.op().symbol()), a, b);
    }

    @Override
    public IRValue visitVar(Var expr) {
        var slot = nameResolver.slot(expr); // resolve slot using name resolver
        var frame = variableMap.get(slot); // resolve frame from slot
        return tr.load(frame); // load value from frame (or wherever it is stored)
    }

    @Override
    public Void visitForLoopStatement(ForLoopStatement stmt) throws HaraldLangException {
        var conditionLabel = tr.label();
        var bodyLabel = tr.label();
        var endLabel = tr.label();

        stmt.initial().accept(this);
        var cond = stmt.condition().accept(this);
        tr.brz(cond, endLabel, bodyLabel);

        tr.mark(bodyLabel);
        stmt.body().accept(this);
        stmt.update().accept(this);
        tr.jmp(conditionLabel);

        tr.mark(endLabel);

        return null;
    }

    @Override
    public Void visitBlockStatement(BlockStatement block) throws HaraldLangException {
        for (var statement : block.statements()) {
            statement.accept(this);
        }
        return null;
    }

    @Override
    public Void visitDeclaration(Declaration declaration) throws HaraldLangException {
        // name resolver has already had a chance to map the variable to a slot
        // we don't need to do anything other than store the initial value if there is one
        var expr = declaration.expression();
        if (expr != null) {
            var value = expr.accept(this);
            var slot = nameResolver.slot(declaration);
            var frame = variableMap.get(slot);
            tr.store(frame, value);
        }
        return null;
    }

    @Override
    public Void visitAssignment(Assignment stmt) throws HaraldLangException {
        if (!(stmt.lvalue() instanceof Var var)) {
            throw new NotImplementedException("Can only assign to variables for now");
        }
        var slot = nameResolver.slot(var);
        var frame = variableMap.get(slot);

        var value = stmt.value().accept(this);
        tr.store(frame, value);

        return null;
    }

    @Override
    public Void visitIfStatement(IfStatement stmt) throws HaraldLangException {
        var thenBlockLabel = tr.label();
        var elseBlockLabel = stmt.elseBody() != null ? tr.label() : null;
        var endBlockLabel = tr.label();

        var cond = stmt.condition().accept(this);
        var ifZeroTarget = elseBlockLabel != null ? elseBlockLabel : endBlockLabel;
        tr.brz(cond, ifZeroTarget, thenBlockLabel);

        // then block
        tr.mark(thenBlockLabel);
        stmt.thenBody().accept(this);
        tr.jmp(endBlockLabel);

        // else block
        if (elseBlockLabel != null) {
            tr.mark(elseBlockLabel);
            stmt.elseBody().accept(this);
            tr.jmp(endBlockLabel);
        }

        tr.mark(endBlockLabel); // should we mark these? what happens otherwise?

        return null;
    }

    @Override
    public Void visitLiftedExpressionStatement(LiftedExpressionStatement stmt) throws HaraldLangException {
        stmt.expression().accept(this);
        return null;
    }

    @Override
    public Void visitPrintStatement(PrintStatement stmt) throws HaraldLangException {
        var value = stmt.expr().accept(this);
        tr.print(value);
        return null;
    }

    @Override
    public Void visitWhileStatement(WhileStatement stmt) throws HaraldLangException {
        var conditionLabel = tr.label();
        var bodyLabel = tr.label();
        var endLabel = tr.label();

        var cond = stmt.condition().accept(this);
        tr.brz(cond, endLabel, bodyLabel);

        tr.mark(bodyLabel);
        stmt.body().accept(this);
        tr.jmp(conditionLabel);

        tr.mark(endLabel);

        return null;
    }
}
