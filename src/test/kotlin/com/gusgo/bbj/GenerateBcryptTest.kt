package com.gusgo.bbj

import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class GenerateBcryptTest {
    @Test
    fun generatePasswordHash() {
        val passwordEncoder = BCryptPasswordEncoder()

        val rawPassword = "admin123"

        val passwordHash = passwordEncoder.encode(rawPassword)

        println("\n==================================================")
        println("SUA SENHA CRUA: $rawPassword")
        println("SEU HASH BCRYPT PARA O BANCO DE DADOS:")
        println(passwordHash)
        println("==================================================\n")
    }
}