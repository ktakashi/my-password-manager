package io.mpm.idp.dtos

import java.time.OffsetDateTime
import java.time.ZoneId

enum class ErrorType {
    USER_ALREADY_EXISTS,
    INVALID_CREDENTIAL
}

class ErrorResponse(val type: ErrorType, val timestamp: OffsetDateTime = OffsetDateTime.now(ZoneId.of("UTC"))): Response
fun invalidCredentialResponse() = ErrorResponse(ErrorType.INVALID_CREDENTIAL)
fun userAlreadyExistsResponse() = ErrorResponse(ErrorType.USER_ALREADY_EXISTS)
