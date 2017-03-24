package net.biville.florent.repl.exercises;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Maps.newHashMap;

public class ResultOperationsTest {

    @Test
    public void computes_record_difference() {
        List<Map<String, Object>> actualResult = asList(
                newHashMap("foo2", "bar2"),
                newHashMap("baz", "bah")
        );
        List<Map<String, Object>> expectedResult = asList(
                newHashMap("foo", "bar"),
                newHashMap("baz", "bah")
        );

        ResultDifference difference = ResultOperations.difference(actualResult, expectedResult);

        assertThat(difference.getReport())
                .isEqualTo(
                        "Expected records not found:" +
                        "\n\t(column foo, value bar)" +
                        "\nUnexpected records found:" +
                        "\n\t(column foo2, value bar2)");
    }

    @Test
    public void reports_no_difference() {
        List<Map<String, Object>> actualResult = singletonList(newHashMap("foo", "bar"));
        List<Map<String, Object>> expectedResult = singletonList(newHashMap("foo", "bar"));

        ResultDifference difference = ResultOperations.difference(actualResult, expectedResult);

        assertThat(difference.getReport()).isEqualTo("All good!");
    }
}
