package io.kmd

import org.junit.Test
import kotlin.test.assertEquals

class IntegrationTest {
    private val parser = Parser()
    private val html = Html()

    @Test
    fun `render a sample markdown`() {
        val md =
                """
                # The Title
                ## The Title
                ### The Title
                #### The Title
                ##### The Title
                ###### The Title
                this is a normal paragraph with __foo__, **bar** and ~~baz~~.

                check [this](www.google.com) and [](#this) and ![this](foo/image) for more information

                > this is a quote
                > in two lines
                >> this one is embedded

                * blue
                * red `color`
                * green

                ! blue
                ! red `color`
                ! green

                ```kotlin
                    val x = 10
                    val y = 20
                    val z = x + 7
                ```
                """

        val mu =
                """
                <h1>The Title</h1>
                <h2>The Title</h2>
                <h3>The Title</h3>
                <h4>The Title</h4>
                <h5>The Title</h5>
                <h6>The Title</h6>
                <p>
                    this is a normal paragraph with <strong>foo</strong>, <strong>bar</strong> and <del>baz</del>.
                </p>
                <p>
                    check <a href="www.google.com">this</a> and <a href="#this"></a> and <img src="foo/image" alt="this" /> for more information
                </p>
                <quote>
                    <p>this is a quote in two lines</p>
                    <quote><p>this one is embedded</p></quote>
                </quote>
                <ul>
                    <li><p>blue</p></li>
                    <li><p>red <code>color</code></p></li>
                    <li><p>green</p></li>
                </ul>
                <ol>
                    <li><p>blue</p></li>
                    <li><p>red <code>color</code></p></li>
                    <li><p>green</p></li>
                </ol>
                <pre>
                    val x = 10
                    val y = 20
                    val z = x + 7
                </pre>
                """

        verify(md, mu)
    }

    private fun verify(md: String, mu: String) {
        val normalized = mu.split("\n").map(String::trim).joinToString("")

        assertEquals(normalized, html.render(parser.parse(md)).replace("\n", ""))
    }
}