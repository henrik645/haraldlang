package nu.henrikvester.haraldlang.codegen.ir;

import java.util.List;

public record IRFunction(String name, Label entry, List<BasicBlock> basicBlocks) {
}
