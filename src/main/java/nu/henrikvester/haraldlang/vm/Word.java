package nu.henrikvester.haraldlang.vm;

public record Word(int value) {
    public static Word ofBoolean(boolean b) {
        return new Word(b ? 1 : 0);
    }

    public boolean isTruthy() {
        return value != 0;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
