package io.mpm.idp

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.mpm.idp.dtos.AuthenticationRequest
import io.mpm.idp.dtos.AuthenticationResponse
import io.mpm.idp.dtos.ErrorType
import io.mpm.idp.dtos.RegistrationRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import java.time.OffsetDateTime

@SpringBootTest
@AutoConfigureWebTestClient
class ApplicationTest  {
    private val objectMapper = ObjectMapper().registerModule(KotlinModule())
    @Test
    fun `When authenticate then get pseudonym`(@Autowired webClient: WebTestClient) {
        webClient.post().uri("/register")
                .body(BodyInserters.fromValue(RegistrationRequest("ktakashi@ymail.com", "password")))
                .exchange()
                .expectStatus().is2xxSuccessful
                .expectBody().isEmpty

        val pseudonym0 = objectMapper.readValue(
                webClient.post().uri("/authenticate/password")
                        .body(BodyInserters.fromValue(AuthenticationRequest("ktakashi@ymail.com", "password")))
                        .exchange()
                        .expectStatus().is2xxSuccessful
                        .expectBody()
                        .jsonPath("$.pseudonym").exists()
                        .returnResult()
                        .responseBody, AuthenticationResponse::class.java)
                .pseudonym

        webClient.post().uri("/authenticate/password")
                .body(BodyInserters.fromValue(AuthenticationRequest("ktakashi@ymail.com", "invalid password")))
                .exchange()
                .expectStatus().is4xxClientError
                .expectBody()
                .jsonPath("$.type").value<String> { v -> assertThat(v).isEqualTo(ErrorType.INVALID_CREDENTIAL.name) }
                .jsonPath("$.timestamp").value<String> { v -> OffsetDateTime.parse(v) }

        val pseudonym1 = objectMapper.readValue(
                webClient.post().uri("/authenticate/password")
                        .body(BodyInserters.fromValue(AuthenticationRequest("ktakashi@ymail.com", "password")))
                        .exchange()
                        .expectStatus().is2xxSuccessful
                        .expectBody()
                        .jsonPath("$.pseudonym").exists()
                        .returnResult()
                        .responseBody, AuthenticationResponse::class.java)
                .pseudonym
        assertThat(pseudonym1).isEqualTo(pseudonym0)
    }
}
