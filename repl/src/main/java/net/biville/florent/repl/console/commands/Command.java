package net.biville.florent.repl.console.commands;

import net.biville.florent.repl.exercises.TraineeSession;

import java.util.Locale;
import java.util.function.BiConsumer;

public interface Command extends BiConsumer<TraineeSession, String> {

    String PREFIX = ":";

    boolean matches(String query);

    String help();

    default String normalize(String input) {
        return input.trim().toLowerCase(Locale.ENGLISH);
    }
}
