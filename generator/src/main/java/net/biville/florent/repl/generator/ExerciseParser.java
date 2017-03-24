package net.biville.florent.repl.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.function.Function;

public class ExerciseParser implements Function<File, Collection<JsonExercise>> {

    private final Gson gson;

    public ExerciseParser() {
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

    @Override
    public Collection<JsonExercise> apply(File exerciseDefinition) {
        try (Reader reader = new FileReader(exerciseDefinition)) {
            return gson.fromJson(reader, new TypeToken<Collection<JsonExercise>>() {}.getType());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
