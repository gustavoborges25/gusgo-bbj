package com.gusgo.bbj.security

import com.gusgo.bbj.domains.registration.User
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class TokenService(private val jwtEncoder: JwtEncoder) {

    fun generateToken(user: User): String {
        val now = Instant.now()
        val expiresIn = 2L // Tempo de validade em horas

        val claims = JwtClaimsSet.builder()
            .issuer("os_jiujitsu_api")
            .subject(user.id.toString())
            .issuedAt(now)
            .expiresAt(now.plus(expiresIn, ChronoUnit.HOURS))
            .claim("academyId", user.academy.id.toString())
            .claim("name", user.name)
            .claim("role", user.role.name) // OWNER, INSTRUCTOR, STUDENT
            .build()

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }
}