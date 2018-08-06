package io.kmd.demo

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class DemoApplicationTest(@Autowired val client: WebTestClient) {

    @Test
    fun `context - load`() {
    }

    @Test
    fun `GET - should return ok without body`() {
        client.get()
                .exchange()
                .expectStatus().isOk
                .expectBody().isEmpty
    }

    @Test
    fun `POST - should return ok with converted markdown`() {
        client.post()
                .contentType(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromObject("##### header"))
                .exchange()
                .expectStatus().isOk
                .expectBody(String::class.java)
                .isEqualTo<Nothing>("<h5>header</h5>")
    }
}