package net.biville.florent.devoxxfr2017.logging;

import java.io.PrintStream;

public class SimpleLogger {

    private final PrintStream standard;
    private final PrintStream error;

    public SimpleLogger() {
        this(System.out, System.err);
    }

    public SimpleLogger(PrintStream standard, PrintStream error) {
        this.standard = standard;
        this.error = error;
    }

    public void log(String string, Object... args) {
        standard.println(format(string, args));
    }

    public void error(String string, Object... args) {
        error.println(format(string, args));
    }

    private static String format(String string, Object[] args) {
        return String.format(string, args);
    }
}
