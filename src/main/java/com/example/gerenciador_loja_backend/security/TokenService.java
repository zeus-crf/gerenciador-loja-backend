package com.example.gerenciador_loja_backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.gerenciador_loja_backend.models.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    private static final String ISSUER = "login-auth-api";

    // 🔐 Gera token JWT
    public String generateToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(usuario.getUsername())
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(Instant.now().plus(2, ChronoUnit.HOURS))
                    .sign(algorithm);

        } catch (JWTCreationException ex) {
            throw new RuntimeException("Erro ao gerar token JWT", ex);
        }
    }

    // ✅ Valida token
    public boolean isTokenValid(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWT.require(algorithm).withIssuer(ISSUER).build().verify(token);
            return true;
        } catch (JWTVerificationException ex) {
            System.out.println("❌ Token inválido: " + ex.getMessage());
            return false;
        }
    }

    // 👤 Extrai username do token
    public String getUsername(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException ex) {
            throw new RuntimeException("Token inválido ou expirado", ex);
        }
    }
}
