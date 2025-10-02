package nu.henrikvester.haraldlang.tokenizer;

import com.sun.source.tree.ReturnTree;
import nu.henrikvester.haraldlang.core.SourceLocation;
import nu.henrikvester.haraldlang.core.Token;
import nu.henrikvester.haraldlang.core.TokenType;
import nu.henrikvester.haraldlang.exceptions.TokenizerException;

public class Tokenizer {
    private final String input;
    private char currChar;
    private int currIndex = 0;
    private int currCol = 0;
    private int currRow = 0;
    private final static Gobbler[] GOBBLERS = new Gobbler[] { new NumberGobbler(), new IdentifierAndKeywordGobbler()};

    public Tokenizer(String input) {
        this.input = input;
        this.currChar = input.charAt(currIndex);
    }

    private boolean areMoreCharacters() {
        return input.length() > currIndex + 1;
    }
    
    private void advance() {
        if (!areMoreCharacters()) return;
        currIndex++;
        advanceSourceLocation();
        currChar = input.charAt(currIndex);
    }
    
    private void advanceSourceLocation() {
        if (currChar == '\n') {
            currRow++;
            currCol = 0;
        } else {
            currCol++;
        }
    }

    private void eatWhitespace() {
        while (areMoreCharacters() && Character.isWhitespace(currChar)) {
            advance();
        }
    }

    public Token getNextToken() throws TokenizerException {
        eatWhitespace();
        if (!areMoreCharacters()) {
            return eofToken();
        }
        
        // Handle all gobblers (multicharacter tokens)
        for (Gobbler gobbler : GOBBLERS) {
            // Handle identifiers and keywords
            if (gobbler.isStart(currChar)) {
                StringBuilder currentLexeme = new StringBuilder();
                var startLocation = currentSourceLocation();
                while (areMoreCharacters() && gobbler.isPart(currChar)) {
                    currentLexeme.append(currChar);
                    advance();
                }
                var lexeme = currentLexeme.toString();
                return new Token(gobbler.getTokenType(lexeme), lexeme, startLocation);
            }
        }

        boolean hasAdvanced = false; // whether we've already advanced the current character
        String lexeme = null;
        // Handle single-character tokens
        var tokenType = switch (currChar) {
            case '(' -> TokenType.LPAREN;
            case ')' -> TokenType.RPAREN;
            case '{' -> TokenType.LBRACE;
            case '}' -> TokenType.RBRACE;
            case ';' -> TokenType.SEMICOLON;
            case ',' -> TokenType.COMMA;
            case '+' -> TokenType.PLUS;
            case '-' -> TokenType.MINUS;
            case '=' -> TokenType.EQUALS;
            case '&' -> TokenType.AMPERSAND;
            case '*' -> TokenType.ASTERISK;
            case '!' -> {
                advance();
                if (currChar == '=') {
                    yield TokenType.NOT_EQUALS;
                } else {
                    hasAdvanced = true;
                    yield TokenType.EXCLAMATION;
                }
            }
            case '>' -> {
                advance();
                if (currChar == '=') {
                    lexeme = ">=";
                    yield TokenType.GREATER_THAN_OR_EQUAL;
                } else {
                    hasAdvanced = true;
                    yield TokenType.GREATER_THAN;
                }
            }
            case '<' -> {
                advance();
                if (currChar == '=') {
                    lexeme = "<=";
                    yield TokenType.LESSER_THAN_OR_EQUAL;
                } else {
                    hasAdvanced = true;
                    yield TokenType.LESSER_THAN;
                }
            }
            default -> throw TokenizerException.unexpectedCharacter(currChar, currentSourceLocation());
        };
        var location = currentSourceLocation();
        lexeme = lexeme == null ? String.valueOf(currChar) : lexeme;
        if (!hasAdvanced) {
            advance();
        }
        return new Token(tokenType, lexeme, location);
    }
    
    private Token eofToken() {
        return new Token(TokenType.EOF, "", currentSourceLocation());
    }
    
    private SourceLocation currentSourceLocation() {
        return new SourceLocation(currRow, currCol);
    }
}

