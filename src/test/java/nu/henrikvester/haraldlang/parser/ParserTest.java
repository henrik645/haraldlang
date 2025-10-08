package nu.henrikvester.haraldlang.parser;

import nu.henrikvester.haraldlang.exceptions.ParserException;
import nu.henrikvester.haraldlang.exceptions.TokenizerException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ParserTest {

    private void parse(String input) throws TokenizerException, ParserException {
        new Parser(input).parse();
    }
    
    @Test
    void parse_unexpectedEOFThrowsRightException() {
        // We don't want an assertion error that the tokens ran out, but a proper parse error.
        var input = "fun main(";
        assertThrows(ParserException.class, () -> parse(input));
    }

    @Test
    void parse_unexpectedEOFThrowsRightException2() {
        var input = "fun main()";
        assertThrows(ParserException.class, () -> parse(input));
    }

    @Test
    void parse_unexpectedEOFThrowsRightException3() {
        var input = "fun main() {";
        assertThrows(ParserException.class, () -> parse(input));
    }
}