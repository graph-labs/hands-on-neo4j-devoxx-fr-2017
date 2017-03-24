package net.biville.florent.repl.generator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.test.rule.ImpermanentDatabaseRule;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.nio.file.Files.write;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.neo4j.graphdb.Label.label;

public class GraphDatabaseBatchPopulatorTest {

    @Rule
    public ImpermanentDatabaseRule graphDb = new ImpermanentDatabaseRule();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void populates_a_database() throws IOException {
        File cypherDump = getCypherDump(asList(
                "begin", "CREATE (:People {name:'Florent'})", "commit",
                "begin", "CREATE INDEX ON :People(name)", "commit",
                "schema await"
        ));
        GraphDatabaseBatchPopulator populator = new GraphDatabaseBatchPopulator(cypherDump);

        populator.accept(graphDb);

        try (Transaction ignored = graphDb.beginTx()) {
            assertThat(graphDb.getAllNodes())
                    .extracting(node -> node.getProperty("name"), Node::getLabels)
                    .containsExactly(tuple("Florent", singletonList(label("People"))));
            assertThat(graphDb.schema().getIndexes())
                    .extracting(IndexDefinition::getLabel, IndexDefinition::getPropertyKeys)
                    .containsExactly(tuple(label("People"), singletonList("name")));
        }
    }

    private File getCypherDump(List<String> lines) throws IOException {
        File file = temporaryFolder.newFile();
        write(file.toPath(), lines);
        return file;
    }
}
