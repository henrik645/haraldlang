package nu.henrikvester.haraldlang;

import nu.henrikvester.haraldlang.exceptions.HaraldLangException;
import nu.henrikvester.haraldlang.parser.Parser;
import nu.henrikvester.haraldlang.vm.HaraldMachine;

public class App {
    public static void main(String[] args) {
        var input = """
        {
            let x = 0;
            while (x <= 10) {
                print x;
                let x = x + 1;
            }
        };
        """;

        try {
            var ast = new Parser(input).parseStatement();
            System.out.println("AST: " + ast);
            var vm = new HaraldMachine();
            vm.run(ast);
        } catch (HaraldLangException e) {
            e.printError(input);
        }
    }
}
