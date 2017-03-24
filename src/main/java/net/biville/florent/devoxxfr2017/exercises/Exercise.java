package net.biville.florent.devoxxfr2017.exercises;

import java.util.Arrays;
import java.util.Objects;

public class Exercise {

    private final String statement;
    private final byte[] serializedResult;

    public Exercise(String statement,
                    byte[] serializedResult) {

        this.statement = statement;
        this.serializedResult = serializedResult;
    }

    public String getStatement() {
        return statement;
    }

    public byte[] getSerializedResult() {
        return serializedResult;
    }

    @Override
    public int hashCode() {
        return Objects.hash(statement, Arrays.hashCode(serializedResult));
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
        return Objects.equals(this.statement, other.statement)
                && Arrays.equals(this.serializedResult, other.serializedResult);
    }
}
