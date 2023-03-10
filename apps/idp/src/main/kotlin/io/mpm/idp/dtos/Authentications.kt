package io.mpm.idp.dtos

import java.util.UUID

data class AuthenticationRequest(val userId: String, val password: String)
data class AuthenticationResponse(val pseudonym: UUID)
