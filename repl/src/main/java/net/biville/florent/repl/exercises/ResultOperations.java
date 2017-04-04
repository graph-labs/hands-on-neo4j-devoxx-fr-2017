package net.biville.florent.repl.exercises;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

class ResultOperations {

    public static ResultDifference difference(List<Map<String, Object>> actualResult,
                                              List<Map<String, Object>> expectedResult) {

        int rows = actualResult.size();
        ResultDifference difference = new ResultDifference(rows);
        for (int i = 0; i < rows; i++) {
            Map<String, Object> actualRow = actualResult.get(i);
            Map<String, Object> expectedRow = expectedResult.get(i);
            difference.add(rowDifference(actualRow, expectedRow));
        }
        return difference;
    }

    private static ResultRowDifference rowDifference(Map<String, Object> actualRow,
                                                     Map<String, Object> expectedRow) {

        if (actualRow.equals(expectedRow)) {
            return ResultRowDifference.empty();
        }

        return computeDifference(actualRow, expectedRow);
    }

    private static ResultRowDifference computeDifference(Map<String, Object> actualRow, Map<String, Object> expectedRow) {
        Set<Map.Entry<String, Object>> actualEntries = actualRow.entrySet();
        Set<Map.Entry<String, Object>> expectedEntries = expectedRow.entrySet();

        return new ResultRowDifference(
                expectedEntries.stream().sorted(comparing(Map.Entry::getKey)).filter(e -> !actualEntries.contains(e)).collect(Collectors.toList()),
                actualEntries.stream().sorted(comparing(Map.Entry::getKey)).filter(a -> !expectedEntries.contains(a)).collect(Collectors.toList())
        );
    }
}
