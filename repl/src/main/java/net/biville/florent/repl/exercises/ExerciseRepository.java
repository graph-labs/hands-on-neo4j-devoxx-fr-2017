package net.biville.florent.repl.exercises;

import net.biville.florent.repl.graph.cypher.CypherQueryExecutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class ExerciseRepository {

    private final CypherQueryExecutor executor;
    private final Base64.Decoder decoder;

    public ExerciseRepository(CypherQueryExecutor executor) {
        this.executor = executor;
        this.decoder = Base64.getDecoder();
    }

    public void importExercises(InputStream dataset) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(dataset, StandardCharsets.UTF_8))) {
            reader.lines().forEachOrdered(executor::execute);
            initializeSession();
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Exercise findCurrentExercise() {
        String query =
                "MATCH (:TraineeSession)-[:CURRENTLY_AT]->(e:Exercise) " +
                "RETURN e.statement AS statement, e.result AS result";

        List<Map<String, Object>> rows = executor.execute(
                query);

        int count = rows.size();
        if (count != 1) {
            throw new RuntimeException(String.format("Expected 1 current exercise, got %d.", count));
        }

        Map<String, Object> row = rows.iterator().next();
        return new Exercise(
            row.get("statement").toString(),
            decoder.decode(row.get("result").toString())
        );
    }

    public boolean moveToNext() {
        String query =
                "MATCH (t:TraineeSession)-[c:CURRENTLY_AT]->(:Exercise)-[:NEXT]->(e:Exercise) " +
                "DELETE c " +
                "CREATE (t)-[:CURRENTLY_AT]->(e) " +
                "RETURN true";

        return !executor.execute(
                query).isEmpty();
    }

    private void initializeSession() {
        executor.executeAll("MERGE (s:TraineeSession) ON CREATE SET s.temp = true",
                "MATCH (e:Exercise) WITH e ORDER BY ID(e) LIMIT 1 " +
                "MATCH (s:TraineeSession {temp:true}) " +
                "CREATE (s)-[:CURRENTLY_AT]->(e) " +
                "WITH s " +
                "REMOVE s.temp");
    }

    public void resetProgression() {
        executor.execute("MATCH (s:TraineeSession)-[r:CURRENTLY_AT]->(current:Exercise), (first:Exercise)" +
                "WHERE NOT((:Exercise)-[:NEXT]->(first)) AND current <> first " +
                "DELETE r " +
                "CREATE (s)-[:CURRENTLY_AT]->(first)");
    }
}
