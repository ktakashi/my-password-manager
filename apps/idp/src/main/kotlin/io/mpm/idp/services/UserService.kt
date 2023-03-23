package io.mpm.idp.services

import io.mpm.idp.entities.Password
import io.mpm.idp.entities.PasswordHistory
import io.mpm.idp.entities.Pseudonym
import io.mpm.idp.exceptions.InvalidCredentialException
import io.mpm.idp.repositories.PasswordRepository
import io.mpm.idp.repositories.PseudonymRepository
import io.mpm.idp.repositories.UserRepository
import jakarta.transaction.Transactional
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsPasswordService
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(private val userRepository: UserRepository,
                  private val passwordRepository: PasswordRepository,
                  private val pseudonymRepository: PseudonymRepository,
                  private val passwordEncoder: PasswordEncoder): UserDetailsService, UserDetailsPasswordService {
    override fun loadUserByUsername(username: String): UserDetails = userRepository.findByUserId(username)?.let {
        User.withUsername(it.userId)
                .password(it.password.currentPassword)
                .build()
    } ?: throw UsernameNotFoundException("user not found")

    override fun updatePassword(user: UserDetails, newPassword: String): UserDetails = User.withUserDetails(user)
            .password(newPassword)
            .passwordEncoder { pass -> passwordEncoder.encode(pass) }
            .build().let {
                val u = userRepository.findByUserId(user.username)?: throw UsernameNotFoundException("user not found")
                val p = u.password
                p.histories += PasswordHistory(p.currentPassword)
                u.password = Password(it.password, p.histories)
                userRepository.save(u)
                it
            }

    fun authenticate(username: String, password: String) = userRepository.findByUserId(username)?.let { user ->
        if (passwordEncoder.matches(password, user.password.currentPassword)) {
            pseudonymRepository.findByUser(user) ?: Pseudonym(user, UUID.randomUUID()).also { pseudonym ->
                pseudonymRepository.save(pseudonym)
            }
        } else {
            throw InvalidCredentialException()
        }
    }?: throw InvalidCredentialException()

    @Transactional
    fun create(username: String, password: String): io.mpm.idp.entities.User =
            userRepository.save(io.mpm.idp.entities.User(username, passwordRepository.save(Password(passwordEncoder.encode(password)))))

    fun findByUsername(username: String): io.mpm.idp.entities.User? = userRepository.findByUserId(username)
}
