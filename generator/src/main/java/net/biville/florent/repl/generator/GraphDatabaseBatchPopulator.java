package net.biville.florent.repl.generator;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class GraphDatabaseBatchPopulator implements Consumer<GraphDatabaseService> {

    private final File cypherDump;

    public GraphDatabaseBatchPopulator(File cypherDump) {
        this.cypherDump = cypherDump;
    }

    @Override
    public void accept(GraphDatabaseService graphDb) {
        try (BufferedReader reader = new BufferedReader(new FileReader(cypherDump))) {
            AtomicReference<Transaction> currentTx = new AtomicReference<>();
            reader.lines().forEachOrdered(line -> {
                executeLine(graphDb, currentTx, line);
            });

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    private void executeLine(GraphDatabaseService graphDb, AtomicReference<Transaction> currentTx, String line) {
        String trimmedLine = line.trim();
        if (trimmedLine.equals("begin")) {
            currentTx.set(graphDb.beginTx());
        }
        else if (trimmedLine.equals("commit")) {
            Transaction transaction = currentTx.get();
            transaction.success();
            transaction.close();
        }
        else if (trimmedLine.equals("schema await")) {
            try (Transaction ignored = graphDb.beginTx()) {
                graphDb.schema().awaitIndexesOnline(2, TimeUnit.SECONDS);
            }
        }
        else if (!trimmedLine.isEmpty()) {
            graphDb.execute(line);
        }
    }
}
