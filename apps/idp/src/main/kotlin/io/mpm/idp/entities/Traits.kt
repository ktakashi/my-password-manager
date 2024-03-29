package io.mpm.idp.entities

import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.OffsetDateTime

@MappedSuperclass
open class BaseEntity(@Id @GeneratedValue val id: Long? = null,
                      @Column(name = "created_at") @CreationTimestamp
                      val createdAt: OffsetDateTime = OffsetDateTime.now(),
                      @Column(name = "modified_at") @UpdateTimestamp
                      var modifiedAt: OffsetDateTime = OffsetDateTime.now())
