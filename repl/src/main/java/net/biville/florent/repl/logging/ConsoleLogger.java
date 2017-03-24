package net.biville.florent.repl.logging;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.io.PrintStream;

public class ConsoleLogger {

    private final Terminal terminal;

    public ConsoleLogger(Terminal terminal) {
        this.terminal = terminal;
    }

    public void log(String string, Object... args) {
        AttributedStyle style = AttributedStyle.BOLD;
        log(string, style, args);
    }

    public void log(String string, AttributedStyle style, Object[] args) {
        log(System.out, style, String.format(string, args));
    }

    public void error(String string, Object... args) {
        log(System.out, AttributedStyle.BOLD.foreground(AttributedStyle.RED), String.format(string, args));
    }

    private void log(PrintStream out, AttributedStyle style, String log) {
        out.println(new AttributedStringBuilder().append(log, style).toAnsi(terminal));
    }
}
