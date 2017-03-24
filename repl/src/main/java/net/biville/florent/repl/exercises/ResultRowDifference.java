package net.biville.florent.repl.exercises;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ResultRowDifference {

    private final List<Map.Entry> expectedNotFound;
    private final List<Map.Entry> unexpectedFound;

    public ResultRowDifference() {
        this(Collections.emptyList(), Collections.emptyList());
    }

    public static ResultRowDifference empty() {
        return new ResultRowDifference();
    }

    public ResultRowDifference(List<Map.Entry> expectedNotFound, List<Map.Entry> unexpectedFound) {
        this.expectedNotFound = expectedNotFound;
        this.unexpectedFound = unexpectedFound;
    }

    public boolean isEmpty() {
        return expectedNotFound.isEmpty() && unexpectedFound.isEmpty();
    }

    public List<Map.Entry> getExpectedNotFound() {
        return expectedNotFound;
    }

    public List<Map.Entry> getUnexpectedFound() {
        return unexpectedFound;
    }
}
