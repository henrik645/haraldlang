package nu.henrikvester.haraldlang.misc;

public interface ColorScheme {
    TerminalColor variable();

    TerminalColor function();

    TerminalColor keyword();

    TerminalColor number();
}

