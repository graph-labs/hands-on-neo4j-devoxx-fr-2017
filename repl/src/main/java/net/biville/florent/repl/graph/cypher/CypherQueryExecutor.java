package net.biville.florent.repl.graph.cypher;

import net.biville.florent.repl.graph.ReplConfiguration;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;

public class CypherQueryExecutor {

    private final ReplConfiguration configuration;

    public CypherQueryExecutor(ReplConfiguration configuration) {
        this.configuration = configuration;
    }

    public void executeAll(String... queries) {
        try (Driver driver = GraphDatabase.driver(configuration.getBoltUri(), configuration.getAuthToken());
             Session session = driver.session()) {

            stream(queries).forEachOrdered(session::run);
        }
    }

    public List<Map<String, Object>> execute(String query) {
        return execute(query, emptyMap());
    }

    public List<Map<String, Object>> execute(String query, Map<String, Object> parameters) {
        try (Driver driver = GraphDatabase.driver(configuration.getBoltUri(), configuration.getAuthToken());
             Session session = driver.session()) {

            StatementResult result = session.run(query, parameters);
            return result.list(Record::asMap);
        }
    }
}
