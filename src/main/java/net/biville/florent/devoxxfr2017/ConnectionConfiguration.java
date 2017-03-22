package net.biville.florent.devoxxfr2017;

import org.neo4j.driver.v1.AuthToken;
import org.neo4j.driver.v1.AuthTokens;

import java.net.URI;
import java.net.URISyntaxException;

public class ConnectionConfiguration {

    private final URI boltUri;
    private final String username;
    private final String password;

    public ConnectionConfiguration(String boltUri, String username, String password) {
        this.boltUri = parse(boltUri);
        this.username = username;
        this.password = password;
    }

    ConnectionConfiguration(URI boltUri) {
        this.boltUri = boltUri;
        username = null;
        password = null;
    }

    private static URI parse(String boltUri) {
        try {
            return new URI(boltUri);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid Bolt URI", e);
        }
    }

    public URI getBoltUri() {
        return boltUri;
    }

    public AuthToken getAuthToken() {
        if (username == null) {
            return AuthTokens.none();
        }
        return AuthTokens.basic(username, password);
    }
}
