package io.kmd.demo

import io.kmd.Html
import io.kmd.Parser
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurerComposite
import org.springframework.web.reactive.config.WebFluxConfigurer


@SpringBootApplication
class DemoApplication {

    @Bean
    fun routes(handler: Handler) = router {
        GET("/", handler::health)
        POST("/", handler::convert)
    }

    @Bean
    fun handler() = Handler(Parser(), Html())

    @Bean
    fun corsConfigurer(): WebFluxConfigurer =
            object : WebFluxConfigurerComposite() {

                override fun addCorsMappings(registry: CorsRegistry) =
                        registry.addMapping("/**")
                                .allowedOrigins("*")
                                .done()
            }

    fun Any.done() = Unit
}

class Handler(
        private val parser: Parser,
        private val html: Html) {

    fun health(request: ServerRequest): Mono<ServerResponse> = ServerResponse.ok().build()

    fun convert(request: ServerRequest): Mono<ServerResponse> =
            request.bodyToMono<String>().flatMap { markdown ->
                ok().body(BodyInserters.fromObject(html.render(parser.parse(markdown))))
            }
}

fun main(args: Array<String>) {
    SpringApplication.run(DemoApplication::class.java, *args)
}
