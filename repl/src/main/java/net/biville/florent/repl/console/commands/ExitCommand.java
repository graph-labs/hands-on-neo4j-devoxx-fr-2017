package net.biville.florent.repl.console.commands;

import net.biville.florent.repl.exercises.TraineeSession;
import net.biville.florent.repl.logging.ConsoleLogger;
import org.jline.utils.AttributedStyle;

public class ExitCommand implements Command {

    private final ConsoleLogger logger;

    public ExitCommand(ConsoleLogger logger) {
        this.logger = logger;
    }

    @Override
    public boolean matches(String query) {
        return normalize(query).equals(":exit");
    }

    @Override
    public String help() {
        return String.format("%s%s - exits REPL", PREFIX, "exit");
    }

    @Override
    public void accept(TraineeSession session, String s) {
        logger.log("Sad to see you go ;-(", AttributedStyle.DEFAULT.blink());
        System.exit(0);
    }
}
