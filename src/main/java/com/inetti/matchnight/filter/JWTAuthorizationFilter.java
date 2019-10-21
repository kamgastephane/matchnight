package com.inetti.matchnight.filter;

import com.inetti.matchnight.configuration.JWTConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Authentication filter to be executed on each request to check the presence of the jwt token
 * based on https://medium.com/omarelgabrys-blog/microservices-with-spring-boot-authentication-with-jwt-part-3-fafc9d7187e8
 */
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final JWTConfig config;

    public JWTAuthorizationFilter(JWTConfig config) {
        this.config = config;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(header == null || !header.startsWith(JWTConfig.BEARER_PREFIX)) {
            filterChain.doFilter(request, response);  		// If not valid, go to the next filter.
            return;
        }
        String token = header.replace(JWTConfig.BEARER_PREFIX, "");
        // 4. Validate the token
        Claims claims = Jwts.parser()
                .setSigningKey(config.getSigningKey().getBytes())
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject();
        if(username != null) {
            @SuppressWarnings("unchecked")
            List<String> authorities = (List<String>) claims.get(JWTConfig.CLAIMS_ROLE);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    username, null, authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
}
