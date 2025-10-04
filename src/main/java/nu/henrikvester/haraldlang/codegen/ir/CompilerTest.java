package nu.henrikvester.haraldlang.codegen.ir;

import nu.henrikvester.haraldlang.exceptions.HaraldLangException;
import nu.henrikvester.haraldlang.parser.Parser;

public class CompilerTest {
    public static void main(String[] args) throws HaraldLangException {
        var input = """
                fun main() {
                    declare test = 5;
                    for (declare i = 0; i < 10; let i = i + 1;) {
                        print i;
                        let test = 6;
                    }
                    print test;
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

//        var ssa = new SSA();
//        for (var block : irFunction.basicBlocks()) {
//            ssa.convertToSSA(block);
//        }
//        System.out.println("\n\n---------------------------------------------------\nSSA:\n");
//        System.out.println(irFunction);

//        var blocks = fb.getBlocks();
//
//        for (var e : blocks.entrySet()) {
//            System.out.println(e.getValue());
//            System.out.println();
//        }
    }
}
