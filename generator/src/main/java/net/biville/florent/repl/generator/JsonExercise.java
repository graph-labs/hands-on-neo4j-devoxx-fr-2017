package net.biville.florent.repl.generator;

import com.google.gson.annotations.Expose;

public class JsonExercise {

    @Expose private String instructions;
    @Expose private String solutionQuery;
    @Expose private String writeQuery;

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getSolutionQuery() {
        return solutionQuery;
    }

    public void setSolutionQuery(String solutionQuery) {
        this.solutionQuery = solutionQuery;
    }

    public String getWriteQuery() {
        return writeQuery;
    }

    public void setWriteQuery(String writeQuery) {
        this.writeQuery = writeQuery;
    }

    public boolean requiresWrite() {
        return writeQuery != null;
    }

    @Override
    public String toString() {
        return "JsonExercise{" +
                "instructions='" + instructions + '\'' +
                ", solutionQuery='" + solutionQuery + '\'' +
                ", writeQuery='" + writeQuery + '\'' +
                '}';
    }
}
