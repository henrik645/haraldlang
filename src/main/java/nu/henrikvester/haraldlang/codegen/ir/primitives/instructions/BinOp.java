package nu.henrikvester.haraldlang.codegen.ir.primitives.instructions;

public enum BinOp {
    ADD, SUB, SHL, AND, OR, XOR;
    // TODO what to do with < > etc?

    public static BinOp fromString(String s) {
        return switch (s) {
            case "+" -> ADD;
            case "-" -> SUB;
            case "<<" -> SHL;
            case "&" -> AND;
            case "|" -> OR;
            case "^" -> XOR;
            default -> throw new IllegalArgumentException("Unknown binary operator: " + s);
        };
    }
}
