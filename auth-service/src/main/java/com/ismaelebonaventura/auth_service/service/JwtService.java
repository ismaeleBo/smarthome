package com.ismaelebonaventura.auth_service.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ismaelebonaventura.auth_service.config.JwtProperties;
import com.ismaelebonaventura.auth_service.model.Role;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class JwtService {

    private final JwtProperties props;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtService(JwtProperties props) {
        this.props = props;
        this.algorithm = Algorithm.HMAC256(props.getSecret());
        this.verifier = JWT.require(algorithm)
                .withIssuer(props.getIssuer())
                .build();
    }

    public String createToken(UUID userId, Role role) {
        Instant now = Instant.now();
        Instant exp = now.plus(props.getExpiresMinutes(), ChronoUnit.MINUTES);

        return JWT.create()
                .withIssuer(props.getIssuer())
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .withSubject(userId.toString())
                .withClaim("role", role.name())
                .sign(algorithm);
    }

    public DecodedJWT verify(String token) throws JWTVerificationException {
        return verifier.verify(token);
    }

    public UUID extractUserId(DecodedJWT jwt) {
        return UUID.fromString(jwt.getSubject());
    }

    public Role extractRole(DecodedJWT jwt) {
        String role = jwt.getClaim("role").asString();
        return Role.valueOf(role);
    }
}
