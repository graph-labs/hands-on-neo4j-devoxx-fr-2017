package net.biville.florent.repl.generator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

public class Main {

    @Parameter(names = {"-f", "--json-file"}, description = "Exercise JSON file", required = true)
    File exerciseDefinition;
    @Parameter(names = {"-o", "--output-file"}, description = "Cypher output file", help = true)
    File outputFile;
    @Parameter(names = {"-i", "--import-file"}, description = "Input dataset")
    File dataset;
    @Parameter(names = {"-h", "--help"}, description = "Help", help = true)
    boolean help;

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        JCommander cli = parseCommandLineArguments(args, main);
        if (main.help) {
            cli.usage();
            return;
        }

        GraphDatabaseSupplier graphDatabaseSupplier = new GraphDatabaseSupplier(
                Files.createTempDirectory("neo4j").toFile(),
                new GraphDatabaseBatchPopulator(main.dataset)
        );

        Collection<JsonExercise> exercises = new ExerciseParser().apply(main.exerciseDefinition);

        new ExerciseExporter(graphDatabaseSupplier.get())
                .accept(main.outputFile, exercises);
    }

    private static JCommander parseCommandLineArguments(String[] args, Main main) {
        JCommander command = getJCommander(args, main);
        command.setProgramName("Hands on Neo4j - Exercise Generator");
        return command;
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
