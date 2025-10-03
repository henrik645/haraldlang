package nu.henrikvester.haraldlang.core;

public record SourceLocation(int line, int column) {
    @Override
    public String toString() {
        return "line " + line + ", column " + column;
    }
    
    public void pointOut(String code, int context) {
        final String ANSI_RED = "\u001B[31m"; // TODO place in terminal utility class
        final String ANSI_RESET = "\u001B[0m";

        var lines = code.split("\n");
        var startRange = clamp(0, line - context, lines.length - 1);
        var endRange = clamp(0, line + context, lines.length - 1);
        var indentation = 4;
        var pointer = " ".repeat(this.column + indentation) + "^";
        for (int i = startRange; i <= endRange; i++) {
            System.err.printf("%s%3d %s%n", i == this.line ? ANSI_RED : "", i, lines[i]);
            if (i == this.line) { // point to the error line
                // TODO print in colors
                System.err.println(pointer + ANSI_RESET);
            }
        }
    }
    
    public void pointOut(String code) {
        pointOut(code, 2);
    }
    
    private int clamp(int min, int value, int max) {
        return Math.max(min, Math.min(value, max));
    }
}
