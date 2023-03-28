package io.mpm.kms.services

import io.mpm.kms.entities.KeyUsages
import io.mpm.kms.entities.SecretKey
import io.mpm.kms.repositories.SecretKeyRepository
import org.springframework.stereotype.Service
import java.security.AlgorithmParameterGenerator
import java.security.Key
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.DSAKey
import java.security.interfaces.ECKey
import java.security.interfaces.EdECKey
import java.security.interfaces.RSAKey
import java.security.interfaces.XECKey
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.DSAGenParameterSpec
import java.security.spec.ECGenParameterSpec
import java.security.spec.EdDSAParameterSpec
import java.security.spec.RSAKeyGenParameterSpec
import java.util.UUID
import javax.crypto.KeyAgreement
import javax.crypto.interfaces.DHKey
import javax.crypto.spec.DHGenParameterSpec

private fun algorithm(key: Key): String = when (key) {
    is RSAKey -> "RSA"
    is DSAKey -> "DSA"
    is DHKey -> "DH"
    is ECKey, is XECKey -> "ECDH"
    is EdECKey -> "EdDSA"
    else -> throw IllegalArgumentException("Unknown key type")
}

private fun getDefaultKeyPairParameter(algorithm: String): AlgorithmParameterSpec = AlgorithmParameterGenerator.getInstance(algorithm).let {
    it.generateParameters().getParameterSpec(when (algorithm) {
        "RSA" -> RSAKeyGenParameterSpec::class.java
        "EC", "ECDH" -> ECGenParameterSpec::class.java
        "DH"-> DHGenParameterSpec::class.java
        "DSA" -> DSAGenParameterSpec::class.java
        "EdDSA" -> EdDSAParameterSpec::class.java
        else -> throw java.lang.IllegalArgumentException("Unknown key type")
    })
}
@Service
class KeyAgreementService {
    fun calculateAgreement(publicKey: PublicKey, privateKey: PrivateKey, size: Int): ByteArray = KeyAgreement.getInstance(algorithm(publicKey)).let { ka ->
        ka.init(privateKey)
        ka.doPhase(publicKey, true)
        ByteArray(size).apply {
            ka.generateSecret(this, 0)
        }
    }
}

data class AgreedKey(val keyId: UUID, val publicKey: PublicKey)
@Service
class KeyGenerator(private val keyAgreementService: KeyAgreementService,
                   private val secretKeyRepository: SecretKeyRepository) {
    fun generateServerKeyPair(publicKey: PublicKey): KeyPair = generateServerKeyPair(algorithm(publicKey))
    fun generateServerKeyPair(algorithm: String): KeyPair = KeyPairGenerator.getInstance(algorithm).let { kpg ->
        kpg.initialize(getDefaultKeyPairParameter(algorithm))
        kpg.generateKeyPair()
    }

    fun generateAgreedSecretKey(publicKey: PublicKey, size: Int): AgreedKey = generateServerKeyPair(publicKey).let { kp ->
        val agreedSecretKey = keyAgreementService.calculateAgreement(publicKey, kp.private, size)
        // TODO KEK
        val entity = secretKeyRepository.save(SecretKey(agreedSecretKey, null, KeyUsages.CONTENT_ENCRYPTION))
        return AgreedKey(entity.keyId, kp.public)
    }
}
