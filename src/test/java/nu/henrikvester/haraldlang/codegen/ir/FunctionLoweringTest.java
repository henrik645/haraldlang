package nu.henrikvester.haraldlang.codegen.ir;

import nu.henrikvester.haraldlang.analysis.NameResolver;
import nu.henrikvester.haraldlang.codegen.ir.primitives.IRFunction;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;
import nu.henrikvester.haraldlang.parser.Parser;
import org.junit.jupiter.api.Test;

class FunctionLoweringTest {

    static IRFunction getFunction(String input) throws HaraldLangException {
        var ast = new Parser(input).parse();
        var first = ast.functions().getFirst();
        var bindings = new NameResolver().resolve(first);
        return new FunctionLowering().lowerFunction(first, bindings);
    }

    @Test
    void functionLowering_allBlocksAreClosed() throws HaraldLangException {
        var f = getFunction("""
                fun main() {
                    for (int i = 10; i; i = i - 1;) {
                        print i;
                    }
                }
                """);

        for (var block : f.basicBlocks()) {
            assert block.isClosed();
        }
    }
}