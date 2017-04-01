package net.biville.florent.repl.exercises;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class TraineeSession {

    private final ExerciseRepository repository;
    private final ExerciseValidator validator;
    private boolean completed = false;

    public TraineeSession(ExerciseRepository repository,
                          ExerciseValidator validator) {

        this.repository = repository;
        this.validator = validator;
    }

    public void init(InputStream stream) {
        repository.importExercises(stream);
    }

    public ExerciseValidation validate(List<Map<String, Object>> actualResult) {
        ExerciseValidation validation = validator.validate(actualResult, getCurrentExercise());
        if (validation.isSuccessful()) {
            completed = !repository.moveToNextExercise();
        }
        return validation;
    }

    public boolean isCompleted() {
        return completed;
    }

    public Exercise getCurrentExercise() {
        return repository.findCurrentExercise();
    }

    public void reset() {
        repository.resetProgression();
    }
}
