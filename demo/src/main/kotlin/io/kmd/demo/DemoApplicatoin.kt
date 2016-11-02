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

private val DEFAULT_MARKDOWN =
        """
        # KMD (Markdown to HTML Converter)

        # Headings

        You can create a heading by adding one or more # symbols before your heading text.
        ```
        # The largest heading (a h1 tag)
        ## The second largest heading (a h2 tag)
        …
        ###### The 6th largest heading (a h6 tag)
        ```

        # Styling text

        You can make text **bold**, *italic* or ~~struck~~.

        ```
        *This text will be italic*. _this one too_

        **This text will be bold** and __this one too__

        ~~This ext will be struked~~
        ```

        # Blockquotes

        > You can indicate blockquotes with a >.

        ```
        Example:
        > A quoted text
        >>
        ```

        # Links

        Following forms of links are supported:

        ```

        [normal link](www.google.com)

        [](#ancher)

        ![image](https://www.gstatic.com/webp/gallery3/2.png)
        ```

        # Lists
        ## Unordered
        ```
        * Red
        * White
        * Green
        ```
        ## Ordered
        ```
        ! Red
        ! White
        ! Green
        ```

        # Code Formating
        ## Inline

        Use single backticks to `format` text in a special monospace format.

        ```
           `This` is an inline format
        ```

        ## Multiple Lines

        You can also use triple backticks to format text as its own distinct block.

        ```
          val x = 10
          val y = 20
          val z = x + y
        ```
        """.trimIndent()