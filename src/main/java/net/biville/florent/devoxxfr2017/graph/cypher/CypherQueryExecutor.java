package net.biville.florent.devoxxfr2017.graph.cypher;

import net.biville.florent.devoxxfr2017.graph.ConnectionConfiguration;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;

public class CypherQueryExecutor {

    private final ConnectionConfiguration configuration;

    public CypherQueryExecutor(ConnectionConfiguration configuration) {
        this.configuration = configuration;
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
