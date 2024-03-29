package io.mpm.idp.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "users") // avoiding reserved keyword
class User(@Column(name = "user_id", unique = true) val userId: String,
           @OneToOne var password: Password): BaseEntity()

@Entity
class Password(@Column(name = "current_password") var currentPassword: String,
               @OneToMany var histories: List<PasswordHistory> = listOf()): BaseEntity()

@Entity
class PasswordHistory(@Column(name = "old_value") // same reason
                      val value: String): BaseEntity()
