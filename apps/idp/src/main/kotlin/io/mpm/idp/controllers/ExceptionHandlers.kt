package io.mpm.idp.controllers

import io.mpm.idp.dtos.ErrorResponse
import io.mpm.idp.dtos.invalidCredentialResponse
import io.mpm.idp.exceptions.InvalidCredentialException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class InvalidCredentialExceptionHandler {
    @ExceptionHandler(InvalidCredentialException::class)
    fun handleException(e: InvalidCredentialException): ResponseEntity<ErrorResponse> =
            ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(invalidCredentialResponse())
}
