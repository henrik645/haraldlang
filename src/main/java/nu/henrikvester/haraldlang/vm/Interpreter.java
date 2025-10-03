package nu.henrikvester.haraldlang.vm;

import nu.henrikvester.haraldlang.ast.expressions.*;
import nu.henrikvester.haraldlang.ast.statements.*;
import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;
import nu.henrikvester.haraldlang.exceptions.NotImplementedException;

class Interpreter implements StatementVisitor<Void>, ExpressionVisitor<Word> {
    private final Environment environment;

    public Interpreter(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Void visitForLoopStatement(ForLoopStatement stmt) throws HaraldMachineException {
        stmt.initial().accept(this);
        while (stmt.condition().accept(this).isTruthy()) {
            stmt.body().accept(this);
            stmt.update().accept(this);
        }
        return null;
    }

    @Override
    public Void visitBlockStatement(BlockStatement block) throws HaraldMachineException {
        for (var stmt : block.statements()) {
            stmt.accept(this);
        }
        return null;
    }

    @Override
    public Void visitAssignment(Assignment stmt) throws HaraldMachineException {
        environment.set(stmt.identifier(), stmt.value().accept(this));
        return null;
    }

    @Override
    public Void visitIfStatement(IfStatement stmt) throws HaraldMachineException {
        if (stmt.condition().accept(this).isTruthy()) {
            stmt.thenBody().accept(this);
        } else if (stmt.elseBody() != null) {
            stmt.elseBody().accept(this);
        }
        return null;
    }

    @Override
    public Void visitLiftedExpressionStatement(LiftedExpressionStatement stmt) throws HaraldMachineException {
        stmt.expression().accept(this);
        return null;
    }

    @Override
    public Void visitPrintStatement(PrintStatement stmt) throws HaraldMachineException {
        System.out.println("[OUTPUT] " + stmt.expr().accept(this));
        return null;
    }

    @Override
    public Void visitWhileStatement(WhileStatement stmt) throws HaraldMachineException {
        while (stmt.condition().accept(this).isTruthy()) {
            stmt.body().accept(this);
        }
        return null;
    }

    @Override
    public Word visitAddressOfExpression(AddressOfExpression expr) {
        throw new NotImplementedException();
    }

    @Override
    public Word visitLiteralExpression(LiteralExpression expr) {
        return new Word(expr.value());
    }

    @Override
    public Word visitBinaryExpression(BinaryExpression expr) throws HaraldMachineException {
        return expr.op().apply(expr.left().accept(this), expr.right().accept(this));
    }

    @Override
    public Word visitIdentifierExpression(IdentifierExpression expr) throws HaraldMachineException {
        var ret = environment.get(expr.identifier());
        if (ret == null) {
            throw HaraldMachineException.undefinedVariable(expr.identifier(), expr.location());
        }
        return ret;
    }
}