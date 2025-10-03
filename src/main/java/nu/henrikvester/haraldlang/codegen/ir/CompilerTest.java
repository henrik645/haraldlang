package nu.henrikvester.haraldlang.codegen.ir;

import nu.henrikvester.haraldlang.exceptions.HaraldLangException;
import nu.henrikvester.haraldlang.parser.Parser;

import java.util.List;

public class CompilerTest {
    public static void main(String[] args) throws HaraldLangException {
        var input = """
                {
                    for (declare x = 10; x; let x = x - 1;) {
                        declare y = x + 2;
                        print y;
                    }
                }
                """;

        var ast = new Parser(input).parse();

        var nr = new NameResolver();
        ast.accept(nr);

        var fb = new FunctionBuilder("main");

        var locals = List.of(new VarSlot(0, "x"), new VarSlot(1, "y"));
        var codegen = new CodeGenerator(new TranslatorImpl(fb), nr, locals);

        ast.accept(codegen);

        var blocks = fb.getBlocks();

        for (var e : blocks.entrySet()) {
            System.out.println(e.getValue());
            System.out.println();
        }
    }
}
