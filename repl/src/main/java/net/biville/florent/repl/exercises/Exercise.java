package net.biville.florent.repl.exercises;

import java.util.Arrays;
import java.util.Objects;

public class Exercise {

    private final String instructions;
    private final String writeValidationQuery;
    private final byte[] serializedResult;

    public Exercise(String instructions,
                    String writeValidationQuery,
                    byte[] serializedResult) {

        this.instructions = instructions;
        this.writeValidationQuery = writeValidationQuery;
        this.serializedResult = serializedResult;
    }

    public String getInstructions() {
        return instructions;
    }

    public byte[] getSerializedResult() {
        return serializedResult;
    }

    public String getWriteValidationQuery() {
        return writeValidationQuery;
    }

    public boolean requiresWrites() {
        return writeValidationQuery != null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(instructions, Arrays.hashCode(serializedResult));
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Exercise other = (Exercise) obj;
        return Objects.equals(this.instructions, other.instructions)
                && Arrays.equals(this.serializedResult, other.serializedResult);
    }
}
