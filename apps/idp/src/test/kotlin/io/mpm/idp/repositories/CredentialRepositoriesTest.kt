package io.mpm.idp.repositories

import io.mpm.idp.entities.Password
import io.mpm.idp.entities.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class CredentialRepositoriesTest
@Autowired constructor(val entityManager: TestEntityManager,
                       val userRepository: UserRepository) {
    @Test
    fun `When findByUserId then return User`() {
        val pass = Password(currentPassword = "")
        entityManager.persist(pass)

        val me = User(userId = "ktakashi@ymail.com", password = pass)
        entityManager.persist(me)
        entityManager.flush()

        val found = userRepository.findByUserId("ktakashi@ymail.com")
        assertThat(found).isEqualTo(me)
        assertThat(found?.password).isEqualTo(pass)

        val notFound = userRepository.findByUserId("ktakashi19@gmail.com")
        assertThat(notFound).isNull()
    }
}
