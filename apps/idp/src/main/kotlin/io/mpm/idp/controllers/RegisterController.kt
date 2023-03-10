package io.mpm.idp.controllers

import io.mpm.idp.dtos.RegistrationRequest
import io.mpm.idp.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class RegisterController(private val userService: UserService) {
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun authenticate(@RequestBody registrationRequest: RegistrationRequest): Mono<Void> = Mono.fromRunnable {
        userService.create(registrationRequest.userId, registrationRequest.password)
    }
}
