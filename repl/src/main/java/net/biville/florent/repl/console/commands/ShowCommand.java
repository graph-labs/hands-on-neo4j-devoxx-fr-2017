package net.biville.florent.repl.console.commands;

import net.biville.florent.repl.exercises.TraineeSession;
import net.biville.florent.repl.logging.ConsoleLogger;
import org.jline.utils.AttributedStyle;

public class ShowCommand implements Command {

    private final ConsoleLogger logger;

    public ShowCommand(ConsoleLogger logger) {
        this.logger = logger;
    }

    @Override
    public boolean matches(String query) {
        return normalize(query).equals(PREFIX + "show");
    }

    @Override
    public String help() {
        return String.format("%s%s - shows current exercise instructions", PREFIX, "show");
    }

    @Override
    public void accept(TraineeSession session, String ignored) {
        logger.log(session.getCurrentExercise().getInstructions(), AttributedStyle.DEFAULT.italic());
    }

    @Override
    public String toString() {
        return ":show";
    }
}
