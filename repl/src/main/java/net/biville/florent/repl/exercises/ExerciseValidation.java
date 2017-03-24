package net.biville.florent.repl.exercises;

public class ExerciseValidation {

    private final boolean success;
    private final String report;

    public ExerciseValidation(ResultDifference difference) {
        this(difference.isEmpty(), difference.getReport());
    }

    public ExerciseValidation(boolean success, String report, Object... args) {
        this.success = success;
        this.report = String.format(report, args);
    }

    public String getReport() {
        return report;
    }

    public boolean isSuccessful() {
        return success;
    }
}
