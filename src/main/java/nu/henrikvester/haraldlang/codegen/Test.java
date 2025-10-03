package nu.henrikvester.haraldlang.codegen;

public class Test {
    public static void main(String[] args) {
        var tr = new Translator();

//        // translating x = 5 + 3;
//        var c1 = tr.emitConst(5);
//        var c2 = tr.emitConst(3);
//        var sum = tr.emitAdd(c1, c2);
//        tr.setVariable("x", sum);

        tr.printDebugInfo();
    }
}
