package io.mpm.idp.dtos

import java.time.OffsetDateTime
import java.time.ZoneId

enum class ErrorType {
    INVALID_CREDENTIAL
}

class ErrorResponse(val type: ErrorType, val timestamp: OffsetDateTime = OffsetDateTime.now(ZoneId.of("UTC")))
fun invalidCredentialResponse() = ErrorResponse(ErrorType.INVALID_CREDENTIAL)
