package io.mpm.kms.repositories

import io.mpm.kms.entities.DisposedKey
import io.mpm.kms.entities.SecretKey
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DisposedKeyRepository: CrudRepository<DisposedKey, Long> {
    fun findByKey(key: SecretKey): DisposedKey?
}

@Repository
interface SecretKeyRepository: CrudRepository<SecretKey, Long> {
    @Query("select k from SecretKey k left join DisposedKey dk on k = dk.key where k.keyId = :id and dk = null")
    fun findByKeyId(@Param("id") id: UUID): SecretKey?
}
