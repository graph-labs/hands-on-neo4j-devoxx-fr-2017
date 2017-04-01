package net.biville.florent.repl.graph.cypher;

import net.biville.florent.repl.graph.ReplConfiguration;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class CypherQueryExecutor {

    private final ReplConfiguration configuration;

    public CypherQueryExecutor(ReplConfiguration configuration) {
        this.configuration = configuration;
    }

    public List<Map<String, Object>> commit(Function<Transaction, List<Map<String, Object>>> callback) {
        try (Driver driver = GraphDatabase.driver(configuration.getBoltUri(), configuration.getAuthToken());
             Session session = driver.session();
             Transaction tx = session.beginTransaction()) {

            List<Map<String, Object>> result = callback.apply(tx);
            tx.success();
            return result;
        }
    }

    public void commit(Consumer<Transaction> callback) {
        try (Driver driver = GraphDatabase.driver(configuration.getBoltUri(), configuration.getAuthToken());
             Session session = driver.session();
             Transaction tx = session.beginTransaction()) {

            callback.accept(tx);
            tx.success();
        }
    }

    public void rollback(Consumer<Transaction> callback) {
        try (Driver driver = GraphDatabase.driver(configuration.getBoltUri(), configuration.getAuthToken());
             Session session = driver.session();
             Transaction tx = session.beginTransaction()) {

            callback.accept(tx);
            tx.failure();
        }
    }

    public List<Map<String, Object>> rollback(Function<Transaction, List<Map<String, Object>>> callback) {
        try (Driver driver = GraphDatabase.driver(configuration.getBoltUri(), configuration.getAuthToken());
             Session session = driver.session();
             Transaction tx = session.beginTransaction()) {

            List<Map<String, Object>> result = callback.apply(tx);
            tx.failure();
            return result;
        }
    }
}
