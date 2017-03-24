package net.biville.florent.repl.console.commands;

import org.reflections.Reflections;

import java.util.function.Predicate;

public class CommandScanner {

    private final Reflections reflections;

    public CommandScanner(String packageToScan) {
        reflections = new Reflections(packageToScan);
    }

    public Command[] scan() {
        Predicate<Class<?>> notDefaultCommands =
                Predicate.<Class<?>>isEqual(CypherSessionFallbackCommand.class).negate()
                        .and(Predicate.<Class<?>>isEqual(CommandRegistry.class).negate())
                        .and(Predicate.<Class<?>>isEqual(ShowCommand.class).negate())
                        .and(Predicate.<Class<?>>isEqual(ExitCommand.class).negate())
                        .and(Predicate.<Class<?>>isEqual(ResetProgressionCommand.class).negate())
                ;

        return reflections.getSubTypesOf(Command.class)
                .stream()
                .filter(notDefaultCommands)
                .map(this::instantiate)
                .toArray(Command[]::new);
    }

    private <T> T instantiate(Class<? extends T> type) {
        try {
            return type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Could not instantiate command", e);
        }
    }

}
