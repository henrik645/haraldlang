package nu.henrikvester.haraldlang.codegen.ir;

import nu.henrikvester.haraldlang.exceptions.HaraldLangException;
import nu.henrikvester.haraldlang.parser.Parser;

public class CompilerTest {
    public static void main(String[] args) throws HaraldLangException {
        var input = """
                fun main(x) {
                    print x;
                    for (declare i = 10; i; let i = i - 1;) {
                        for (declare j = 10; j; let j = j - 1;) {
                            declare i = i + 2;
                            print i + j;
                        }
                    }
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
