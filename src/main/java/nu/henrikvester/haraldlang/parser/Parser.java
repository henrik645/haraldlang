package nu.henrikvester.haraldlang.parser;

import nu.henrikvester.haraldlang.ast.Program;
import nu.henrikvester.haraldlang.ast.definitions.FunctionDefinition;
import nu.henrikvester.haraldlang.ast.expressions.*;
import nu.henrikvester.haraldlang.ast.statements.*;
import nu.henrikvester.haraldlang.core.Token;
import nu.henrikvester.haraldlang.core.TokenType;
import nu.henrikvester.haraldlang.exceptions.NotImplementedException;
import nu.henrikvester.haraldlang.exceptions.ParserException;
import nu.henrikvester.haraldlang.exceptions.TokenizerException;
import nu.henrikvester.haraldlang.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final static boolean DEBUG = false;
    // TODO replace with Deque?
    private final List<Token> tokens = new ArrayList<>();

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
        return tokens.removeFirst();
    }

    private Token peek() {
        assert (!tokens.isEmpty());
        return tokens.getFirst();
    }

    private void pushBack(Token token) {
        tokens.addFirst(token);
    }

    private Expression parseExpression() throws ParserException {
        var token = pop();

        Expression left;
        if (token.type() == TokenType.LPAREN) {
            left = parseExpression();
            parseExact(TokenType.RPAREN);
        } else {
            left = switch (token.type()) {
                case AMPERSAND -> new AddressOfExpression(parseVariable(), token.location());
                case ASTERISK -> throw ParserException.notImplementedYet("Dereference operator", token.location());
                case NUMBER -> new LiteralExpression(Integer.parseInt(token.lexeme()), token.location());
                case IDENTIFIER -> new Var(token.lexeme(), token.location());
                default -> throw ParserException.unexpectedToken("expression", token.type().name(), token.location());
            };
        }

        var operator = peek();
        if (BinaryOperators.tokenTypeIsOperator(operator.type())) {
            var op = pop(); // pop operator
            var right = parseExpression();
            return new BinaryExpression(left, BinaryOperators.fromTokenType(operator.type()), right, op.location());
        } else {
            return left;
        }
    }

    public Program parse() throws ParserException {
        List<FunctionDefinition> functions = new ArrayList<>();
        for (var next = peek(); next.type() != TokenType.EOF; next = peek()) {
            functions.add(parseFunction());
        }
        return new Program(functions);
    }

    private FunctionDefinition parseFunction() throws ParserException {
        var kw = parseExact(TokenType.KEYWORD_FUN);
        var name = parseVariable();
        parseExact(TokenType.LPAREN);
        var parameters = new ArrayList<Declaration>();
        while (true) {
            var next = peek();
            if (next.type() == TokenType.RPAREN) {
                break;
            }
            var paramType = parseTypeUsage();
            var variable = parseVariable();
            var declaration = new Declaration(paramType, variable.identifier(), null, variable.location()); // change here for default function parameters?
            parameters.add(declaration);
            next = peek();
            if (next.type() == TokenType.COMMA) {
                pop(); // pop ','
            } else if (next.type() != TokenType.RPAREN) {
                throw ParserException.unexpectedToken("',' or ')'", next.type().name(), next.location());
            }
        }
        parseExact(TokenType.RPAREN);

        var body = parseStatement();

        return new FunctionDefinition(name.identifier(), parameters, body, kw.location());
    }

    private Statement parseStatement() throws ParserException {
        var next = peek();
        switch (next.type()) {
            case IDENTIFIER -> {
                // either a lifted variable expression, a function call, a variable assignment, or a variable declaration
                // x
                // method()
                // x = 2
                // int x = 2;

                var ident = pop();
                var anotherIdent = peek();
                if (anotherIdent.type() == TokenType.LPAREN) { // method call
                    throw new NotImplementedException("Method calls");
                } else if (anotherIdent.type() == TokenType.EQUALS) { // variable assignment
                    parseExact(TokenType.EQUALS);
                    var expression = parseExpression();
                    parseExact(TokenType.SEMICOLON);
                    return new Assignment(new Var(ident.lexeme(), ident.location()), expression);
                } else if (anotherIdent.type() == TokenType.IDENTIFIER) { // variable declaration
                    var varName = parseVariable();
                    var typeUse = new TypeUse(ident.lexeme(), ident.location());
                    var equals = peek();
                    if (equals.type() == TokenType.EQUALS) {
                        pop(); // pop '='
                        var expression = parseExpression();
                        parseExact(TokenType.SEMICOLON);
                        return new Declaration(typeUse, varName.identifier(), expression, ident.location());
                    }
                    parseExact(TokenType.SEMICOLON);
                    return new Declaration(typeUse, varName.identifier(), null, ident.location());
                } else { // expression
                    pushBack(ident);
                    return liftExpression();
                }
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
            case NUMBER, LPAREN -> {
                return liftExpression();
            }
            case LBRACE -> {
                var opening = pop(); // pop '{'
                var statements = new ArrayList<Statement>();
                while (peek().type() != TokenType.RBRACE) {
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
        // throw warning if this is of the form (i = i + 1), i.e., lifted expressions, since the loop variable then won't change!
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

    private TypeUse parseTypeUsage() throws ParserException {
        var token = pop();
        if (token.type() != TokenType.IDENTIFIER) {
            throw ParserException.unexpectedToken("type name", token.type().name(), token.location());
        }
        return new TypeUse(token.lexeme(), token.location());
    }

    private Token parseExact(TokenType tokenType) throws ParserException {
        var token = pop();
        if (token.type() != tokenType) {
            throw ParserException.unexpectedToken(tokenType.name(), token.type().name(), token.location());
        }
        return token;
    }
}
