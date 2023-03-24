package io.mpm.kms.repositories

import io.mpm.kms.entities.DisposedKey
import io.mpm.kms.entities.Key
import io.mpm.kms.entities.KeyUsages
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class KeyRepositoriesTest
@Autowired constructor(private val entityManager: TestEntityManager,
                       private val keyRepository: KeyRepository,
                       private val disposedKeyRepository: DisposedKeyRepository) {
    @Test
    fun test() {
        val masterKey = entityManager.persist(Key(usage = KeyUsages.KEY_ENCRYPTION))
        val kek = entityManager.persist(Key(byteArrayOf(0x1), masterKey, KeyUsages.KEY_ENCRYPTION))
        val cek = entityManager.persist(Key(byteArrayOf(0x2), kek, KeyUsages.CONTENT_ENCRYPTION))
        entityManager.flush()
        val cekId = cek.id!!

        val r0 = keyRepository.findById(masterKey.id!!)
        assertThat(r0.get()).isEqualTo(masterKey)
        assertThat(r0.get().id).isNotNull
        assertThat(r0.get().encryptionKey).isNull()
        assertThat(r0.get().keyUsage).isEqualTo(KeyUsages.KEY_ENCRYPTION)

        val r1 = keyRepository.findById(kek.id!!)
        assertThat(r1.get()).isEqualTo(kek)
        assertThat(r1.get().id).isNotNull
        assertThat(r1.get().encryptionKey).isEqualTo(masterKey)

        val r2 = keyRepository.findById(cekId)
        assertThat(r2.get()).isEqualTo(cek)
        assertThat(r2.get().id).isNotNull
        assertThat(r2.get().encryptionKey).isEqualTo(kek)

        val modifiedAt = cek.modifiedAt
        cek.value = byteArrayOf(0x03)

        entityManager.persist(DisposedKey(cek))
        entityManager.flush()

        assertThat(keyRepository.findByKeyId(cek.keyId)).isNull()
        val r3 = keyRepository.findById(cekId)
        assertThat(r3.isPresent)
        assertThat(modifiedAt).isNotEqualTo(cek.modifiedAt)

        val dk = disposedKeyRepository.findByKey(cek)
        assertThat(dk).isNotNull
        assertThat(dk!!.key).isEqualTo(cek)

        assertThrows<Exception> {
            disposedKeyRepository.save(DisposedKey(cek))
            entityManager.flush()
        }
    }
}
