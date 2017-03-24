package net.biville.florent.repl.generator;

import org.neo4j.graphdb.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public enum ResultConverter implements Function<Result, List<Map<String,Object>>> {

    INSTANCE;

    @Override
    public List<Map<String, Object>> apply(Result input) {
        List<Map<String, Object>> result = new ArrayList<>();
        while (input.hasNext()) {
            result.add(convertMap(input));
        }
        return result;
    }

    private Map<String, Object> convertMap(Result input) {
        Map<String, Object> row = new HashMap<>();
        input.next().forEach(row::put);
        return row;
    }
}
