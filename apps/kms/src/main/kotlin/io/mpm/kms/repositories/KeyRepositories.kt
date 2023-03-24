package io.mpm.kms.repositories

import io.mpm.kms.entities.DisposedKey
import io.mpm.kms.entities.Key
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface DisposedKeyRepository: CrudRepository<DisposedKey, Long> {
    fun findByKey(key: Key): DisposedKey?
}

@Repository
interface KeyRepository: CrudRepository<Key, Long> {
    @Query("select k from Key k left join DisposedKey dk on k = dk.key where k.id = :id and dk = null")
    fun findByKeyId(@Param("id") id: Long): Key?
}
