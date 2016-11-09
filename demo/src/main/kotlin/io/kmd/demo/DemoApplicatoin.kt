package io.kmd.demo

import io.kmd.Html
import io.kmd.Parser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

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

    @GetMapping("/")
    fun home(model: ModelMap): ModelAndView = ModelAndView("index", update(model, DEFAULT_MARKDOWN))

    @GetMapping("/reset")
    fun reset(model: ModelMap): ModelAndView = ModelAndView("index", update(model, DEFAULT_MARKDOWN))

    @PostMapping("/view")
    fun view(model: ModelMap,
             @RequestParam markdown: String): ModelAndView = ModelAndView("index", update(model, markdown))

    private fun update(model: ModelMap, markdown: String): ModelMap {
        model.addAttribute("markdown", markdown)
        model.addAttribute("markup", html.render(parser.parse(markdown)))
        return model
    }
}

private val DEFAULT_MARKDOWN =
        """
        #### KMD (Markdown to HTML Converter)

        ##### Headings

        Heading are created by adding one or more # symbols before text.
        ```
        # The largest heading (h1 tag)
        ## The second largest heading (h2 tag)
        …
        ###### The 6th largest heading (h6 tag)
        ```

        ##### Styling text

        Texts can be **bold**, *italic* or ~~struck~~.
        ```
        *This text will be italic*. _this one too_

        **This text will be bold** and __this one too__

        ~~This ext will be struked~~
        ```

        ##### Blockquotes

        > Blockquotes are indicated with a >.
        >> They can be embedded.

        ```
        > A quoted text
        >> An embedded (second level) quoted text
        ```

        ##### Links

        Following forms of links are supported:
        ```
        [normal link](www.google.com)

        [](#ancher)

        ![image](https://www.example/image)
        ```

        ##### Lists

        Unordered lists are indicated by *.
        ```
        * Red
        * White
        * Green
        ```

        Ordered lists are indiicated by !.
        ```
        ! Red
        ! White
        ! Green
        ```

        ##### Code Formatting

        Single backticks are used to `format` text in a special monospace format.
        ```
           `This` is an inline format
        ```

        Triple backticks are used to format text as its own distinct block.
        ```
          val x = 10
          val y = 20
          val z = x + y
        ```
        """.trimIndent()