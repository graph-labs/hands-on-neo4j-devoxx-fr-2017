package net.biville.florent.repl.console.commands;

import net.biville.florent.repl.exercises.TraineeSession;
import net.biville.florent.repl.logging.ConsoleLogger;

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
    public void accept(TraineeSession traineeSession, String ignored) {
        traineeSession.reset();
        logger.log("Progression reset! Current exercise is now:");
        logger.log(traineeSession.getCurrentExercise().getStatement());
    }
}
