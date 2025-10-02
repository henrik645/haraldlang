package nu.henrikvester.haraldlang;

import nu.henrikvester.haraldlang.core.TokenType;
import nu.henrikvester.haraldlang.exceptions.TokenizerException;
import nu.henrikvester.haraldlang.tokenizer.Tokenizer;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws TokenizerException {
        var input = "fun (hello) { return 42; }";
        var tokenizer = new Tokenizer(input);
        while (true) {
            var token = tokenizer.getNextToken();
            if (token.type() == TokenType.EOF) break;
            token.pointOut(input);
        }
    }
}
