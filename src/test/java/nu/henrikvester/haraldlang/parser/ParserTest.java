package nu.henrikvester.haraldlang.parser;

import nu.henrikvester.haraldlang.exceptions.HaraldLangException;
import nu.henrikvester.haraldlang.exceptions.ParserException;
import nu.henrikvester.haraldlang.exceptions.TokenizerException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ParserTest {

    private void parse(String input) throws TokenizerException, ParserException {
        new Parser(input).parse();
    }
    
    @Test
    void parse_misleadingForLoopUpdateThrowsWarning() {
        var input = """
                fun main() {
                    int test = 5;
                    for (int i = 0; i < 10; i == i + 1;) {
                        print i;
                        test = 6;
                    }
                    print test;
                }
                """;

        // TODO change to identify warnings. WHEN: we have warning
        assertThrows(HaraldLangException.class, () -> parse(input));
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