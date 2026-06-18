package com.gusgo.bbj.security

import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Component
class SecurityContextService {

    /**
     * Recupera o objeto JWT bruto que está no contexto de segurança da requisição atual.
     */
    private fun getPrincipal(): Jwt {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication is JwtAuthenticationToken) {
            return authentication.principal as Jwt
        }

        throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated or token is invalid.")
    }

    /**
     * Extrai o ID do Usuário Logado (guardado no campo 'sub' do JWT).
     */
    fun getCurrentUserId(): UUID {
        val jwt = getPrincipal()
        return UUID.fromString(jwt.subject)
    }

    /**
     * Extrai o Academy ID embutido no Token (Multitenancy Seguro).
     */
    fun getCurrentAcademyId(): UUID {
        val jwt = getPrincipal()
        // Recupera o claim customizado que adicionamos na emissão do token
        val academyIdStr = jwt.getClaim<String>("academyId")
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "Academy context missing in authentication token.")

        return UUID.fromString(academyIdStr)
    }

    /**
     * Extrai o Nome do usuário de dentro do token.
     */
    fun getCurrentUserName(): String {
        val jwt = getPrincipal()
        return jwt.getClaim<String>("name") ?: "Unknown"
    }

    /**
     * Extrai a Role (Perfil) do usuário logado.
     */
    fun getCurrentUserRole(): String {
        val jwt = getPrincipal()
        return jwt.getClaim<String>("role") ?: ""
    }
}