package net.biville.florent.repl.generator;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import org.assertj.core.data.MapEntry;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.harness.junit.Neo4jRule;

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

    @Rule public Neo4jRule graphDb = new Neo4jRule();
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    private File dump;
    private BiConsumer<File, Collection<JsonExercise>> exporter;

    @Before
    public void prepare() throws IOException {
        dump = folder.newFile("dump.cypher");
        exporter = new ExerciseExporter(graphDb.boltURI().toString(), AuthTokens.none());
    }

    @Test
    public void exports_single_exercise() throws IOException {
        exporter.accept(dump, singletonList(exercise("Crazy query!", "MATCH (n) RETURN COUNT(n) AS count")));

        String expectedBase64 = "AQEBAGphdmEudXRpbC5IYXNoTWHwAQEDAWNvdW70CQA=";
        assertThat(dump).hasContent(
                String.format("MERGE (e:Exercise {instructions: 'Crazy query!', result: '%s'})", expectedBase64)
        );
        assertThatDeserializedResult(expectedBase64, result -> {
            assertThat(result).hasSize(1);
            Map<String, Object> row = result.iterator().next();
            assertThat(row).containsExactly(MapEntry.entry("count", 0L));
        });
    }

    @Test
    public void exports_write_exercises() {
        exporter.accept(dump, singletonList(exercise(
                "Create a node Person whose name is foobar",
                "MATCH (n:Person {name:'foobar'}) RETURN n.name",
                "CREATE (:Person {name:'foobar'})")));

        assertThat(dump).hasContent("MERGE (e:Exercise {instructions: 'Create a node Person whose name is foobar', validationQuery: 'MATCH (n:Person {name:\\'foobar\\'}) RETURN n.name', result: 'AQEBAGphdmEudXRpbC5IYXNoTWHwAQEDAW4ubmFt5QMBZm9vYmHy'})");
        try (Driver driver = GraphDatabase.driver(graphDb.boltURI(), config()); Session session = driver.session()) {
            StatementResult result = session.run("MATCH (n:Person {name:'foobar'}) RETURN n.name");
            assertThat(result.list()).isEmpty(); //rollbacks against remote DB
        }
    }

    @Test
    public void exports_several_exercises() {
        exporter.accept(dump, asList(
                exercise("foo", "MATCH (n:Foo) RETURN COUNT(n) AS foo"),
                exercise("bar", "MATCH (n:Bar) RETURN COUNT(n) AS bar")));

        assertThat(dump).hasContent(
                "MERGE (e:Exercise {instructions: 'foo', result: 'AQEBAGphdmEudXRpbC5IYXNoTWHwAQEDAWZv7wkA'})\n" +
                "MERGE (e:Exercise {instructions: 'bar', result: 'AQEBAGphdmEudXRpbC5IYXNoTWHwAQEDAWJh8gkA'})\n" +
                "MATCH (e:Exercise) WITH e ORDER BY ID(e) WITH COLLECT(e) AS exercises FOREACH (i IN RANGE(0, length(exercises)-2) | FOREACH (first IN [exercises[i]] | FOREACH (second IN [exercises[i+1]] | MERGE (first)-[:NEXT]->(second))))");
    }

    @Test
    public void generates_executable_dump() throws IOException {
        exporter.accept(dump, asList(
                exercise("foo", "MATCH (n:Foo) RETURN COUNT(n) AS foo_count"),
                exercise("bar", "MATCH (n:Bar) RETURN COUNT(n) AS bar_count"),
                exercise("baz", "MATCH (n:Baz) RETURN COUNT(n) AS baz_count", "CREATE (:Baz {name:'meh'})")));

        lines(dump.toPath(), UTF_8).forEachOrdered(line -> {
            try (Driver driver = GraphDatabase.driver(graphDb.boltURI(), config()); Session session = driver.session()) {
                session.run(line);
            }
        });

        try (Driver driver = GraphDatabase.driver(graphDb.boltURI(), config()); Session session = driver.session()) {
            StatementResult result = session.run(
                    "MATCH p=(e1:Exercise)-[:NEXT*]->(e2:Exercise) WITH p ORDER BY LENGTH(p) DESC LIMIT 1 " +
                            "RETURN EXTRACT(exercise IN NODES(p) | exercise.instructions) AS instructions");
            assertThat(result.single().get("instructions").asList(Value::asString)).containsExactly("foo", "bar", "baz");
            result = session.run(
                    "MATCH (e:Exercise) WHERE EXISTS(e.validationQuery) RETURN e.instructions AS instruction");
            assertThat(result.single().get("instruction").asString()).isEqualTo("baz");
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

    private JsonExercise exercise(String instructions, String solutionQuery) {
        return exercise(instructions, solutionQuery, null);
    }

    private JsonExercise exercise(String instructions, String solutionQuery, String writeQuery) {
        JsonExercise exercise = new JsonExercise();
        exercise.setInstructions(instructions);
        exercise.setSolutionQuery(solutionQuery);
        exercise.setWriteQuery(writeQuery);
        return exercise;
    }

    private Config config() {
        return Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE).toConfig();
    }
}
