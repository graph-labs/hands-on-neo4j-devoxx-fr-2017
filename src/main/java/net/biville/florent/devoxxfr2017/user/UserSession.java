package net.biville.florent.devoxxfr2017.user;

public class UserSession {

    private final String username;

    public UserSession(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
