package io.mpm.idp.repositories

import io.mpm.idp.entities.Password
import io.mpm.idp.entities.Pseudonym
import io.mpm.idp.entities.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository: CrudRepository<User, Long> {
    fun findByUserId(userId: String): User?
}

@Repository
interface PasswordRepository: CrudRepository<Password, Long>

@Repository
interface PseudonymRepository: CrudRepository<Pseudonym, Long> {
    fun findByPseudonym(pseudonym: UUID): Pseudonym
    fun findByUser(user: User): Pseudonym?
}
