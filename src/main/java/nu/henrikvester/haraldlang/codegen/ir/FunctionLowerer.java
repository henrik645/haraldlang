package nu.henrikvester.haraldlang.codegen.ir;

import lombok.RequiredArgsConstructor;
import nu.henrikvester.haraldlang.ast.expressions.*;
import nu.henrikvester.haraldlang.ast.statements.*;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.Bin;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.BinOp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.instructions.Print;
import nu.henrikvester.haraldlang.codegen.ir.primitives.terminators.BrZ;
import nu.henrikvester.haraldlang.codegen.ir.primitives.terminators.Jmp;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRConst;
import nu.henrikvester.haraldlang.codegen.ir.primitives.values.IRValue;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;
import nu.henrikvester.haraldlang.exceptions.NotImplementedException;

@RequiredArgsConstructor
public class FunctionLowerer implements StatementVisitor<Void>, ExpressionVisitor<IRValue> {
    private final FunctionBuilder b;

    @Override
    public IRValue visitAddressOfExpression(AddressOfExpression expr) {
        throw new NotImplementedException();
    }

    @Override
    public IRValue visitLiteralExpression(LiteralExpression expr) {
        return new IRConst(expr.value());
    }

    @Override
    public IRValue visitBinaryExpression(BinaryExpression expr) throws HaraldLangException {
        var binOp = BinOp.fromString(expr.op().symbol());
        var left = expr.left().accept(this);
        var right = expr.right().accept(this);
        var res = b.newTemp();
        b.emit(new Bin(res, binOp, left, right));
        return res;
    }

    @Override
    public IRValue visitVar(Var expr) throws HaraldLangException {
        return b.readVar(expr);
    }

    @Override
    public Void visitForLoopStatement(ForLoopStatement stmt) throws HaraldLangException {
        var conditionLabel = b.newLabel("for_cond");
        var bodyLabel = b.newLabel("for_body");
        var endLabel = b.newLabel("for_end");

        stmt.initial().accept(this);
        b.endWith(new Jmp(conditionLabel));

        b.mark(conditionLabel);
        var cond = stmt.condition().accept(this);
        b.endWith(new BrZ(cond, endLabel, bodyLabel));

        b.mark(bodyLabel);
        stmt.body().accept(this);
        stmt.update().accept(this);
        b.endWith(new Jmp(conditionLabel));

        b.mark(endLabel);

        return null;
    }

    @Override
    public Void visitBlockStatement(BlockStatement block) throws HaraldLangException {
        for (var s : block.statements()) {
            s.accept(this);
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
            b.setVar(declaration, value);
        }
        return null;
    }

    @Override
    public Void visitAssignment(Assignment stmt) throws HaraldLangException {
        var value = stmt.value().accept(this);
        if (!(stmt.lvalue() instanceof Var var)) {
            throw new NotImplementedException("Can only assign to variables for now");
        }
        b.setVar(var, value);
        return null;
    }

    @Override
    public Void visitIfStatement(IfStatement stmt) throws HaraldLangException {
        var thenBlockLabel = b.newLabel("if_then");
        var elseBlockLabel = stmt.elseBody() != null ? b.newLabel("if_else") : null;
        var endBlockLabel = b.newLabel("if_end");

        var cond = stmt.condition().accept(this);
        var ifZeroTarget = elseBlockLabel != null ? elseBlockLabel : endBlockLabel;
        b.endWith(new BrZ(cond, ifZeroTarget, thenBlockLabel));

        // then block
        b.mark(thenBlockLabel);
        stmt.thenBody().accept(this);
        b.endWith(new Jmp(endBlockLabel));

        // else block
        if (elseBlockLabel != null) {
            b.mark(elseBlockLabel);
            stmt.elseBody().accept(this);
            b.endWith(new Jmp(endBlockLabel));
        }

        b.mark(endBlockLabel);

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
        b.emit(new Print(value));
        return null;
    }

    @Override
    public Void visitWhileStatement(WhileStatement stmt) throws HaraldLangException {
        var conditionLabel = b.newLabel("while_cond");
        var bodyLabel = b.newLabel("while_body");
        var endLabel = b.newLabel("while_end");

        b.endWith(new Jmp(conditionLabel));

        b.mark(conditionLabel);
        var cond = stmt.condition().accept(this);
        b.endWith(new BrZ(cond, endLabel, bodyLabel));

        b.mark(bodyLabel);
        stmt.body().accept(this);
        b.endWith(new Jmp(conditionLabel));

        b.mark(endLabel);

        return null;

    }
}
