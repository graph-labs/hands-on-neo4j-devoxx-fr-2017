package net.biville.florent.repl.graph.cypher;

import org.junit.Test;

import java.util.Collection;

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

    @Test
    public void validates_wrong_regex_operator() {
        assertThat(validator.validate(" MATCH (u:User) WHERE LOWER(u.login) ~= '.*a.*' DELETE u;"))
                .hasSize(1)
                .extracting(CypherError::getLine, CypherError::getCharPositionInLine, CypherError::getMessage)
                .containsOnly(tuple(
                        1, 37, "token recognition error at: '~'"
                ));
    }

    @Test
    public void exists_in_where_should_not_raise_errors() {
        assertThat(validator.validate("MATCH (m:Movie) WHERE EXISTS(m.title) RETURN m;")).isEmpty();
    }
}
