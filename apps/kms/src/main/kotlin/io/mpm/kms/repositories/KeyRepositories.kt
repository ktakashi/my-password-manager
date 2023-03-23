package io.mpm.kms.repositories

import io.mpm.kms.entities.ContentEncryptionKey
import io.mpm.kms.entities.DisposedKey
import io.mpm.kms.entities.Key
import io.mpm.kms.entities.KeyEncryptionKey
import io.mpm.kms.entities.MasterKey
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.stereotype.Repository

@Repository
interface DisposedKeyRepository: CrudRepository<DisposedKey, Long>

@NoRepositoryBean
interface KeyRepository<K: Key>: CrudRepository<K, Long>

@Repository
interface MasterKeyRepository: KeyRepository<MasterKey>

@Repository
interface KeyEncryptionKeyRepository: KeyRepository<KeyEncryptionKey>

@Repository
interface ContentEncryptionKeyRepository: KeyRepository<ContentEncryptionKey>
