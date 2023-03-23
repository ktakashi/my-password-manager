package io.mpm.idp.entities

import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import java.time.OffsetDateTime

@MappedSuperclass
open class BaseEntity(@Id @GeneratedValue var id: Long? = null,
                      @Column(name = "created_at")  var createdAt: OffsetDateTime = OffsetDateTime.now(),
                      @Column(name = "modified_at") var modifiedAt: OffsetDateTime = OffsetDateTime.now())
