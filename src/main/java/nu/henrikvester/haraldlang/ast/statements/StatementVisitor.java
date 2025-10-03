package nu.henrikvester.haraldlang.ast.statements;

import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;

public interface StatementVisitor<R> {
    R visitForLoopStatement(ForLoopStatement stmt) throws HaraldMachineException;

    R visitBlockStatement(BlockStatement stmt) throws HaraldMachineException;

    R visitAssignment(Assignment stmt) throws HaraldMachineException;

    R visitIfStatement(IfStatement stmt) throws HaraldMachineException;

    R visitLiftedExpressionStatement(LiftedExpressionStatement stmt) throws HaraldMachineException;

    R visitPrintStatement(PrintStatement stmt) throws HaraldMachineException;

    R visitWhileStatement(WhileStatement stmt) throws HaraldMachineException;
}
