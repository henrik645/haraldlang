package nu.henrikvester.haraldlang.vm;

import nu.henrikvester.haraldlang.ast.expressions.*;
import nu.henrikvester.haraldlang.ast.lvalue.LValueVisitor;
import nu.henrikvester.haraldlang.ast.statements.*;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;
import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;
import nu.henrikvester.haraldlang.exceptions.NotImplementedException;

class Interpreter implements StatementVisitor<Void>, ExpressionVisitor<Word>, LValueVisitor<Word> {
    private final Environment environment;

    public Interpreter(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Void visitForLoopStatement(ForLoopStatement stmt) throws HaraldLangException {
        stmt.initial().accept(this);
        while (stmt.condition().accept(this).isTruthy()) {
            stmt.body().accept(this);
            stmt.update().accept(this);
        }
        return null;
    }

    @Override
    public Void visitBlockStatement(BlockStatement block) throws HaraldLangException {
        for (var stmt : block.statements()) {
            stmt.accept(this);
        }
        return null;
    }

    @Override
    public Void visitDeclaration(Declaration declaration) throws HaraldLangException {
        Word value;
        if (declaration.expression() != null) {
            value = declaration.expression().accept(this);
        } else {
            value = null;
        }
        environment.set(declaration.identifier(), value);
        return null;
    }
    
    @Override
    public Void visitAssignment(Assignment stmt) throws HaraldLangException {
        // must be previously declared
        if (!(stmt.lvalue() instanceof Var var)) {
            throw new NotImplementedException(); // TODO support other lvalues (like dereference)
        }
        if (!environment.isDeclared(var.identifier())) {
            throw HaraldMachineException.undefinedVariable(stmt.lvalue());
        }
        environment.set(var.identifier(), stmt.value().accept(this));
        return null;
    }

    @Override
    public Void visitIfStatement(IfStatement stmt) throws HaraldLangException {
        if (stmt.condition().accept(this).isTruthy()) {
            stmt.thenBody().accept(this);
        } else if (stmt.elseBody() != null) {
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
        System.out.println("[OUTPUT] " + stmt.expr().accept(this));
        return null;
    }

    @Override
    public Void visitWhileStatement(WhileStatement stmt) throws HaraldLangException {
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
    public Word visitBinaryExpression(BinaryExpression expr) throws HaraldLangException {
        return expr.op().apply(expr.left().accept(this), expr.right().accept(this));
    }

    @Override
    public Word visitVar(Var var) throws HaraldLangException {
        if (!environment.isDeclared(var.identifier())) {
            throw HaraldMachineException.undefinedVariable(var);
        }
        var ret = environment.get(var.identifier());
        if (ret == null) {
            throw HaraldMachineException.uninitializedVariable(var);
        }
        return ret;
    }
}