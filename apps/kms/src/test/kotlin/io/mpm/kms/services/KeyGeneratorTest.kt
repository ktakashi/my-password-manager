package io.mpm.kms.services

import io.mpm.kms.entities.SecretKey
import io.mpm.kms.repositories.SecretKeyRepository
import org.assertj.core.api.Assertions.assertThat
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.capture
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.security.KeyPairGenerator
import java.security.Security
import java.util.UUID

class KeyGeneratorTest {

    companion object {
        @JvmStatic
        @BeforeAll
        fun setup() {
            Security.addProvider(BouncyCastleProvider())
        }
    }


    @ParameterizedTest
    @CsvSource(value = [
        "EC",
        "XDH"
        // "DH"
    ])
    fun generateAgreedSecretKey(algorithm: String) {
        val kpg = KeyPairGenerator.getInstance(algorithm)
        val kp = kpg.generateKeyPair()
        println(kp.public.javaClass)
        println(kp.public)
        val id = UUID.randomUUID()
        val entity = mock<SecretKey> {
            on { keyId } doReturn id
        }
        val argumentCaptor = argumentCaptor<SecretKey>()
        val repository = mock<SecretKeyRepository> {
            on { save(argumentCaptor.capture()) } doReturn entity
        }
        val generator = KeyGenerator(KeyAgreementService(), repository)
        val r = generator.generateAgreedSecretKey(publicKey = kp.public, 32)
        assertThat(r.keyId).isEqualTo(id)
    }
}
