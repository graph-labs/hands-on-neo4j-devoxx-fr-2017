package net.biville.florent.repl.graph.cypher;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

public class CypherStatementValidatorTest {

    private CypherStatementValidator validator = new CypherStatementValidator();

    @Test
    public void validates_valid_cypher_statements() {
        assertThat(validator.validate("MATCH (n) RETURN n;")).isEmpty();
        assertThat(validator.validate("CREATE (n)\n RETURN n;")).isEmpty();
        assertThat(validator.validate("MATCH\n(n)\nOPTIONAL MATCH (m:First)\n RETURN n\n;")).isEmpty();
        assertThat(validator.validate("RETURN\n[1,2,3];")).isEmpty();
    }

    @Test
    public void diagnoses_invalid_statement() {
        assertThat(validator.validate("toto;"))
                .hasSize(1)
                .extracting(CypherError::getLine, CypherError::getCharPositionInLine, CypherError::getMessage)
                .containsOnly(tuple(
                        1,
                        0,
                        "mismatched input 'toto' expecting {<EOF>, CYPHER, EXPLAIN, PROFILE, USING, CREATE, DROP, LOAD, WITH, OPTIONAL, MATCH, UNWIND, MERGE, SET, DETACH, DELETE, REMOVE, FOREACH, RETURN, START, CALL, SP}"));
    }
}
