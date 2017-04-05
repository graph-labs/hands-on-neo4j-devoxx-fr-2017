package net.biville.florent.repl.console;

import net.biville.florent.repl.console.commands.Command;
import net.biville.florent.repl.console.commands.CommandRegistry;
import net.biville.florent.repl.console.commands.CypherSessionFallbackCommand;
import net.biville.florent.repl.exercises.TraineeSession;
import net.biville.florent.repl.graph.cypher.CypherQueryExecutor;
import net.biville.florent.repl.graph.cypher.CypherStatementValidator;
import net.biville.florent.repl.logging.ConsoleLogger;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.jline.utils.AttributedStyle;

import static org.jline.utils.AttributedStyle.GREEN;
import static org.jline.utils.AttributedStyle.YELLOW;

public class Console {

    private final ConsoleLogger logger;
    private final CommandRegistry commandRegistry;
    private final LineReader lineReader;
    private final TraineeSession session;
    private final Command defaultCypherCommand;

    public Console(ConsoleLogger logger,
                   LineReader lineReader,
                   CommandRegistry commandRegistry,
                   TraineeSession session,
                   CypherQueryExecutor cypherQueryExecutor,
                   CypherStatementValidator cypherStatementValidator) {

        this.logger = logger;
        this.lineReader = lineReader;
        this.commandRegistry = commandRegistry;
        this.session = session;
        this.defaultCypherCommand = new CypherSessionFallbackCommand(
                logger,
                cypherQueryExecutor,
                cypherStatementValidator
        );
        
    }

    public void start() {
        logger.log("\n" +
                "     \n" +
                "          ╓╖╓\n" +
                "        ╓▓▓▓▓▓M▌ ╟╜▀▓▌╗╥                                                        █\n" +
                "         ▓▓▓▓▓¼▓▓▓╗╬╢╬▓▓▓▌╗,        ,,         ,;          ,;;\n" +
                "       ╓▓▓╩╬▌╬▓▓▓▓╟▓▓▓▓▓▓▓▓▓▌   █▄▀╙└╙▀█▄   ▄▀▀└└\"▀█    ▄█▀┘└└▀▀▄      █▀       █\n" +
                "       ▓▓▌w╬▓▓▓▓▓╡▓▓▓▓▓▓▓▓▓▓▓M  █▌      █  █▀       █  █▀       \"█    █▀        █\n" +
                "      ▐▓▓▓,╟▓▓▓▓▓▓╟▓▓▓▓▓▓▓▓▓▓   █=      █  █▀▀▀▀▀▀▀▀▀  █         █⌐  █▀    ╓    █\n" +
                "      ▐▓▓▓▄,▓▓▓▓▓▓▓╬╬▓▓▓▓▓▓▌⌐   █=      █  ╙█          ╙█       ▄▀  █▀     █=   █\n" +
                "       ╟▓▓▓Ü ╟▓▌╬▓▓▓▓╬▌╝╬╬▓▌    ▀⌐      █   `▀Wæ▄æA▀     ▀▀æ▄æΦ▀└  ╝█≡≡≡≡≡≡█=   █\n" +
                "        ╚▓▓▌▌╬▓▓▓▓▌▀▓╜½╖▓▓M                                                █=   █\n" +
                "         `╚▓▓▓▓▓▓▓▓▓╟@╬▓▀                                                  ▀⌐╘≡▀└\n" +
                "           ▓▓▓▓▓▓▓▓▓╜╙`\n" +
                "            ╚▓▓▓▓▓▀\n" +
                "\n", AttributedStyle.BOLD.foreground(GREEN));

        AttributedStyle warningStyle = AttributedStyle.BOLD.blink().foreground(YELLOW);
        logger.log("");
        logger.log("Initializing session now...", AttributedStyle.DEFAULT.italic());
        session.init(this.getClass().getResourceAsStream("/exercises/dump.cypher"));
        logger.log("... done!", AttributedStyle.DEFAULT.italic());
        logger.log("");
        logger.log("Welcome to Devoxx France 2017 Hands on Neo4j!");
        logger.log("Available commands can be displayed with ':commands'");
        logger.log("");
        logger.log("Please make sure your Cypher statements end with a semicolon.", warningStyle);
        logger.log("Every exercise is independent, no changes are persisted against your database.", warningStyle);
        logger.log("Make sure to undo the insertions in the browser before using this REPL!", warningStyle);
        logger.log("");
        logger.log("Ask for help when needed and have fun!", AttributedStyle.BOLD.foreground(AttributedStyle.CYAN));
        while (true) {
            try {
                String statement = lineReader.readLine("(:Devoxx)-[:`<3`]-(:Cypher)> ");
                commandRegistry
                        .findFirst(statement)
                        .orElse(defaultCypherCommand)
                        .accept(session, statement);
            } catch (UserInterruptException ignored) {
            } catch (EndOfFileException e) {
                logger.log("Goodbye!");
                return;
            }
        }
    }
}
