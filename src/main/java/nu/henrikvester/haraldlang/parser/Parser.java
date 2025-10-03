package nu.henrikvester.haraldlang.parser;

import nu.henrikvester.haraldlang.ast.expressions.*;
import nu.henrikvester.haraldlang.ast.statements.*;
import nu.henrikvester.haraldlang.core.SourceLocation;
import nu.henrikvester.haraldlang.core.Token;
import nu.henrikvester.haraldlang.core.TokenType;
import nu.henrikvester.haraldlang.exceptions.ParserException;
import nu.henrikvester.haraldlang.exceptions.TokenizerException;
import nu.henrikvester.haraldlang.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final static boolean DEBUG = false;
    private final List<Token> tokens = new ArrayList<>();
    private SourceLocation lastLocation;

    public Parser(String input) throws TokenizerException {
        var tokenizer = new Tokenizer(input);
        while (true) {
            var token = tokenizer.getNextToken();
            if (DEBUG) {
                System.out.println(token);
                token.location().pointOut(input, null);
                System.out.println();
            }
            tokens.add(token);
            if (token.type() == TokenType.EOF) break;
        }
    }

    private Token pop() {
        Token ret = tokens.remove(0);
        lastLocation = ret.location();
        return ret;
    }

    private Token peek() {
        if (tokens.isEmpty()) return null; // I don't think this ever happens? EOF should be the last token
        return tokens.get(0);
    }

    public Expression parseExpression() throws ParserException {
        var token = pop();
        var left = switch (token.type()) {
            case AMPERSAND -> new AddressOfExpression(parseVariable(), token.location());
            case ASTERISK -> throw ParserException.notImplementedYet("Dereference operator", token.location());
            case NUMBER -> new LiteralExpression(Integer.parseInt(token.lexeme()), token.location());
            case IDENTIFIER -> new Var(token.lexeme(), token.location());
            default -> throw ParserException.unexpectedToken("expression", token.type().name(), token.location());
        };

        var operator = peek();
        if (operator == null) {
            return left;
        }
        if (BinaryOperators.tokenTypeIsOperator(operator.type())) {
            var op = pop(); // pop operator
            var right = parseExpression();
            return new BinaryExpression(left, BinaryOperators.fromTokenType(operator.type()), right, op.location());
        } else {
            return left;
        }
    }

    public Statement parse() throws ParserException {
        var ret = parseStatement();
        var next = peek();
        if (next != null && next.type() != TokenType.EOF) {
            throw ParserException.unexpectedToken("end of file", next.type().name(), next.location());
        }
        return ret;
    }

    private Statement parseStatement() throws ParserException {
        var next = peek();
        if (next == null) {
            throw ParserException.unexpectedEndOfInput(lastLocation);
        }
        switch (next.type()) {
            case KEYWORD_DECLARE -> {
                var kw = pop();
                var var = parseVariable();
                var equals = peek();
                if (equals != null && equals.type() == TokenType.EQUALS) {
                    pop(); // pop '='
                    var expression = parseExpression();
                    parseExact(TokenType.SEMICOLON);
                    return new Declaration(var.identifier(), expression, kw.location());
                }
                parseExact(TokenType.SEMICOLON);
                return new Declaration(var.identifier(), null, kw.location());
            }
            case KEYWORD_LET -> {
                pop();
                var var = parseVariable();
                parseExact(TokenType.EQUALS);
                var expression = parseExpression();
                parseExact(TokenType.SEMICOLON);

                return new Assignment(var, expression);
            }
            case KEYWORD_PRINT -> {
                pop();
                var exp = parseExpression();
                parseExact(TokenType.SEMICOLON);
                return new PrintStatement(exp);
            }
            case KEYWORD_IF -> {
                return parseIfStatement();
            }
            case KEYWORD_WHILE -> {
                return parseWhileStatement();
            }
            case KEYWORD_FOR -> {
                return parseForLoopStatement();
            }
            case IDENTIFIER, NUMBER -> {
                return liftExpression();
            }
            case LBRACE -> {
                var opening = pop(); // pop '{'
                var statements = new ArrayList<Statement>();
                while (peek() != null && peek().type() != TokenType.RBRACE) {
                    statements.add(parseStatement());
                }
                parseExact(TokenType.RBRACE);
                return new BlockStatement(statements, opening.location());
            }
            default -> throw ParserException.unexpectedToken("statement", next.type().name(), next.location());
        }
    }

    private IfStatement parseIfStatement() throws ParserException {
        var kw = pop(); // pop 'if'
        var condition = parseParenthesizedExpression();
        var thenBody = parseStatement();
        var next = peek();
        if (next.type() == TokenType.KEYWORD_ELSE) {
            pop(); // pop 'else'
            var elseBody = parseStatement();
            return new IfStatement(condition, thenBody, elseBody, kw.location());
        } else {
            return new IfStatement(condition, thenBody, kw.location());
        }
    }

    private WhileStatement parseWhileStatement() throws ParserException {
        var kw = pop(); // pop 'while'
        var condition = parseParenthesizedExpression();
        var body = parseStatement();
        return new WhileStatement(condition, body, kw.location());
    }

    private ForLoopStatement parseForLoopStatement() throws ParserException {
        var kw = pop(); // pop 'for'
        parseExact(TokenType.LPAREN);
        var initializer = parseStatement();
        var condition = parseExpression();
        parseExact(TokenType.SEMICOLON);
        var increment = parseStatement();
        parseExact(TokenType.RPAREN);
        var body = parseStatement();
        return new ForLoopStatement(initializer, condition, increment, body, kw.location());
    }

    private Expression parseParenthesizedExpression() throws ParserException {
        parseExact(TokenType.LPAREN);
        var condition = parseExpression();
        parseExact(TokenType.RPAREN);
        return condition;
    }

    private Statement liftExpression() throws ParserException {
        var exp = parseExpression();
        parseExact(TokenType.SEMICOLON);
        return new LiftedExpressionStatement(exp);
    }

    private Var parseVariable() throws ParserException {
        var token = pop();
        if (token.type() != TokenType.IDENTIFIER) {
            throw ParserException.unexpectedToken("IDENTIFIER", token.type().name(), token.location());
        }
        return new Var(token.lexeme(), token.location());
    }

    private void parseExact(TokenType tokenType) throws ParserException {
        var token = pop();
        if (token.type() != tokenType) {
            throw ParserException.unexpectedToken(tokenType.name(), token.type().name(), token.location());
        }
    }
}
