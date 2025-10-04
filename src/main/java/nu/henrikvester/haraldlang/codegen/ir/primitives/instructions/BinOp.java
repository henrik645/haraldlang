package nu.henrikvester.haraldlang.codegen.ir.primitives.instructions;

public enum BinOp {
    ADD, SUB, SHL, AND, OR, XOR,
    EQ, NE, LT, LTE, GT, GTE;

    public static BinOp fromString(String s) {
        return switch (s) {
            case "+" -> ADD;
            case "-" -> SUB;
            case "<<" -> SHL;
            case "&" -> AND;
            case "|" -> OR;
            case "^" -> XOR;
            case "=" -> EQ;
            case "!=" -> NE;
            case "<" -> LT;
            case "<=" -> LTE;
            case ">" -> GT;
            case ">=" -> GTE;
            default -> throw new IllegalArgumentException("Unknown binary operator: " + s);
        };
    }
}
