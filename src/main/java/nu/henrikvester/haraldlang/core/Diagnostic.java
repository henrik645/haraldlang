package nu.henrikvester.haraldlang.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Diagnostic {
    private final DiagnosticCode code;
    private final DiagnosticLevel level;
    private final SourceLocation location;

    public static Diagnostic error(DiagnosticCode code, SourceLocation location) {
        return new Diagnostic(code, DiagnosticLevel.ERROR, location);
    }

    public static Diagnostic warning(DiagnosticCode code, SourceLocation location) {
        return new Diagnostic(code, DiagnosticLevel.WARNING, location);
    }

    public String getMessage() {
        return code.getMessage();
    }

    @Override
    public String toString() {
        return String.format("%s: %s", level, code.getMessage());
    }

    public String colorToString() {
        return level.getColor().colorize(this.toString());
    }
}

