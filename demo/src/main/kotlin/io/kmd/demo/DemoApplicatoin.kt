package io.kmd.demo

import io.kmd.Html
import io.kmd.Parser
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.View
import org.springframework.web.servlet.view.RedirectView
import javax.servlet.http.HttpSession

@SpringBootApplication
class DemoApplication {

    @Bean
    fun parser(): Parser = Parser()

    @Bean
    fun html(): Html = Html()
}

fun main(args: Array<String>) {
    SpringApplication.run(DemoApplication::class.java, *args)
}

@Controller
class MainController(private val parser: Parser,
                     private val html: Html) {

    @GetMapping("/")
    fun home(session: HttpSession): ModelAndView = modelAndView(session)

    @GetMapping("/reset")
    fun reset(session: HttpSession): View = updateAndRedirect(session, DEFAULT_MARKDOWN)

    @PostMapping("/view")
    fun view(session: HttpSession, @RequestParam markdown: String): View = updateAndRedirect(session, markdown)

    private fun updateAndRedirect(session: HttpSession, markdown: String): RedirectView {
        session.setAttribute("markdown", markdown)

        return RedirectView("/")
    }

    private fun modelAndView(session: HttpSession): ModelAndView {
        val markdown = session.getAttribute("markdown") as String? ?: DEFAULT_MARKDOWN

        return ModelAndView("index",
                ModelMap()
                        .addAttribute("markdown", markdown)
                        .addAttribute("markup", html.render(parser.parse(markdown))))
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

        ```
            Last Update: 18 July
        ```
        """.trimIndent()