package com.ismaelebonaventura.analytics_service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ismaelebonaventura.analytics_service.config.JwtProperties;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JwtService {

    private final JWTVerifier verifier;

    public JwtService(JwtProperties props) {
        Algorithm algorithm = Algorithm.HMAC256(props.getSecret());
        this.verifier = JWT.require(algorithm)
                .withIssuer(props.getIssuer())
                .build();
    }

    public DecodedJWT verify(String token) {
        return verifier.verify(token);
    }

    public UUID extractUserId(DecodedJWT jwt) {
        return UUID.fromString(jwt.getSubject());
    }

    public String extractRole(DecodedJWT jwt) {
        return jwt.getClaim("role").asString();
    }
}