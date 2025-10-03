package nu.henrikvester.haraldlang;

import nu.henrikvester.haraldlang.exceptions.HaraldLangException;
import nu.henrikvester.haraldlang.misc.PrettyPrinter;
import nu.henrikvester.haraldlang.parser.Parser;
import nu.henrikvester.haraldlang.vm.HaraldMachine;

public class App {
    public static void main(String[] args) {
        var input = """
                {
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
            var ast = new Parser(input).parse();
            var prettyPrinter = new PrettyPrinter(8);
            var pretty = ast.accept(prettyPrinter);
            System.out.println("Pretty printed AST:");
            System.out.println(pretty);
            var vm = new HaraldMachine();
            vm.run(ast);
        } catch (HaraldLangException e) {
            e.printError(input);
        }
    }
}
