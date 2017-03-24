package net.biville.florent.repl.generator;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class ExerciseExporter implements BiConsumer<File, Collection<JsonExercise>> {

    private final GraphDatabaseService graphDb;
    private final Kryo kryo;
    private final Base64.Encoder encoder;

    public ExerciseExporter(GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
        kryo = new Kryo();
        encoder = Base64.getEncoder();
    }

    @Override
    public void accept(File file, Collection<JsonExercise> jsonExercises) {
        try {
            Collection<String> cypherQueries = cypherQueries(graphDb, jsonExercises);
            Files.write(file.toPath(), cypherQueries);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private Collection<String> cypherQueries(GraphDatabaseService graphDb, Collection<JsonExercise> jsonExercises) {
        Collection<String> cypherQueries = new ArrayList<>();
        jsonExercises.forEach(exercise -> {
            try (Transaction transaction = graphDb.beginTx()) {
                byte[] expectedResult = serialize(graphDb.execute(exercise.getQueryToExecute()));
                cypherQueries.add(insert(exercise.getStatement(), expectedResult));
                transaction.success();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });

        if (jsonExercises.size() > 1) {
            cypherQueries.add(linkQuery());
        }
        return cypherQueries;
    }

    private String insert(String statement, byte[] serializedResult) {
        return String.format("MERGE (e:Exercise {statement: '%s', result: '%s'})", statement, encoder.encodeToString(serializedResult));
    }


    private String linkQuery() {
        return "MATCH (e:Exercise) " +
                "WITH e ORDER BY ID(e) " +
                "WITH COLLECT(e) AS exercises " +
                "FOREACH (i IN RANGE(0, length(exercises)-2) | " +
                "FOREACH (first IN [exercises[i]] | " +
                "FOREACH (second IN [exercises[i+1]] | MERGE (first)-[:NEXT]->(second))))";
    }

    private byte[] serialize(Result result) throws IOException {
        List<Map<String, Object>> rows = ResultConverter.INSTANCE.apply(result);
        try (Output output = new Output(new ByteArrayOutputStream())) {
            kryo.writeObject(output, rows);
            return output.toBytes();
        }
    }
}
