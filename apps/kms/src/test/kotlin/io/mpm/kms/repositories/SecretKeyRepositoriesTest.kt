package io.mpm.kms.repositories

import io.mpm.kms.entities.DisposedKey
import io.mpm.kms.entities.SecretKey
import io.mpm.kms.entities.KeyUsages
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class SecretKeyRepositoriesTest
@Autowired constructor(private val entityManager: TestEntityManager,
                       private val secretKeyRepository: SecretKeyRepository,
                       private val disposedKeyRepository: DisposedKeyRepository) {
    @Test
    fun test() {
        val masterKey = entityManager.persist(SecretKey(usage = KeyUsages.KEY_ENCRYPTION))
        val kek = entityManager.persist(SecretKey(byteArrayOf(0x1), masterKey, KeyUsages.KEY_ENCRYPTION))
        val cek = entityManager.persist(SecretKey(byteArrayOf(0x2), kek, KeyUsages.CONTENT_ENCRYPTION))
        val cek2 = entityManager.persist(SecretKey(byteArrayOf(0x2), kek, KeyUsages.CONTENT_ENCRYPTION))
        entityManager.flush()
        val cekId = cek.id!!

        val r0 = secretKeyRepository.findById(masterKey.id!!)
        assertThat(r0.get()).isEqualTo(masterKey)
        assertThat(r0.get().id).isNotNull
        assertThat(r0.get().encryptionKey).isNull()
        assertThat(r0.get().keyUsage).isEqualTo(KeyUsages.KEY_ENCRYPTION)

        val r1 = secretKeyRepository.findById(kek.id!!)
        assertThat(r1.get()).isEqualTo(kek)
        assertThat(r1.get().id).isNotNull
        assertThat(r1.get().encryptionKey).isEqualTo(masterKey)

        val r2 = secretKeyRepository.findById(cekId)
        assertThat(r2.get()).isEqualTo(cek)
        assertThat(r2.get().id).isNotNull
        assertThat(r2.get().encryptionKey).isEqualTo(kek)

        val r4 = secretKeyRepository.findByKeyId(cek2.keyId)
        assertThat(r4).isNotEqualTo(r2.get())

        val modifiedAt = cek.modifiedAt
        cek.value = byteArrayOf(0x03)

        entityManager.persist(DisposedKey(cek))
        entityManager.flush()

        assertThat(secretKeyRepository.findByKeyId(cek.keyId)).isNull()
        val r3 = secretKeyRepository.findById(cekId)
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
