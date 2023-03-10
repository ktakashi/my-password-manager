package io.mpm.idp.services

import io.mpm.idp.entiries.Password
import io.mpm.idp.entiries.User
import io.mpm.idp.repositories.PasswordRepository
import io.mpm.idp.repositories.PseudonymRepository
import io.mpm.idp.repositories.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.security.crypto.factory.PasswordEncoderFactories

@DataJpaTest
class UserServiceTest
@Autowired constructor(val entityManager: TestEntityManager,
                       userRepository: UserRepository,
                       passwordRepository: PasswordRepository,
                       pseudonymRepository: PseudonymRepository) {
    private val passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
    private val userService = UserService(userRepository, passwordRepository, pseudonymRepository, passwordEncoder)

    @Test
    fun `When username and password matches`() {
        val pass = Password(currentPassword = passwordEncoder.encode("password"))
        entityManager.persist(pass)

        val me = User(userId = "ktakashi@ymail.com", password = pass)
        entityManager.persist(me)
        entityManager.flush()

        val pseudonym = userService.authenticate("ktakashi@ymail.com", "password")
        assertThat(pseudonym.user).isEqualTo(me)
    }
}
