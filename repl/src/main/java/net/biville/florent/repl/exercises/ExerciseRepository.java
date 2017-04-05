package net.biville.florent.repl.exercises;

import net.biville.florent.repl.graph.cypher.CypherQueryExecutor;
import org.neo4j.driver.v1.Record;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ExerciseRepository {

    private final CypherQueryExecutor executor;
    private final Base64.Decoder decoder;

    public ExerciseRepository(CypherQueryExecutor executor) {
        this.executor = executor;
        this.decoder = Base64.getDecoder();
    }

    public void importExercises(InputStream dataset) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(dataset, UTF_8))) {
            executor.commit(tx -> {
                reader.lines().forEachOrdered(line -> tx.run(line.replaceAll("\\\\n", "\n")));
            });
            executor.commit(tx -> {
                tx.run("MERGE (s:TraineeSession) ON CREATE SET s.temp = true");
                tx.run("MATCH (e:Exercise) WITH e ORDER BY ID(e) LIMIT 1 " +
                        "MATCH (s:TraineeSession {temp:true}) " +
                        "CREATE (s)-[:CURRENTLY_AT]->(e) " +
                        "WITH s " +
                        "REMOVE s.temp");
            });
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Exercise findCurrentExercise() {
        List<Map<String, Object>> rows = executor.rollback(tx -> {
            return tx.run("MATCH (:TraineeSession)-[:CURRENTLY_AT]->(e:Exercise)<-[p:NEXT*0..]-(:Exercise), (all:Exercise) " +
                    "WITH e,p, COUNT(all) AS total " +
                    "ORDER BY LENGTH(p) DESC LIMIT 1 " +
                    "RETURN e.instructions AS instructions," +
                    "       e.result AS result," +
                    "       e.validationQuery AS validationQuery," +
                    "       1+LENGTH(p) AS position," +
                    "       total"
            ).list(Record::asMap);
        });

        int count = rows.size();
        if (count != 1) {
            throw new RuntimeException(String.format("Expected 1 current exercise, got %d.", count));
        }

        Map<String, Object> row = rows.iterator().next();
        Object validationQuery = row.get("validationQuery");
        return new Exercise(
                row.get("instructions").toString(),
                validationQuery == null ? null : validationQuery.toString(),
                decoder.decode(row.get("result").toString()),
                parseInt(row.get("position").toString(), 10),
                parseInt(row.get("total").toString(), 10));
    }

    public boolean moveToNextExercise() {
        String query =
                "MATCH (t:TraineeSession)-[c:CURRENTLY_AT]->(:Exercise)-[:NEXT]->(e:Exercise) " +
                        "DELETE c " +
                        "CREATE (t)-[:CURRENTLY_AT]->(e) " +
                        "RETURN true";

        return !executor.commit(tx -> {
            return tx.run(query).list(Record::asMap);
        }).isEmpty();
    }

    public void resetProgression() {
        executor.commit(tx -> {
            tx.run("MATCH (s:TraineeSession)-[r:CURRENTLY_AT]->(current:Exercise), (first:Exercise)" +
                    "WHERE NOT((:Exercise)-[:NEXT]->(first)) AND current <> first " +
                    "DELETE r " +
                    "CREATE (s)-[:CURRENTLY_AT]->(first)");
        });
    }

}
