package net.biville.florent.repl.exercises;

import net.biville.florent.repl.graph.ReplConfiguration;
import net.biville.florent.repl.graph.cypher.CypherQueryExecutor;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.harness.junit.Neo4jRule;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

public class ExerciseRepositoryTest {

    @Rule
    public Neo4jRule graphDatabase = new Neo4jRule();

    private ExerciseRepository repository;

    @Before
    public void prepare() {
        ReplConfiguration configuration = new ReplConfiguration(graphDatabase.boltURI());
        repository = new ExerciseRepository(new CypherQueryExecutor(configuration));
    }

    @Test
    public void returns_current_exercise() {
        String solution = "whatever works";
        write(format("CREATE (:TraineeSession)-[:CURRENTLY_AT]->(:Exercise {instructions:'Do something', result:'%s'})",
                        encode(solution)));

        Exercise exercise = repository.findCurrentExercise();

        assertThat(exercise.getInstructions()).isEqualTo("Do something");
        assertThat(exercise.getPosition()).isEqualTo(1);
        assertThat(exercise.getTotal()).isEqualTo(1);
        assertThat(exercise.getSerializedResult()).isEqualTo(solution.getBytes(StandardCharsets.US_ASCII));
    }

    @Test
    public void moves_to_next() {
        String nextSolution = "whatever works again";
        write(format("CREATE (:TraineeSession)-[:CURRENTLY_AT]->(:Exercise {instructions:'Do something', result:'%s'})-[:NEXT]->(:Exercise {instructions:'Next one!', result:'%s'})",
                        encode("whatever works"),
                        encode(nextSolution)));

        repository.moveToNextExercise();

        List<Map<String, Object>> rows = read("MATCH (e:Exercise)<-[:CURRENTLY_AT]-(:TraineeSession) " +
                "RETURN e.instructions AS instructions," +
                "       e.result AS result");

        assertThat(rows)
                .containsExactly(map(
                        tuple("instructions", "Next one!"),
                        tuple("result", encode((nextSolution)))
                ));
    }

    @Test
    public void resets_progression() {
        String resetExerciseSolution = "previous soon-to-be current solution";
        String cql = "CREATE (:TraineeSession)-[:CURRENTLY_AT]->(current:Exercise {instructions:'Next one!', result:'%s'})," +
                "(:Exercise {instructions:'Do something', result:'%s'})-[:NEXT]->(current)";
        write(format(cql, encode("ahah"), encode(resetExerciseSolution)));

        repository.resetProgression();

        List<Map<String, Object>> rows = read("MATCH (e:Exercise)<-[:CURRENTLY_AT]-(:TraineeSession) " +
                "RETURN e.instructions AS instructions," +
                "       e.result AS result");

        assertThat(rows)
                .hasSize(1)
                .containsExactly(map(
                        tuple("instructions", "Do something"),
                        tuple("result", encode(resetExerciseSolution))
                ));
    }

    private List<Map<String, Object>> read(String cql) {
        GraphDatabaseService gds = graphDatabase.getGraphDatabaseService();
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Transaction transaction = gds.beginTx();
             Result result = gds.execute(cql)) {

            while (result.hasNext()) {
                rows.add(result.next());
            }
            transaction.success();
        }
        return rows;
    }

    private void write(String cql) {
        GraphDatabaseService graphDatabaseService = graphDatabase.getGraphDatabaseService();
        try (Transaction tx = graphDatabaseService.beginTx()) {
            graphDatabaseService.execute(cql);
            tx.success();
        }
    }

    private Map<String, Object> map(Tuple... tuples) {
        HashMap<String, Object> map = new HashMap<>();
        for (Tuple tuple : tuples) {
            Object[] tupleArray = tuple.toArray();
            map.put((String)tupleArray[0], tupleArray[1]);
        }
        return map;
    }

    private static String encode(String solution) {
        return Base64.getEncoder().encodeToString(solution.getBytes(StandardCharsets.US_ASCII));
    }
}
