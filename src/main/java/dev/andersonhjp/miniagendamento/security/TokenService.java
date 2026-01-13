package dev.andersonhjp.miniagendamento.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import dev.andersonhjp.miniagendamento.model.LoginUser;
import lombok.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class TokenService {

    // Essa chave deve estar no seu application.properties para segurança
    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(LoginUser user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("auth-api") // Identificador da sua aplicação
                    .withSubject(user.getEmail()) // Guarda o email do usuário no token
                    .withExpiresAt(genExpirationDate()) // Define quando o token morre
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token", exception);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject(); // Se for válido, retorna o email (subject)
        } catch (JWTVerificationException exception) {
            return ""; // Se o token for inválido ou expirado, retorna vazio
        }
    }

    // Define que o token expira em 2 horas
    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
}
