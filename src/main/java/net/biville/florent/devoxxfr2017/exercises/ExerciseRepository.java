package net.biville.florent.devoxxfr2017.exercises;

import net.biville.florent.devoxxfr2017.graph.cypher.CypherQueryExecutor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ExerciseRepository {

    private final CypherQueryExecutor executor;

    public ExerciseRepository(CypherQueryExecutor executor) {
        this.executor = executor;
    }

    public List<Map<String, Object>> computeResult(String query) {
        return executor.execute(query);
    }

    public Optional<Exercise> getNextExercise(Exercise currentExercise) {
        //TODO
        return null;
    }
}
