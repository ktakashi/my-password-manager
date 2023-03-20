package io.mpm.idp.controllers

import io.mpm.idp.dtos.AuthenticationRequest
import io.mpm.idp.dtos.AuthenticationResponse
import io.mpm.idp.services.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class AuthenticateController(private val userService: UserService) {
    @PostMapping("/authenticate/password")
    fun authenticate(@RequestBody authenticateRequest: AuthenticationRequest): Mono<AuthenticationResponse> = Mono.fromCallable {
        AuthenticationResponse(userService.authenticate(authenticateRequest.userId, authenticateRequest.password).pseudonym)
    }
}
