package net.biville.florent.repl.graph;

import org.neo4j.driver.v1.AuthToken;
import org.neo4j.driver.v1.AuthTokens;

import java.net.URI;
import java.net.URISyntaxException;

public class ReplConfiguration {

    private final URI boltUri;
    private final String username;
    private final String password;
    private String packageToScan;

    public ReplConfiguration(String boltUri, String username, String password, String packageToScan) {
        this.boltUri = parse(boltUri);
        this.username = username;
        this.password = password;
        this.packageToScan = packageToScan;
    }

    public ReplConfiguration(URI boltUri) {
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

    public String getPackageToScan() {
        return packageToScan;
    }

    public AuthToken getAuthToken() {
        if (username == null) {
            return AuthTokens.none();
        }
        return AuthTokens.basic(username, password);
    }
}
