package io.mpm.kms.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.util.UUID

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
data class DisposedKey(@OneToOne(optional = false) val key: SecretKey): BaseEntity()

@Entity
class SecretKey(@Column(unique = true) val keyId: UUID = UUID.randomUUID(),
                @Column(name = "key_value") var value: ByteArray?,
                @OneToOne var encryptionKey: SecretKey?,
                // Default content encryption
                private val usage: UInt = KeyUsages.CONTENT_ENCRYPTION.bits) : ModifiableEntity() {
    constructor(value: ByteArray? = null,
                encryptionKey: SecretKey? = null,
                usage: KeyUsage = KeyUsages.CONTENT_ENCRYPTION)
            : this(value = value, encryptionKey = encryptionKey, usage = usage.bits)
    val keyUsage: KeyUsage
        get() = KeyUsage.fromUsageBits(usage)
}

@Entity
class MasterKey(@OneToOne(optional = false) val key: SecretKey,
                @Column(unique = true) val specific: String) : BaseEntity()

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["key_id", "algorithm"])])
class SecretKeyUsage(@JoinColumn(name = "key_id", nullable = false) @ManyToOne val key: SecretKey,
                     @Column(nullable = false) val algorithm: String,
                     @Column(name = "processed_bytes") var processedBytes: Long = 0L): ModifiableEntity()
