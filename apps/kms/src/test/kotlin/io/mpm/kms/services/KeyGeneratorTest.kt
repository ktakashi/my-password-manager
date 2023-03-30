package io.mpm.kms.services

import io.mpm.kms.entities.SecretKey
import io.mpm.kms.repositories.SecretKeyRepository
import org.assertj.core.api.Assertions.assertThat
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.security.KeyPair
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
        "XDH",
        "DH"
    ])
    fun `When using right algorithm, then got proper result`(algorithm: String) {
        val (kp, id, generator) = setupKeyGenerator(algorithm)
        val r = generator.generateAgreedSecretKey(publicKey = kp.public, 32)
        assertThat(r.keyId).isEqualTo(id)
    }

    @ParameterizedTest
    @CsvSource(value = [
        "RSA",
        "DSA",
        "EdDSA"
    ])
    fun `When using wrong algorithm, then got error`(algorithm: String) {
        val (kp, _, generator) = setupKeyGenerator(algorithm)
        assertThrows<IllegalArgumentException> { generator.generateAgreedSecretKey(publicKey = kp.public, 32) }
    }

    private fun setupKeyGenerator(algorithm: String): Triple<KeyPair, UUID, KeyGenerator> {
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
        return Triple(kp, id, generator)
    }

}
