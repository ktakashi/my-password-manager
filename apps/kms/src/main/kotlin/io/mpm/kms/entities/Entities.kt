package io.mpm.kms.entities

import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.OffsetDateTime
import java.time.ZoneId

@MappedSuperclass
open class BaseEntity(@Id @GeneratedValue val id: Long? = null,
                      @Column(name = "created_at") @CreationTimestamp
                      var createdAt: OffsetDateTime = OffsetDateTime.now(ZoneId.of("UTC")))

@MappedSuperclass
open class ModifiableEntity(@Column(name = "modified_at") @UpdateTimestamp
                            var modifiedAt: OffsetDateTime = OffsetDateTime.now(ZoneId.of("UTC"))): BaseEntity()
