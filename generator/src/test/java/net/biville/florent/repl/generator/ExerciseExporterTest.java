package net.biville.florent.repl.generator;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import org.assertj.core.data.MapEntry;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.rule.EmbeddedDatabaseRule;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.lines;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class ExerciseExporterTest {

    private static final Kryo KRYO = new Kryo();

    @Rule
    public EmbeddedDatabaseRule graphDb = new EmbeddedDatabaseRule();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private File dump;
    private BiConsumer<File, Collection<JsonExercise>> exporter;

    @Before
    public void prepare() throws IOException {
        dump = folder.newFile("dump.cypher");
        exporter = new ExerciseExporter(graphDb);
    }

    @Test
    public void exports_single_exercise() throws IOException {
        exporter.accept(dump, singletonList(exercise("Crazy query!", "MATCH (n) RETURN COUNT(n) AS count")));

        String expectedBase64 = "AQEBAGphdmEudXRpbC5IYXNoTWHwAQEDAWNvdW70CQA=";
        assertThat(dump).hasContent(
                String.format("MERGE (e:Exercise {statement: 'Crazy query!', result: '%s'})", expectedBase64)
        );
        assertThatDeserializedResult(expectedBase64, result -> {
            assertThat(result).hasSize(1);
            Map<String, Object> row = result.iterator().next();
            assertThat(row).containsExactly(MapEntry.entry("count", 0L));
        });
    }

    @Test
    public void exports_several_exercises() {
        exporter.accept(dump, asList(
                exercise("foo", "MATCH (n:Foo) RETURN COUNT(n) AS foo"),
                exercise("bar", "MATCH (n:Bar) RETURN COUNT(n) AS bar")));

        assertThat(dump).hasContent(
                "MERGE (e:Exercise {statement: 'foo', result: 'AQEBAGphdmEudXRpbC5IYXNoTWHwAQEDAWZv7wkA'})\n" +
                "MERGE (e:Exercise {statement: 'bar', result: 'AQEBAGphdmEudXRpbC5IYXNoTWHwAQEDAWJh8gkA'})\n" +
                "MATCH (e:Exercise) WITH e ORDER BY ID(e) WITH COLLECT(e) AS exercises FOREACH (i IN RANGE(0, length(exercises)-2) | FOREACH (first IN [exercises[i]] | FOREACH (second IN [exercises[i+1]] | MERGE (first)-[:NEXT]->(second))))");
    }

    @Test
    public void generates_executable_dump() throws IOException {
        exporter.accept(dump, asList(
                exercise("foo", "MATCH (n:Foo) RETURN COUNT(n) AS foo_count"),
                exercise("bar", "MATCH (n:Bar) RETURN COUNT(n) AS bar_count"),
                exercise("baz", "MATCH (n:Baz) RETURN COUNT(n) AS baz_count")));

        lines(dump.toPath(), UTF_8).forEachOrdered(line -> {
            try (Transaction transaction = graphDb.beginTx()) {
                graphDb.execute(line);
                transaction.success();
            }
        });

        try (Transaction ignored = graphDb.beginTx();
             Result result = graphDb.execute(
                    "MATCH p=(e1:Exercise)-[:NEXT*]->(e2:Exercise) WITH p ORDER BY LENGTH(p) DESC LIMIT 1 " +
                          "RETURN EXTRACT(exercise IN NODES(p) | exercise.statement) AS statements")) {

            assertThat(result.hasNext()).isTrue();
            @SuppressWarnings("unchecked")
            Collection<String> statements = (Collection<String>) result.next().get("statements");
            assertThat(statements).containsExactly("foo", "bar", "baz");
            assertThat(result.hasNext()).isFalse();
        }
    }

    private static void assertThatDeserializedResult(String expectedBase64, Consumer<List<Map<String, Object>>> asserts) throws IOException {
        byte[] bytes = Base64.getDecoder().decode(expectedBase64);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> result = KRYO.readObject(new Input(inputStream), ArrayList.class);
            asserts.accept(result);
        }
    }

    private JsonExercise exercise(String statement, String query) {
        JsonExercise exercise = new JsonExercise();
        exercise.setStatement(statement);
        exercise.setQueryToExecute(query);
        return exercise;
    }
}
