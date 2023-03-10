package io.mpm.idp

import io.mpm.idp.dtos.AuthenticationRequest
import io.mpm.idp.dtos.RegistrationRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters

@SpringBootTest
@AutoConfigureWebTestClient
class ApplicationTest  {

    @Test
    fun `When authenticate then get pseudonym`(@Autowired webClient: WebTestClient) {
        webClient.post().uri("/register")
                .body(BodyInserters.fromValue(RegistrationRequest("ktakashi@ymail.com", "password")))
                .exchange()
                .expectStatus().is2xxSuccessful
                .expectBody().isEmpty

        webClient.post().uri("/authenticate")
                .body(BodyInserters.fromValue(AuthenticationRequest("ktakashi@ymail.com", "password")))
                .exchange()
                .expectStatus().is2xxSuccessful
                .expectBody()
                .jsonPath("$.pseudonym").exists()

        webClient.post().uri("/authenticate")
                .body(BodyInserters.fromValue(AuthenticationRequest("ktakashi@ymail.com", "invalid password")))
                .exchange()
                .expectStatus().is5xxServerError
    }
}
