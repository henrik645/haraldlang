package nu.henrikvester.haraldlang.misc;

import nu.henrikvester.haraldlang.ast.definitions.FunctionDefinition;
import nu.henrikvester.haraldlang.ast.expressions.*;
import nu.henrikvester.haraldlang.ast.statements.*;
import nu.henrikvester.haraldlang.core.Diagnostic;
import nu.henrikvester.haraldlang.core.DiagnosticCode;
import nu.henrikvester.haraldlang.core.SourceLocation;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;
import nu.henrikvester.haraldlang.exceptions.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public class Linter implements StatementVisitor<Void>, ExpressionVisitor<Void> {
    private final List<Diagnostic> diagnostics = new ArrayList<>();

    public static List<Diagnostic> lint(FunctionDefinition functionDefinition) {
        try {
            var linter = new Linter();
            functionDefinition.body().accept(linter);
            return linter.diagnostics;
        } catch (HaraldLangException e) {
            throw new RuntimeException("Linting should not throw HaraldLangExceptions", e);
        }
    }

    private void warning(DiagnosticCode code, SourceLocation location) {
        diagnostics.add(Diagnostic.warning(code, location));
    }

    @Override
    public Void visitAddressOfExpression(AddressOfExpression expr) {
        throw new NotImplementedException();
    }

    @Override
    public Void visitLiteralExpression(LiteralExpression expr) {
        return null;
    }

    @Override
    public Void visitBinaryExpression(BinaryExpression expr) throws HaraldLangException {
        expr.left().accept(this);
        expr.right().accept(this);
        return null;
    }

    @Override
    public Void visitVar(Var expr) throws HaraldLangException {
        return null;
    }

    @Override
    public Void visitForLoopStatement(ForLoopStatement stmt) throws HaraldLangException {
        stmt.initial().accept(this);
        stmt.condition().accept(this);
        if (stmt.initial() instanceof LiftedExpressionStatement) {
            warning(DiagnosticCode.SUSPICIOUS_FOR_LOOP_INITIAL_STATEMENT, stmt.initial().getLocation());
        }
        if (stmt.update() instanceof LiftedExpressionStatement) {
            warning(DiagnosticCode.SUSPICIOUS_FOR_LOOP_UPDATE_STATEMENT, stmt.update().getLocation());
        }
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
        declaration.expression().accept(this);
        return null;
    }

    @Override
    public Void visitAssignment(Assignment stmt) throws HaraldLangException {
        stmt.value().accept(this);
        return null;
    }

    @Override
    public Void visitIfStatement(IfStatement stmt) throws HaraldLangException {
        stmt.condition().accept(this);
        stmt.thenBody().accept(this);
        if (stmt.elseBody() != null)
            stmt.elseBody().accept(this);
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
        stmt.condition().accept(this);
        stmt.body().accept(this);
        return null;
    }
}
