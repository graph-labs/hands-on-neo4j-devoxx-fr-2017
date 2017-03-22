package net.biville.florent.devoxxfr2017;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;

public class CypherQueryExecutor {

    private final ConnectionConfiguration configuration;

    public CypherQueryExecutor(ConnectionConfiguration configuration) {
        this.configuration = configuration;
    }

    public Value readOne(String query, String column) {
        try (Driver driver = GraphDatabase.driver(configuration.getBoltUri(), configuration.getAuthToken());
             Session session = driver.session()) {

            StatementResult result = session.run(query);
            return result.single().get(column);
        }
    }
}
