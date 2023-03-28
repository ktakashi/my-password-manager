package io.mpm.kms.services

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.security.KeyPairGenerator

class KeyAgreementServiceTest {

    @Test
    fun calculateAgreement() {
        val service = KeyAgreementService()
        val kpg = KeyPairGenerator.getInstance("EC")
        val kp1 = kpg.genKeyPair()
        val kp2 = kpg.genKeyPair()
        val result = service.calculateAgreement(kp1.public, kp2.private, 32)
        assertThat(result).hasSize(32)
    }
}
