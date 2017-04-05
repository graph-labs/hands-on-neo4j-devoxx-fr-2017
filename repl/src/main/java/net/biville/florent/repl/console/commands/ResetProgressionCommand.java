package net.biville.florent.repl.console.commands;

import net.biville.florent.repl.exercises.Exercise;
import net.biville.florent.repl.exercises.TraineeSession;
import net.biville.florent.repl.logging.ConsoleLogger;
import org.jline.utils.AttributedStyle;

import static org.jline.utils.AttributedStyle.BLUE;

public class ResetProgressionCommand implements Command {

    private final ConsoleLogger logger;

    public ResetProgressionCommand(ConsoleLogger logger) {
        this.logger = logger;
    }

    @Override
    public boolean matches(String query) {
        return normalize(query).equals(PREFIX + "reset");
    }

    @Override
    public String help() {
        return String.format("%s%s - resets progression, you'll start over at the first exercise", PREFIX, "reset");
    }

    @Override
    public void accept(TraineeSession session, String ignored) {
        session.reset();
        logger.log("Progression reset! Current exercise is now:");
        session.getCurrentExercise().accept(logger);
    }
}
