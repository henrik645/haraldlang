package nu.henrikvester.haraldlang.misc;

public class ColorSchemes {
    public static final ColorScheme JETBRAINS = new ColorScheme() {

        @Override
        public TerminalColor variable() {
            return TerminalColor.ANSI_BLACK;
        }

        @Override
        public TerminalColor function() {
            return TerminalColor.ANSI_CYAN;
        }

        @Override
        public TerminalColor keyword() {
            return TerminalColor.ANSI_BLUE;
        }

        @Override
        public TerminalColor number() {
            return TerminalColor.ANSI_BLACK;
        }
    };

    public static final ColorScheme GITHUB = new ColorScheme() {

        @Override
        public TerminalColor variable() {
            return TerminalColor.ANSI_BLACK;
        }

        @Override
        public TerminalColor function() {
            return TerminalColor.ANSI_PURPLE;
        }

        @Override
        public TerminalColor keyword() {
            return TerminalColor.ANSI_RED;
        }

        @Override
        public TerminalColor number() {
            return TerminalColor.ANSI_BLUE;
        }
    };
}
