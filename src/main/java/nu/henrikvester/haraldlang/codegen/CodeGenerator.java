package nu.henrikvester.haraldlang.codegen;

import nu.henrikvester.haraldlang.ast.expressions.*;
import nu.henrikvester.haraldlang.ast.statements.*;
import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;

public class CodeGenerator implements StatementVisitor<Void>, ExpressionVisitor<Void> {

    @Override
    public Void visitAddressOfExpression(AddressOfExpression expr) {
        return null;
    }

    @Override
    public Void visitLiteralExpression(LiteralExpression expr) {
        return null;
    }

    @Override
    public Void visitBinaryExpression(BinaryExpression expr) throws HaraldMachineException {
        return null;
    }

    @Override
    public Void visitIdentifierExpression(IdentifierExpression expr) throws HaraldMachineException {
        return null;
    }

    @Override
    public Void visitForLoopStatement(ForLoopStatement stmt) throws HaraldMachineException {
        return null;
    }

    @Override
    public Void visitBlockStatement(BlockStatement stmt) throws HaraldMachineException {
        return null;
    }

    @Override
    public Void visitAssignment(Assignment stmt) throws HaraldMachineException {
        return null;
    }

    @Override
    public Void visitIfStatement(IfStatement stmt) throws HaraldMachineException {
        return null;
    }

    @Override
    public Void visitLiftedExpressionStatement(LiftedExpressionStatement stmt) throws HaraldMachineException {
        return null;
    }

    @Override
    public Void visitPrintStatement(PrintStatement stmt) throws HaraldMachineException {
        return null;
    }

    @Override
    public Void visitWhileStatement(WhileStatement stmt) throws HaraldMachineException {
        return null;
    }
}
