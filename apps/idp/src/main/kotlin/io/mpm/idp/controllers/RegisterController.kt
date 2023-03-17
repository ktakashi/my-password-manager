package io.mpm.idp.controllers

import io.mpm.idp.dtos.ErrorResponse
import io.mpm.idp.dtos.RegistrationRequest
import io.mpm.idp.dtos.Response
import io.mpm.idp.dtos.userAlreadyExistsResponse
import io.mpm.idp.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.net.URI

@RestController
class RegisterController(private val userService: UserService) {
    @PostMapping("/register")
    fun authenticate(@RequestBody registrationRequest: RegistrationRequest): Mono<ResponseEntity<Response>> = Mono.fromCallable {
        userService.findByUsername(registrationRequest.userId)
    }.publishOn(Schedulers.boundedElastic())
            .map { ResponseEntity.badRequest().body<Response>(userAlreadyExistsResponse()) }
            .switchIfEmpty(Mono.defer {
                Mono.fromCallable {
                    userService.create(registrationRequest.userId, registrationRequest.password)
                }.publishOn(Schedulers.boundedElastic())
                        .map { ResponseEntity.created(URI.create("/")).build() }
            })

}
