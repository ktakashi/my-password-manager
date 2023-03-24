package io.mpm.kms.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

sealed interface KeyUsage {
    /**
     * Combine other key usage
     */
    fun and(other: KeyUsage): KeyUsage
    fun hasUsage(usage: KeyUsages): Boolean = (bits and usage.bits) != 0u
    val bits: UInt

    companion object {
        @JvmStatic
        fun fromUsageBits(bits: UInt): KeyUsage = if (bits.countOneBits() == 1) {
            KeyUsages.fromUsageBit(bits)
        } else {
            CombinedKeyUsage(bits)
        }
    }
}

data class CombinedKeyUsage(override val bits: UInt): KeyUsage {
    override fun and(other: KeyUsage): KeyUsage = CombinedKeyUsage(bits or other.bits)
}
enum class KeyUsages(override val bits: UInt): KeyUsage {
    KEY_ENCRYPTION(0x0000_0001u),
    CONTENT_ENCRYPTION(0x0000_0002u);

    override fun and(other: KeyUsage): KeyUsage = CombinedKeyUsage(bits or other.bits)

    companion object {
        @JvmStatic
        fun fromUsageBit(bits: UInt): KeyUsage = values().find { it.bits == bits }?: throw IllegalArgumentException("No key usage for $bits bit")
    }
}

@Entity
data class DisposedKey(@OneToOne val key: Key): BaseEntity()

@Entity
@Table(name = "keys")
class Key(@Column(name = "key_value") var value: ByteArray?,
          @Column(name = "processed_bytes") var processedBytes: Long,
          @OneToOne var encryptionKey: Key?,
          // Default content encryption
          private val usage: UInt = KeyUsages.CONTENT_ENCRYPTION.bits) : ModifiableEntity() {
    constructor(value: ByteArray? = null,
                encryptionKey: Key? = null,
                usage: KeyUsage = KeyUsages.CONTENT_ENCRYPTION): this(value, 0L, encryptionKey, usage.bits)
    val keyUsage: KeyUsage
        get() = KeyUsage.fromUsageBits(usage)
}

