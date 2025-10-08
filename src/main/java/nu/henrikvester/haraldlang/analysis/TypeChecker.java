package nu.henrikvester.haraldlang.analysis;

import lombok.RequiredArgsConstructor;
import nu.henrikvester.haraldlang.ast.definitions.FunctionDefinition;
import nu.henrikvester.haraldlang.ast.expressions.*;
import nu.henrikvester.haraldlang.ast.statements.*;
import nu.henrikvester.haraldlang.ast.types.HLBool;
import nu.henrikvester.haraldlang.ast.types.HLInt;
import nu.henrikvester.haraldlang.ast.types.HLType;
import nu.henrikvester.haraldlang.exceptions.CompilerException;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;
import nu.henrikvester.haraldlang.exceptions.NotImplementedException;

@RequiredArgsConstructor
public class TypeChecker implements StatementVisitor<Void>, ExpressionVisitor<HLType> {
    private final Bindings bindings;

    public void typeCheck(FunctionDefinition functionDefinition) throws HaraldLangException {
        functionDefinition.body().accept(this);
        // TODO: type check function return type
    }

    @Override
    public HLType visitAddressOfExpression(AddressOfExpression expr) {
        throw new NotImplementedException();
    }

    @Override
    public HLType visitLiteralExpression(LiteralExpression expr) {
        return new HLInt();
    }

    @Override
    public HLType visitBinaryExpression(BinaryExpression expr) throws HaraldLangException {
        var op = expr.op();
        assertType(op.leftType(), expr.left());
        assertType(op.rightType(), expr.right());
        return op.resultType();
    }

    @Override
    public HLType visitVar(Var expr) {
        return bindings.slot(expr).type();
    }

    private void assertType(HLType expected, Expression expr) throws HaraldLangException {
        var actual = expr.accept(this);
        if (!expected.equals(actual)) {
            throw CompilerException.typeMismatch(expected, actual, expr);
        }
    }

    @Override
    public Void visitForLoopStatement(ForLoopStatement stmt) throws HaraldLangException {
        stmt.initial().accept(this);
        stmt.condition().accept(this);
        stmt.update().accept(this);
        stmt.body().accept(this);
        return null;
    }

    @Override
    public Void visitBlockStatement(BlockStatement stmt) throws HaraldLangException {
        for (var s : stmt.statements()) {
            s.accept(this);
        }
        return null;
    }

    @Override
    public Void visitDeclaration(Declaration declaration) throws HaraldLangException {
        if (declaration.expression() == null) return null;
        var type = bindings.slot(declaration).type(); // lookup the HLType this TypeUse refers to
        assertType(type, declaration.expression());
        return null;
    }

    @Override
    public Void visitAssignment(Assignment stmt) throws HaraldLangException {
        if (!(stmt.lvalue() instanceof Var var)) {
            throw new NotImplementedException("Only variable assignments are supported in type checker");
        }
        assertType(bindings.slot(var).type(), stmt.value());
        return null;
    }

    @Override
    public Void visitIfStatement(IfStatement stmt) throws HaraldLangException {
        assertType(new HLBool(), stmt.condition());
        stmt.thenBody().accept(this);
        if (stmt.elseBody() != null) {
            stmt.elseBody().accept(this);
        }
        return null;
    }

    @Override
    public Void visitLiftedExpressionStatement(LiftedExpressionStatement stmt) throws HaraldLangException {
        stmt.expression().accept(this);
        return null;
    }

    @Override
    public Void visitPrintStatement(PrintStatement stmt) throws HaraldLangException {
        stmt.expr().accept(this);
        return null;
    }

    @Override
    public Void visitWhileStatement(WhileStatement stmt) throws HaraldLangException {
        assertType(new HLBool(), stmt.condition());
        stmt.body().accept(this);
        return null;
    }
}
