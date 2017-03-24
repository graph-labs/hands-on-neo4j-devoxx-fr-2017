package net.biville.florent.repl.generator;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GraphDatabaseSupplier implements Supplier<GraphDatabaseService> {

    private final File dbDirectory;
    private final Consumer<GraphDatabaseService> populator;

    public GraphDatabaseSupplier(File dbDirectory,
                                 Consumer<GraphDatabaseService> populator) {

        this.dbDirectory = dbDirectory;
        this.populator = populator;
    }

    @Override
    public GraphDatabaseService get() {
        GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dbDirectory);
        populator.accept(graphDb);
        return graphDb;
    }
}
