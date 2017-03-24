package net.biville.florent.repl.generator;

import com.google.gson.annotations.Expose;

public class JsonExercise {

    @Expose
    private String statement;
    @Expose
    private String queryToExecute;

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getQueryToExecute() {
        return queryToExecute;
    }

    public void setQueryToExecute(String queryToExecute) {
        this.queryToExecute = queryToExecute;
    }
}
