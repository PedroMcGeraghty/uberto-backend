package ar.edu.unsam.phm.uberto.security

import ar.edu.unsam.phm.uberto.model.UserAuthCredentials
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.JWTVerifier
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.constraints.NotNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*

@Component
class TokenJwtUtil {

    //Firma del token
    @Value("\${jwt.secret.key}")
    lateinit var secretKey: String

    @Value("\${jwt.time.expiration}")
    var accessTokenMinutes: Int = 30

    @Value("\${jwt.user.generator}")
    var userGeneration: String = ""

    fun generate(user: UserAuthCredentials, userId: Long): String {
        val algorithm = Algorithm.HMAC512(this.secretKey)
        val username = user.username
        return JWT.create()
            .withIssuer(this.userGeneration)
            .withSubject(username)
            .withIssuedAt(Date())
            .withClaim("rol", listOf("ROLE_${user.role}"))
            .withClaim("userID", userId)
            .withExpiresAt(Date(System.currentTimeMillis() + accessTokenMinutes))
            .withJWTId(UUID.randomUUID().toString())
            .withNotBefore(Date(System.currentTimeMillis()))
            .sign(algorithm)
    }

    fun getAllClaims(jwtToken: String): Claims {
        val secretKey = Keys.hmacShaKeyFor(this.secretKey.toByteArray())
        val parser = Jwts.parser().verifyWith(secretKey).build()
        return parser.parseSignedClaims(jwtToken).payload
    }

    fun validateToken(token: String): DecodedJWT {
        try {
            val algorithm = Algorithm.HMAC512(secretKey)
            val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(this.userGeneration).build()
            return verifier.verify(token)
        } catch (ex: JWTVerificationException) {
            throw JWTVerificationException("Token invalido, no autorizado")
        }
    }

    fun extractUsername(decodedJWT: DecodedJWT): String {
        return decodedJWT.subject.toString()
    }

    fun getSpecificClaim(decodedJWT: DecodedJWT, claimName: String): Claim {
        return decodedJWT.getClaim(claimName)
    }

    fun getIdFromTokenString(@NotNull request: HttpServletRequest): Long {
        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        val jwtToken = authHeader.substring(7)
        val decodedJWT = validateToken(jwtToken)
        return decodedJWT.getClaim("userID").asLong()
    }

    fun shouldTokenRefresh(token: String): Boolean {
        val claims = getAllClaims(token)
        val expiration = claims.expiration
        val timeLeft = expiration.time - System.currentTimeMillis()
        val refreshThresholdMs = 5 * 60 * 1000L // 5 minutos
        return timeLeft < refreshThresholdMs && timeLeft > 0
    }



}