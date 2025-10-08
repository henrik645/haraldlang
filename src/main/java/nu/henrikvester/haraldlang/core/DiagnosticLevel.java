package nu.henrikvester.haraldlang.core;

import nu.henrikvester.haraldlang.misc.TerminalColor;

public enum DiagnosticLevel {
    ERROR,
    WARNING;

    TerminalColor getColor() {
        return switch (this) {
            case ERROR -> TerminalColor.ANSI_RED;
            case WARNING -> TerminalColor.ANSI_YELLOW;
        };
    }
}
