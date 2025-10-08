package nu.henrikvester.haraldlang;

import nu.henrikvester.haraldlang.analysis.NameResolver;
import nu.henrikvester.haraldlang.analysis.TypeChecker;
import nu.henrikvester.haraldlang.exceptions.HaraldLangException;
import nu.henrikvester.haraldlang.misc.ColorPrettyPrinter;
import nu.henrikvester.haraldlang.misc.ColorSchemes;
import nu.henrikvester.haraldlang.parser.Parser;
import nu.henrikvester.haraldlang.vm.HaraldMachine;

public class App {
    public static void main(String[] args) {
        var input = """
                fun main() {
                    int x = (5 + 2) - 3;
                    boolean y = (x > 15);
                    print y;
                }
                """;

        try {
            var program = new Parser(input).parse();
            for (var def : program.functions()) {
                System.out.println("Parsed function: " + def.name() + " with parameters " + def.parameters());
                var prettyPrinter = new ColorPrettyPrinter(4, ColorSchemes.JETBRAINS);
                var pretty = def.accept(prettyPrinter);
                System.out.println("Pretty printed AST:");
                System.out.println(pretty);
            }
            var main = program.functions().stream().filter(f -> f.name().equals("main")).findFirst().orElseThrow(HaraldLangException::noMainFunction);

            var nameResolver = new NameResolver();
            var bindings = nameResolver.resolve(main);
            var tc = new TypeChecker(bindings);
            tc.typeCheck(main);
            
            var vm = new HaraldMachine();
            vm.run(main.body());
        } catch (HaraldLangException e) {
            e.printError(input);
        }
    }
}
