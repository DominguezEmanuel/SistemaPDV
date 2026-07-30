package com.sistemapdv.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Servicio para gestionar operaciones relacionadas con JWT (JSON Web Tokens).
 * Lee la configuración de clave secreta y tiempo de expiración desde el archivo de propiedades.
 */
@Slf4j
@Service
public class JwtService {

    /**
     * Clave secreta para firmar y verificar los JWT.
     * Se carga desde la propiedad jwt.secret del archivo application.properties.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Tiempo de expiración del JWT en milisegundos.
     * Se carga desde la propiedad jwt.expiration del archivo application.properties.
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Genera un token JWT para un usuario específico.
     *
     * @param userDetails identificador único del usuario (generalmente el username o id)
     * @return token JWT firmado y codificado
     */
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        log.info("Fecha {}", now);
        Date expiryDate = new Date(now.getTime() + expiration);
        log.info("Fecha expiracion {}", expiryDate);
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrae el username (identificador del usuario) del token JWT.
     *
     * @param token token JWT
     * @return subject del token (username o id del usuario)
     */
    public String extractUsername(String token) {
        return getAllClaims(token).getSubject();
    }

    /**
     * Extrae la fecha de expiración del token JWT.
     *
     * @param token token JWT
     * @return fecha de expiración del token
     */
    public Date getExpiration(String token) {
        return getAllClaims(token).getExpiration();
    }

    /**
     * Verifica si el token JWT ha expirado.
     *
     * @param token token JWT
     * @return true si el token ha expirado, false en caso contrario
     */
    public boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

    /**
     * Valida si el token JWT es válido y le corresponde al usuario.
     * Verifica que el token no haya expirado y que pueda ser parseado correctamente.
     *
     * @param token token JWT a validar
     * @param userDetails datos del usuario dueño del token
     * @return true si el token es válido, false en caso contrario
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    /**
     * Extrae todos los claims (información) del token JWT.
     * Los claims son los datos almacenados dentro del token.
     *
     * @param token token JWT
     * @return objeto Claims con toda la información del token
     */
    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Obtiene la clave de firma para firmar y verificar los tokens JWT.
     * Utiliza el algoritmo HS256 con la clave secreta configurada.
     *
     * @return SecretKey para operaciones de firma
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

}
