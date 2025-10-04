package nu.henrikvester.haraldlang.codegen.ir;

public class SSA {
    public void convertToSSA(BasicBlock block) {
        for (var instruction : block.getInstructions()) {
            System.out.println(instruction);
        }
    }
}
