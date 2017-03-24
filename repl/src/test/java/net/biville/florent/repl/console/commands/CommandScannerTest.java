package net.biville.florent.repl.console.commands;

import net.biville.florent.repl.exercises.TraineeSession;
import org.junit.Test;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandScannerTest {

    @Test
    public void returns_nothings_if_no_custom_commands_in_package() {
        Command[] commands = new CommandScanner("com.acme").scan();

        assertThat(commands).isEmpty();
    }

    @Test
    public void returns_custom_commands_found_in_classpath() throws Exception {

        Command[] commands = new CommandScanner(this.getClass().getPackage().getName()).scan();

        assertThat(commands).containsExactly(new MyCustomCommand());
    }

    static class MyCustomCommand implements Command {

        private final String name;

        public MyCustomCommand() {
            this.name = "hello";
        }

        @Override
        public boolean matches(String query) {
            return query.contains(Command.PREFIX + name);
        }

        @Override
        public String help() {
            return "welp";
        }

        @Override
        public void accept(TraineeSession session, String statement) {}

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final MyCustomCommand other = (MyCustomCommand) obj;
            return Objects.equals(this.name, other.name);
        }

        @Override
        public String toString() {
            return Command.PREFIX + name;
        }
    }
}
