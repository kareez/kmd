package io.kmd.demo

import io.kmd.Html
import io.kmd.Parser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

@Configuration
@SpringBootApplication
open class DemoApplication {

    @Bean
    open fun parser(): Parser = Parser()

    @Bean
    open fun html(): Html = Html()
}

fun main(args: Array<String>) {
    SpringApplication.run(DemoApplication::class.java, *args)
}


@Controller
class MainController
@Autowired constructor(private val parser: Parser,
                       private val html: Html) {

    companion object {
        private val DEFAULT_MARKDOWN =
                """
                #### This is the default one
                > Try it yourself
                """.trimIndent()
    }

    @GetMapping("/")
    fun home(model: ModelMap): ModelAndView = ModelAndView("index", update(model, DEFAULT_MARKDOWN))

    @GetMapping("/reset")
    fun reset(model: ModelMap): ModelAndView = ModelAndView("index", update(model, DEFAULT_MARKDOWN))

    @PostMapping("/view")
    fun view(model: ModelMap,
             @RequestParam markdown: String): ModelAndView = ModelAndView("index", update(model, markdown))

    private fun update(model: ModelMap, markdown: String): ModelMap {
        model.addAttribute("markup", html.render(parser.parse(markdown)))
        model.addAttribute("markdown", markdown)
        return model
    }
}