package net.biville.florent.repl.generator;

import org.assertj.core.groups.Tuple;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import java.io.IOException;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class GraphDatabaseSupplierTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void supplies_populated_database() throws IOException {
        GraphDatabaseSupplier supplier = new GraphDatabaseSupplier(folder.newFolder("neotest"), graphDb -> {
            try (Transaction transaction = graphDb.beginTx()) {
                graphDb.execute("CREATE (:People {name:'Marouane'})");
                transaction.success();
            }
        });

        GraphDatabaseService graphDb = supplier.get();

        try (Transaction ignored = graphDb.beginTx()) {
            assertThat(graphDb.getAllNodes())
                    .extracting(node -> node.getProperty("name"), Node::getLabels)
                    .containsExactly(Tuple.tuple("Marouane", singletonList(Label.label("People"))));
        }
    }
}
