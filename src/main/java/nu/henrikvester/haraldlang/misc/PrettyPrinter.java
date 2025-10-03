package nu.henrikvester.haraldlang.misc;

import nu.henrikvester.haraldlang.ast.expressions.*;
import nu.henrikvester.haraldlang.ast.statements.*;
import nu.henrikvester.haraldlang.exceptions.HaraldMachineException;

import java.util.ArrayList;

public class PrettyPrinter implements StatementVisitor<String>, ExpressionVisitor<String> {
    private final int indentationLevel;
    private int currentIndentationLevel = 0;
    private int previousIndentationLevel = 0;

    public PrettyPrinter(int indentationLevel) {
        this.indentationLevel = indentationLevel;
    }

    public PrettyPrinter() {
        this.indentationLevel = 4;
    }

    private void indent() {
        currentIndentationLevel += indentationLevel;
    }

    private void dedent() {
        currentIndentationLevel -= indentationLevel;
    }

    private String indentation() {
        return " ".repeat(currentIndentationLevel);
    }

    private void restorePreviousIndentation() {
        currentIndentationLevel = previousIndentationLevel;
    }

    private void saveCurrentIndentation() {
        previousIndentationLevel = currentIndentationLevel;
        currentIndentationLevel = 0;
    }

    @Override
    public String visitAddressOfExpression(AddressOfExpression expr) {
        return "&" + expr.accept(this);
    }

    @Override
    public String visitLiteralExpression(LiteralExpression expr) {
        return Integer.toString(expr.value());
    }

    @Override
    public String visitBinaryExpression(BinaryExpression expr) throws HaraldMachineException {
        return expr.left().accept(this) + " " + expr.op().symbol() + " " + expr.right().accept(this);
    }

    @Override
    public String visitIdentifierExpression(IdentifierExpression expr) throws HaraldMachineException {
        return expr.identifier();
    }

    @Override
    public String visitForLoopStatement(ForLoopStatement stmt) throws HaraldMachineException {
        var prefix = indentation() + "for (";
        saveCurrentIndentation(); // don't want indentation inside the for loop header
        var header = stmt.initial().accept(this) + " " + stmt.condition().accept(this) + "; " + stmt.update().accept(this) + ") ";
        restorePreviousIndentation();
        var body = stmt.body().accept(this);
        return prefix + header + body;
    }

    @Override
    public String visitBlockStatement(BlockStatement stmt) throws HaraldMachineException {
        var lines = new ArrayList<String>();
        lines.add("{");
        indent();
        for (var statement : stmt.statements()) {
            lines.add(statement.accept(this));
        }
        dedent();
        lines.add(indentation() + "}");
        return String.join("\n", lines);
    }

    @Override
    public String visitAssignment(Assignment stmt) throws HaraldMachineException {
        return indentation() + "let " + stmt.identifier() + " = " + stmt.value().accept(this) + ";";
    }

    @Override
    public String visitIfStatement(IfStatement stmt) throws HaraldMachineException {
        var thenBody = stmt.thenBody().accept(this);
        var elseBody = stmt.elseBody() != null ? (" else ") + stmt.elseBody().accept(this) : "";
        return indentation() + "if (" + stmt.condition().accept(this) + ") " + thenBody + elseBody;
    }

    @Override
    public String visitLiftedExpressionStatement(LiftedExpressionStatement stmt) throws HaraldMachineException {
        return indentation() + stmt.expression().accept(this) + ";";
    }

    @Override
    public String visitPrintStatement(PrintStatement stmt) throws HaraldMachineException {
        return indentation() + "print " + stmt.expr().accept(this) + ";";
    }

    @Override
    public String visitWhileStatement(WhileStatement stmt) throws HaraldMachineException {
        return indentation() + "while (" + stmt.condition().accept(this) + ") " + stmt.body().accept(this);
    }
}
