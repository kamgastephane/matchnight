package com.inetti.matchnight.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inetti.matchnight.configuration.JWTConfig;
import com.inetti.matchnight.controller.AuthenticationController;
import com.inetti.matchnight.data.dto.InettoContract;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    private final AuthenticationManager authenticationManager;
    private final JWTConfig config;
    private final ObjectMapper mapper;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTConfig jwtConfig) {
        this.authenticationManager = authenticationManager;
        this.config = jwtConfig;
        setFilterProcessesUrl(AuthenticationController.LOGIN_URL);
        mapper = new ObjectMapper();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        final JWTAuthenticationFilter.User user;
        try {
            user = mapper.readValue(request.getReader(), JWTAuthenticationFilter.User.class);
        } catch (IOException e) {
            LOGGER.debug("failed to extract the credentials from the login request", e);
            throw new AuthenticationCredentialsNotFoundException("failed to extract the credentials from the login request", e);
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.username, user.password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain, Authentication authentication) {
        InettoContract user = ((InettoContract) authentication.getPrincipal());

        List<String> roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String token = Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(config.getSigningKey().getBytes()), SignatureAlgorithm.HS512)
                .setHeaderParam("type", "JWT")
                .setIssuedAt(new Date())
                .setIssuer("matchnight")
                .setSubject(user.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * config.getTokenValidityInHours()))
                .claim(JWTConfig.CLAIMS_ROLE, roles)
                .compact();

        response. addHeader(HttpHeaders.AUTHORIZATION, JWTConfig.BEARER_PREFIX + token);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class User {

        private final String username;
        private final String password;

        @JsonIgnoreProperties(ignoreUnknown = true)
        @JsonCreator
        public User(@NotNull @JsonProperty("username") String username, @NotNull @JsonProperty("password") String password) {
            this.username = username;
            this.password = password;
        }
    }
}
