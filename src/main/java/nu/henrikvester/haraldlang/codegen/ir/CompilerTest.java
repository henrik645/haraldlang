package nu.henrikvester.haraldlang.codegen.ir;

import nu.henrikvester.haraldlang.exceptions.HaraldLangException;
import nu.henrikvester.haraldlang.parser.Parser;

import java.util.List;

public class CompilerTest {
    public static void main(String[] args) throws HaraldLangException {
        var input = """
                {
                    declare a = 5;
                    declare x = 10;
                    {
                        declare x = 5;
                        print x;
                    }
                    declare y = a + x;
                }
                """;

        var ast = new Parser(input).parse();

        var nr = new NameResolver();
        ast.accept(nr);

        var fb = new FunctionBuilder("main");

        var locals = List.of(new VarSlot(0, "a"), new VarSlot(1, "x"), new VarSlot(2, "x"), new VarSlot(3, "y"));
        var codegen = new CodeGenerator(new TranslatorImpl(fb), nr, locals);

        ast.accept(codegen);

        var blocks = fb.getBlocks();

        for (var e : blocks.entrySet()) {
            System.out.println("Key: " + e.getKey());
            System.out.println("Block: " + e.getValue());
            System.out.println();
        }
    }
}
