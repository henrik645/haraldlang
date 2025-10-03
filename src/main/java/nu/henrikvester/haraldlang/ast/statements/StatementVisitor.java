package nu.henrikvester.haraldlang.ast.statements;

import nu.henrikvester.haraldlang.exceptions.HaraldLangException;

public interface StatementVisitor<R> {
    R visitForLoopStatement(ForLoopStatement stmt) throws HaraldLangException;

    R visitBlockStatement(BlockStatement stmt) throws HaraldLangException;

    R visitDeclaration(Declaration declaration) throws HaraldLangException;

    R visitAssignment(Assignment stmt) throws HaraldLangException;

    R visitIfStatement(IfStatement stmt) throws HaraldLangException;

    R visitLiftedExpressionStatement(LiftedExpressionStatement stmt) throws HaraldLangException;

    R visitPrintStatement(PrintStatement stmt) throws HaraldLangException;

    R visitWhileStatement(WhileStatement stmt) throws HaraldLangException;
}
