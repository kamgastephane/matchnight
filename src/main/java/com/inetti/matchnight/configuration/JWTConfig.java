package com.inetti.matchnight.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JWTConfig {

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String CLAIMS_ROLE = "Role ";

    private final String signingKey;
    private final Long tokenValidityInHours;


    public JWTConfig(@Value("${jwt.key}") String signingKey, @Value("${jwt.validityInHours}") Long tokenValidityInHours) {
        this.signingKey = signingKey;
        this.tokenValidityInHours = tokenValidityInHours;
    }

    public String getSigningKey() {
        return signingKey;
    }

    public Long getTokenValidityInHours() {
        return tokenValidityInHours;
    }
}
