package nu.henrikvester.haraldlang.tokenizer;

import nu.henrikvester.haraldlang.core.Token;
import nu.henrikvester.haraldlang.core.TokenType;
import nu.henrikvester.haraldlang.exceptions.TokenizerException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static nu.henrikvester.haraldlang.core.TokenType.*;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class TokenizerTest {
    List<Token> tokenize(String input) throws TokenizerException {
        var tokens = new ArrayList<Token>();
        var tokenizer = new Tokenizer(input);
        Token token;
        while ((token = tokenizer.getNextToken()) != null && token.type() != TokenType.EOF) {
            tokens.add(token);
        }
        return tokens;
    }

    List<TokenType> tokenTypes(List<Token> tokens) {
        return tokens.stream().map(Token::type).toList();
    }

    @Test
    void tokenize_emptyInput() throws TokenizerException {
        assertIterableEquals(tokenize(""), List.of());
    }

    @Test
    void tokenize_singleToken() throws TokenizerException {
        assertIterableEquals(List.of(LPAREN), tokenTypes(tokenize("(")));
    }

    @Test
    void tokenize_singleTokens() throws TokenizerException {
        assertIterableEquals(List.of(KEYWORD_FUN, IDENTIFIER, LPAREN, RPAREN, LBRACE, RBRACE), tokenTypes(tokenize("fun hejsan() {}")));
    }
}