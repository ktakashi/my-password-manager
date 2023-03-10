package io.mpm.idp.entiries

import jakarta.persistence.Entity
import jakarta.persistence.OneToOne
import java.util.UUID

@Entity
class Pseudonym(@OneToOne var user: User,
                var pseudonym: UUID): BaseEntity()
