package nu.henrikvester.haraldlang;

import nu.henrikvester.haraldlang.exceptions.HaraldLangException;
import nu.henrikvester.haraldlang.misc.ColorPrettyPrinter;
import nu.henrikvester.haraldlang.misc.ColorSchemes;
import nu.henrikvester.haraldlang.parser.Parser;
import nu.henrikvester.haraldlang.vm.HaraldMachine;

public class App {
    public static void main(String[] args) {
        var input = """
                fun main() {
                    declare i;
                    declare x = 100;
                    declare y = x + 5;
                    for (let i = 0; i < 10; let i = i + 1;) {
                        print i;
                    }
                    if (i = 10) {
                        for (let x = 0; x < 10; let x = x + 1;) {
                            print x;
                        }
                        print y;
                    } else {
                        print 1;
                    }
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
            var vm = new HaraldMachine();
            vm.run(main.body());
        } catch (HaraldLangException e) {
            e.printError(input);
        }
    }
}
