package net.biville.florent.repl.generator;

import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

public class ExerciseParserTest {

    @Test
    public void parses_json_file() throws Exception {
        ExerciseParser parser = new ExerciseParser();

        Collection<JsonExercise> exercises = parser.apply(classpathFile("/example.json"));

        assertThat(exercises)
                .extracting(JsonExercise::getInstructions, JsonExercise::getSolutionQuery, JsonExercise::getWriteQuery)
                .containsExactly(
                        tuple("This is a very simple exercise", "MATCH (n) RETURN COUNT(n) AS result", null),
                        tuple("This is a write exercise", "MATCH (n:Foo {bar:'baz'}) RETURN COUNT(n) AS result", "CREATE (n:Foo {bar:'baz'})")
                );
    }

    private File classpathFile(String name) throws URISyntaxException {
        return new File(this.getClass().getResource(name).toURI());
    }

}
