package nu.henrikvester.haraldlang.core;

public record SourceLocation(int line, int column) {
    @Override
    public String toString() {
        return "line " + line + ", column " + column;
    }
    
    public void pointOut(String code) {
        var line = code.split("\n")[this.line];
        var pointer = " ".repeat(this.column) + "^";
        System.out.println(line);
        System.out.println(pointer);
    }
}
