package io.mpm.idp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication(exclude = [
    SecurityAutoConfiguration::class,
    ReactiveSecurityAutoConfiguration::class
])
@EnableJpaRepositories
@EnableTransactionManagement
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
