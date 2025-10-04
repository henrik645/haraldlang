package nu.henrikvester.haraldlang.misc;

import nu.henrikvester.haraldlang.ast.definitions.DefinitionVisitor;
import nu.henrikvester.haraldlang.ast.definitions.FunctionDefinition;
import nu.henrikvester.haraldlang.ast.expressions.*;
import nu.henrikvester.haraldlang.ast.statements.*;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;
import nu.henrikvester.haraldlang.exceptions.NotImplementedException;

import java.util.ArrayList;

//final class Pretty implements ExprVisitor<String>, StmtVisitor<Void> {
//    private final StringBuilder out = new StringBuilder();
//    private int indent = 0;
//    private void nl() { out.append('\n').append("  ".repeat(indent)); }
//    // ...
//}

public class PrettyPrinter implements StatementVisitor<String>, ExpressionVisitor<String>, DefinitionVisitor<String> {
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
    public String visitBinaryExpression(BinaryExpression expr) throws HaraldLangException {
        return expr.left().accept(this) + " " + expr.op().symbol() + " " + expr.right().accept(this);
    }

    @Override
    public String visitVar(Var expr) {
        return expr.identifier();
    }

    @Override
    public String visitForLoopStatement(ForLoopStatement stmt) throws HaraldLangException {
        var prefix = indentation() + "for (";
        saveCurrentIndentation(); // don't want indentation inside the for loop header
        var header = stmt.initial().accept(this) + " " + stmt.condition().accept(this) + "; " + stmt.update().accept(this) + ") ";
        restorePreviousIndentation();
        var body = stmt.body().accept(this);
        return prefix + header + body;
    }

    @Override
    public String visitBlockStatement(BlockStatement stmt) throws HaraldLangException {
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
    public String visitAssignment(Assignment stmt) throws HaraldLangException {
        if (!(stmt.lvalue() instanceof Var var)) {
            throw new NotImplementedException("Only variable assignments are supported in pretty printer");
        }
        return indentation() + "let " + var.identifier() + " = " + stmt.value().accept(this) + ";";
    }

    @Override
    public String visitIfStatement(IfStatement stmt) throws HaraldLangException {
        var thenBody = stmt.thenBody().accept(this);
        var elseBody = stmt.elseBody() != null ? (" else ") + stmt.elseBody().accept(this) : "";
        return indentation() + "if (" + stmt.condition().accept(this) + ") " + thenBody + elseBody;
    }

    @Override
    public String visitLiftedExpressionStatement(LiftedExpressionStatement stmt) throws HaraldLangException {
        return indentation() + stmt.expression().accept(this) + ";";
    }

    @Override
    public String visitPrintStatement(PrintStatement stmt) throws HaraldLangException {
        return indentation() + "print " + stmt.expr().accept(this) + ";";
    }

    @Override
    public String visitWhileStatement(WhileStatement stmt) throws HaraldLangException {
        return indentation() + "while (" + stmt.condition().accept(this) + ") " + stmt.body().accept(this);
    }

    @Override
    public String visitDeclaration(Declaration declaration) throws HaraldLangException {
        var expr = declaration.expression() != null ? " = " + declaration.expression().accept(this) : "";
        return indentation() + "declare " + declaration.identifier() + expr + ";";
    }

    @Override
    public String visitFunctionDefinition(FunctionDefinition functionDefinition) throws HaraldLangException {
        var paramList = new ArrayList<String>();
        for (var param : functionDefinition.parameters()) {
            paramList.add(param.accept(this));
        }
        var paramStr = paramList.isEmpty() ? "" : String.join(", ", paramList);
        return "fun " + functionDefinition.name() + "(" + paramStr + ") " + functionDefinition.body().accept(this);
    }
}
