package io.mpm.kms.repositories

import io.mpm.kms.entities.ContentEncryptionKey
import io.mpm.kms.entities.KeyEncryptionKey
import io.mpm.kms.entities.MasterKey
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class KeyRepositoriesTest
@Autowired constructor(private val entityManager: TestEntityManager,
                       private val masterKeyRepository: MasterKeyRepository,
                       private val kekRepository: KeyEncryptionKeyRepository,
                       private val cekRepository: ContentEncryptionKeyRepository) {
    @Test
    fun test() {
        val masterKey = entityManager.persist(MasterKey())
        val kek = entityManager.persist(KeyEncryptionKey(byteArrayOf(0x1), masterKey))
        val cek = entityManager.persist(ContentEncryptionKey(byteArrayOf(0x2), kek))

        val r0 = masterKeyRepository.findById(masterKey.id!!)
        assertThat(r0.get()).isEqualTo(masterKey)
        assertThat(r0.get().id).isNotNull
        assertThat(r0.get().encryptionKey).isNull()

        val r1 = kekRepository.findById(kek.id!!)
        assertThat(r1.get()).isEqualTo(kek)
        assertThat(r1.get().id).isNotNull
        assertThat(r1.get().encryptionKey).isEqualTo(masterKey)

        val r2 = cekRepository.findById(cek.id!!)
        assertThat(r2.get()).isEqualTo(cek)
        assertThat(r2.get().id).isNotNull
        assertThat(r2.get().encryptionKey).isEqualTo(kek)
    }
}
