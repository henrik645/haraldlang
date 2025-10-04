package nu.henrikvester.haraldlang.parser;

import nu.henrikvester.haraldlang.exceptions.HaraldLangException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ParserTest {
    @Test
    void parse_misleadingForLoopUpdateThrowsWarning() {
        var input = """
                fun main() {
                    declare test = 5;
                    for (declare i = 0; i < 10; i = i + 1;) {
                        print i;
                        let test = 6;
                    }
                    print test;
                }
                """;

        // TODO change to identify warnings. WHEN: we have warning
        assertThrows(HaraldLangException.class, () -> new Parser(input).parse());
    }
}