package io.mpm.kms.services

import io.mpm.kms.entities.KeyUsages
import io.mpm.kms.entities.SecretKey
import io.mpm.kms.repositories.SecretKeyRepository
import org.bouncycastle.jcajce.spec.XDHParameterSpec
import org.springframework.stereotype.Service
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
import java.security.spec.DSAParameterSpec
import java.security.spec.ECParameterSpec
import java.security.spec.NamedParameterSpec
import java.security.spec.RSAKeyGenParameterSpec
import java.util.UUID
import javax.crypto.KeyAgreement
import javax.crypto.interfaces.DHKey
import javax.crypto.spec.DHParameterSpec

private fun keyAgreementAlgorithm(key: Key): String = when (key) {
    is RSAKey, is DSAKey, is EdECKey -> throw IllegalArgumentException("Key agreement is not supported")
    is DHKey -> "DiffieHellman"
    is ECKey -> "ECDH"
    is XECKey -> "XDH"
    else -> throw IllegalArgumentException("Unknown key type")
}

private fun getDefaultKeyPairParameter(publicKey: PublicKey): AlgorithmParameterSpec = when (publicKey) {
    is RSAKey -> RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4)
    is ECKey -> ECParameterSpec(publicKey.params.curve, publicKey.params.generator, publicKey.params.order, publicKey.params.cofactor)
    is XECKey -> XDHParameterSpec(publicKey.params.let {
        when (it) {
            is NamedParameterSpec -> it.name
            else -> "X25519"
        }
    })
    is DHKey -> DHParameterSpec(publicKey.params.p, publicKey.params.g)
    is DSAKey -> DSAParameterSpec(publicKey.params.p, publicKey.params.q, publicKey.params.g)
    is EdECKey -> publicKey.params
    else -> throw java.lang.IllegalArgumentException("Unknown key type")
}
@Service
class KeyAgreementService {
    fun calculateAgreement(publicKey: PublicKey, privateKey: PrivateKey, size: Int): ByteArray = KeyAgreement.getInstance(keyAgreementAlgorithm(publicKey)).let { ka ->
        ka.init(privateKey)
        ka.doPhase(publicKey, true)
        ka.generateSecret().let {
            if (it.size == size)
                it
            else
                ByteArray(size).apply {
                    System.arraycopy(it, 0, this, 0, size)
                }
        }
    }
}

data class AgreedKey(val keyId: UUID, val publicKey: PublicKey)
@Service
class KeyGenerator(private val keyAgreementService: KeyAgreementService,
                   private val secretKeyRepository: SecretKeyRepository) {
    fun generateServerKeyPair(publicKey: PublicKey): KeyPair = KeyPairGenerator.getInstance(publicKey.algorithm).let { kpg ->
        kpg.initialize(getDefaultKeyPairParameter(publicKey))
        kpg.generateKeyPair()
    }

    fun generateAgreedSecretKey(publicKey: PublicKey, size: Int): AgreedKey = generateServerKeyPair(publicKey).let { kp ->
        val agreedSecretKey = keyAgreementService.calculateAgreement(publicKey, kp.private, size)
        // TODO KEK
        val entity = secretKeyRepository.save(SecretKey(agreedSecretKey, null, KeyUsages.CONTENT_ENCRYPTION))
        return AgreedKey(entity.keyId, kp.public)
    }
}
