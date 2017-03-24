package net.biville.florent.devoxxfr2017.user;

import net.biville.florent.devoxxfr2017.graph.cypher.CypherQueryExecutor;
import net.biville.florent.devoxxfr2017.exercises.Exercise;

import java.util.HashMap;
import java.util.Map;

public class UserSessionRepository {

    private final CypherQueryExecutor executor;

    public UserSessionRepository(CypherQueryExecutor executor) {
        this.executor = executor;
    }

    public void create(UserSession session) {
        // TODO: this should be called by a command named `:login`
        executor.execute("MERGE (:Session {user: {username}})", parameters(session));
    }

    public void saveProgression(UserSession session, Exercise currentExercise) {
        // TODO remove previous progression marker if exists
        //      and save to next exercise, if any
        //      ... in 1 tx, need to change executor!
    }

    private Map<String, Object> parameters(UserSession session) {
        Map<String, Object> parameters = new HashMap<>(1);
        parameters.put("username", session.getUsername());
        return parameters;
    }
}
