package net.biville.florent.repl.exercises;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResultDifference {

    private final List<ResultRowDifference> differences;

    public ResultDifference(int size) {
        differences = new ArrayList<>(size);
    }

    public boolean isEmpty() {
        return differences.stream().allMatch(ResultRowDifference::isEmpty);
    }

    public String getReport() {
        String expectedNotFound = formatDiff(differences, ResultRowDifference::getExpectedNotFound);
        String unexpectedFound = formatDiff(differences, ResultRowDifference::getUnexpectedFound);
        if (expectedNotFound.isEmpty() && unexpectedFound.isEmpty()) {
            return "All good!";
        }

        return String.format("Expected records not found:%s%nUnexpected records found:%s",
                expectedNotFound.isEmpty() ? "\tNone" : expectedNotFound,
                unexpectedFound.isEmpty() ? "\tNone" : unexpectedFound);
    }

    public void add(ResultRowDifference resultRowDifference) {
        differences.add(resultRowDifference);
    }

    private String formatDiff(List<ResultRowDifference> differences, Function<ResultRowDifference, List<Map.Entry>> extractor) {
        return differences.stream()
                .map(extractor)
                .map(enf -> {
                    if (enf.isEmpty()) {
                        return "";
                    }
                    return String.join(",", formatEntries(enf));
                })
                .filter(s -> !s.isEmpty())
                .reduce("", (a, b) -> String.format("%s%n%s", a, b));
    }

    private List<String> formatEntries(List<Map.Entry> entries) {
        return entries.stream()
                .map(e -> String.format("\t(column %s, value %s)", e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
