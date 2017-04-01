package net.biville.florent.repl.generator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import org.neo4j.driver.v1.AuthToken;
import org.neo4j.driver.v1.AuthTokens;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class Main {

    @Parameter(names = {"-f", "--json-file"}, description = "Exercise JSON file", required = true)
    private File exerciseDefinition;

    @Parameter(names = {"-o", "--output-file"}, description = "Cypher output file", help = true)
    private File outputFile;

    @Parameter(names = {"-b", "--bolt-uri"}, description = "Neo4j Bolt URI")
    private String boltUri = "bolt://localhost:7687";

    @Parameter(names = {"-u", "--username"}, description = "Neo4j User name", required = true)
    private String username = "neo4j";

    @Parameter(names = {"-p", "--password"}, description = "Neo4j password", required = true, password = true)
    private String password;

    @Parameter(names = {"-h", "--help"}, description = "Help", help = true)
    private boolean help;

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        JCommander cli = parseCommandLineArguments(args, main);
        if (main.help) {
            cli.usage();
            return;
        }

        System.out.println("A word of warning:");
        System.out.println("Please make sure the configured database contains only the required data for the exercises.");
        System.out.println("In the end, the database content relied upon by the generator must be exactly the same as the one in user databases.");

        Collection<JsonExercise> exercises = new ExerciseParser().apply(main.exerciseDefinition);
        new ExerciseExporter(main.boltUri, authTokens(main.username, main.password)).accept(main.outputFile, exercises);
    }

    private static JCommander parseCommandLineArguments(String[] args, Main main) {
        JCommander command = getJCommander(args, main);
        command.setProgramName("Hands on Neo4j - Exercise Generator");
        return command;
    }

    private static AuthToken authTokens(String username, String password) {
        if (username.isEmpty()) {
            return AuthTokens.none();
        }
        return AuthTokens.basic(username, password);
    }

    private static JCommander getJCommander(String[] args, Main main) {
        try {
            return new JCommander(main, args);
        } catch (ParameterException pe) {
            main.help = true;
            return new JCommander(main);
        }
    }
}
