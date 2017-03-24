package net.biville.florent.devoxxfr2017;

import net.biville.florent.devoxxfr2017.graph.ConnectionConfiguration;
import net.biville.florent.devoxxfr2017.graph.cypher.CypherQueryExecutor;
import org.assertj.core.util.Maps;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.List;
import java.util.Map;

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
        List<Map<String, Object>> result = executor.execute("MATCH (n)\n RETURN COUNT(n) AS result;");

        assertThat(result).containsExactly(Maps.newHashMap("result", 0L));
    }

    private ConnectionConfiguration configuration() {
        return new ConnectionConfiguration(graphDatabase.boltURI());
    }
}
