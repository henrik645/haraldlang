package nu.henrikvester.haraldlang;

import nu.henrikvester.haraldlang.exceptions.HaraldLangException;
import nu.henrikvester.haraldlang.parser.Parser;
import nu.henrikvester.haraldlang.vm.HaraldMachine;

public class App {
    public static void main(String[] args) {
        var input = """
        {
            for (let x = 0; x < 10; let x = x + 1;) {
                print test;
                print &x;
                # print *x;
                print x;
            }
        };
        """;

        try {
            var ast = new Parser(input).parseStatement();
            var vm = new HaraldMachine();
            vm.run(ast);
        } catch (HaraldLangException e) {
            e.printError(input);
        }
    }
}
