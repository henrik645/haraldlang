package nu.henrikvester.haraldlang.misc;

import nu.henrikvester.haraldlang.core.DiagnosticCode;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;
import nu.henrikvester.haraldlang.parser.Parser;
import org.junit.jupiter.api.Test;

class LinterTest {
    @Test
    void lint_misleadingForLoopUpdateThrowsWarning() throws HaraldLangException {
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

        var res = new Parser(input).parse();
        var main = res.functions().getFirst();

        var diags = Linter.lint(main);

        diags.stream().filter(diag -> diag.getCode() == DiagnosticCode.SUSPICIOUS_FOR_LOOP_UPDATE_STATEMENT).findFirst().orElseThrow();
    }

}