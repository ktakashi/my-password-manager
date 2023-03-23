package io.mpm.kms.entities

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorType
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.OneToOne

@Entity
open class DisposedKey(@OneToOne val key: Key): BaseEntity()

@Entity(name = "key_table")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "keys", discriminatorType = DiscriminatorType.INTEGER)
open class Key(@Column(name = "key_value") val value: ByteArray? = null,
               @Column(name = "processed_bytes") var processedBytes: Long = 0L,
               @OneToOne var encryptionKey: Key? = null)
    : ModifiableEntity()

sealed interface Kek<K: Key> {
    val key: K
}

@Entity
@DiscriminatorValue("0")
class MasterKey: Key(), Kek<MasterKey> {
    override val key: MasterKey
        get() = this
}

@Entity
@DiscriminatorValue("1")
class KeyEncryptionKey(value: ByteArray, kek: Kek<*>): Key(value, encryptionKey = kek.key), Kek<KeyEncryptionKey> {
    override val key: KeyEncryptionKey
        get() = this
}

@Entity
@DiscriminatorValue("2")
class ContentEncryptionKey(value: ByteArray, kek: KeyEncryptionKey): Key(value, encryptionKey = kek)
