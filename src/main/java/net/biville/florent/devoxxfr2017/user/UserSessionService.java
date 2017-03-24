package net.biville.florent.devoxxfr2017.user;

public class UserSessionService {

    private final UserSessionRepository repository;

    public UserSessionService(UserSessionRepository repository) {
        this.repository = repository;
    }

    public void create(UserSession session) {
        repository.create(session);
    }

}
