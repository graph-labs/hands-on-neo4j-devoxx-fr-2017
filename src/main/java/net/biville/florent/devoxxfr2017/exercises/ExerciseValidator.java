package net.biville.florent.devoxxfr2017.exercises;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import net.biville.florent.devoxxfr2017.logging.SimpleLogger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExerciseValidator {

    private static final SimpleLogger LOGGER = new SimpleLogger();
    private final Kryo serializer;

    public ExerciseValidator(Kryo serializer) {
        this.serializer = serializer;
    }

    public ExerciseValidation validate(List<Map<String, Object>> actualResult, Exercise currentExercise) {
        return compare(actualResult, deserialize(currentExercise.getSerializedResult()));
    }

    private ExerciseValidation compare(List<Map<String, Object>> actualResult, List<Map<String, Object>> expectedResult) {
        int expectedSize = expectedResult.size();
        int actualSize = actualResult.size();
        if (actualSize != expectedSize) {
            return new ExerciseValidation(false, "Expected %d returned rows, got %d", expectedSize, actualSize);
        }
        return new ExerciseValidation(ResultOperations.difference(actualResult, expectedResult));
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> deserialize(byte[] input) {

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(input);
             Input output = new Input(inputStream)) {

            return (List<Map<String, Object>>) serializer.readObject(output, List.class);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return Collections.emptyList();
        }
    }
}
