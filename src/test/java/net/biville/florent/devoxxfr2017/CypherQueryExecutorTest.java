package net.biville.florent.devoxxfr2017;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.Value;
import org.neo4j.harness.junit.Neo4jRule;

import static org.assertj.core.api.Assertions.assertThat;

public class CypherQueryExecutorTest {

    @Rule
    public Neo4jRule graphDatabase = new Neo4jRule();

    private CypherQueryExecutor executor;

    @Before
    public void prepare() {
        executor = new CypherQueryExecutor(configuration());
    }

    @Test
    public void executes_Cypher_queries() {
        Value result = executor.readOne("MATCH (n)\n RETURN COUNT(n) AS result;", "result");

        assertThat(result.asLong()).isEqualTo(0L);
    }

    private ConnectionConfiguration configuration() {
        return new ConnectionConfiguration(graphDatabase.boltURI());
    }
}
