package net.biville.florent.repl.exercises;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import net.biville.florent.repl.logging.ConsoleLogger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExerciseValidator {

    private final ConsoleLogger logger;
    private final Kryo serializer;

    public ExerciseValidator(ConsoleLogger logger, Kryo serializer) {
        this.logger = logger;
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

            return (List<Map<String, Object>>) serializer.readObject(output, ArrayList.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return Collections.emptyList();
        }
    }
}
