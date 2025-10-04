package nu.henrikvester.haraldlang.codegen.ir;

import nu.henrikvester.haraldlang.exceptions.HaraldLangException;
import nu.henrikvester.haraldlang.parser.Parser;

public class CompilerTest {
    public static void main(String[] args) throws HaraldLangException {
        var input = """
                fun main(x) {
                    declare y;
                    if (x - 10) {
                        let y = 42;
                    } else {
                        let y = 137;
                    }
                    print y;
                }
                """;

        var defs = new Parser(input).parse();

        var firstFunctionDefinition = defs.functions().getFirst();
        
        var nr = new NameResolver();
        var bindings = nr.resolve(firstFunctionDefinition);
        System.out.println(bindings);

        var functionCompiler = new FunctionLowering();
        var irFunction = functionCompiler.lowerFunction(firstFunctionDefinition, bindings);

        System.out.println(irFunction);

//        var blocks = fb.getBlocks();
//
//        for (var e : blocks.entrySet()) {
//            System.out.println(e.getValue());
//            System.out.println();
//        }
    }
}
