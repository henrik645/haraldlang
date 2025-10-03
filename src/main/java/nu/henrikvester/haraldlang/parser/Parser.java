package nu.henrikvester.haraldlang.parser;

import nu.henrikvester.haraldlang.ast.expressions.*;
import nu.henrikvester.haraldlang.ast.statements.*;
import nu.henrikvester.haraldlang.core.*;
import nu.henrikvester.haraldlang.exceptions.*;
import nu.henrikvester.haraldlang.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens = new ArrayList<>();
    private SourceLocation lastLocation;
    private final static boolean DEBUG = false;

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
            case AMPERSAND -> new AddressOfExpression(parseIdentifier());
            case ASTERISK -> throw ParserException.notImplementedYet("Dereference operator", token.location());
            case NUMBER -> new LiteralExpression(Integer.parseInt(token.lexeme()));
            case IDENTIFIER -> new IdentifierExpression(token.lexeme(), token.location());
            default -> throw ParserException.unexpectedToken("expression", token.type().name(), token.location());
        };
        
        var operator = peek();
        if (operator == null) {
            return left;
        }
        if (BinaryOperators.tokenTypeIsOperator(operator.type())) {
            pop(); // pop operator
            var right = parseExpression();
            return new BinaryExpression(left, BinaryOperators.fromTokenType(operator.type()), right);
        } else {
            return left;
        }
    }
    
    public Statement parseStatement() throws ParserException {
        var next = peek();
        if (next == null) {
            throw ParserException.unexpectedEndOfInput(lastLocation);
        }
        switch (next.type()) {
            case KEYWORD_LET -> {
                pop();
                var identifier = parseIdentifier();
                parseExact(TokenType.EQUALS);
                var expression = parseExpression();
                parseExact(TokenType.SEMICOLON);
                
                return new Assignment(identifier, expression);
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
                pop(); // pop '{'
                var statements = new ArrayList<Statement>();
                while (peek() != null && peek().type() != TokenType.RBRACE) {
                    statements.add(parseStatement());
                }
                parseExact(TokenType.RBRACE);
                return new BlockStatement(statements);
            }
            default -> throw ParserException.unexpectedToken("statement", next.type().name(), next.location());
        }
    }
    
    private IfStatement parseIfStatement() throws ParserException {
        pop(); // pop 'if'
        var condition = parseParenthesizedExpression();
        var thenBody = parseStatement();
        var next = peek();
        if (next.type() == TokenType.KEYWORD_ELSE) {
            pop(); // pop 'else'
            var elseBody = parseStatement();
            return new IfStatement(condition, thenBody, elseBody);
        } else {
            return new IfStatement(condition, thenBody);
        }
    }
    
    private WhileStatement parseWhileStatement() throws ParserException {
        pop(); // pop 'while'
        var condition = parseParenthesizedExpression();
        var body = parseStatement();
        return new WhileStatement(condition, body);
    }
    
    private ForLoopStatement parseForLoopStatement() throws ParserException {
        pop(); // pop 'for'
        parseExact(TokenType.LPAREN);
        var initializer = parseStatement();
        var condition = parseExpression();
        parseExact(TokenType.SEMICOLON);
        var increment = parseStatement();
        parseExact(TokenType.RPAREN);
        var body = parseStatement();
        return new ForLoopStatement(initializer, condition, increment, body);
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
    
    private String parseIdentifier() throws ParserException {
        var token = pop();
        if (token.type() != TokenType.IDENTIFIER) {
            throw ParserException.unexpectedToken("IDENTIFIER", token.type().name(), token.location());
        }
        return token.lexeme();
    }
    
    private void parseExact(TokenType tokenType) throws ParserException {
        var token = pop();
        if (token.type() != tokenType) {
            throw ParserException.unexpectedToken(tokenType.name(), token.type().name(), token.location());
        }
    }
}
