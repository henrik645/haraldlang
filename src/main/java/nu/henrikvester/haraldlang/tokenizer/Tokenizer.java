package nu.henrikvester.haraldlang.tokenizer;

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
    private StringBuilder currentLexeme;
    private final static Gobbler[] GOBBLERS = new Gobbler[] { new NumberGobbler(), new IdentifierAndKeywordGobbler()};

    public Tokenizer(String input) {
        this.input = input;
        this.currChar = input.charAt(currIndex);
    }

    private boolean areMoreCharacters() {
        return input.length() > currIndex;
    }

    private void advance() {
        currIndex++;
        if (!areMoreCharacters()) return;
        currChar = input.charAt(currIndex);
        advanceSourceLocation();
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
                currentLexeme = new StringBuilder();
                var startLocation = currentSourceLocation();
                while (areMoreCharacters() && gobbler.isPart(currChar)) {
                    currentLexeme.append(currChar);
                    advance();
                }
                var lexeme = currentLexeme.toString();
                return new Token(gobbler.getTokenType(lexeme), lexeme, startLocation);
            }
        }

        // Handle single-character tokens
        var tokenType = switch (currChar) {
            case '(' -> TokenType.LPAREN;
            case ')' -> TokenType.RPAREN;
            case '{' -> TokenType.LBRACE;
            case '}' -> TokenType.RBRACE;
            case ';' -> TokenType.SEMICOLON;
            default -> throw TokenizerException.unexpectedCharacter(currChar);
        };
        var location = currentSourceLocation();
        advance();
        return new Token(tokenType, String.valueOf(currChar), location);
    }
    
    private Token eofToken() {
        return new Token(TokenType.EOF, "", currentSourceLocation());
    }
    
    private SourceLocation currentSourceLocation() {
        return new SourceLocation(currRow, currCol);
    }
}

