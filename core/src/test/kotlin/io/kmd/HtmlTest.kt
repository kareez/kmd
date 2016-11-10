package io.kmd

import io.kmd.Paragraph.*
import io.kmd.Text.*
import org.junit.Test
import kotlin.test.assertEquals

class HtmlTest {
    private val html = Html()

    @Test
    fun `render plain`() {
        Plain("") renders ""
        Plain("foo") renders "foo"
        Plain("foo") and Plain("bar") renders "foobar"
        Plain("foo") and Plain("bar") and Plain("baz") renders "foobarbaz"
    }

    @Test
    fun `render emph`() {
        Emph(listOf(Plain(""))) renders ""
        Emph(listOf(Plain("foo"), Plain("bar"))) renders "<em>foobar</em>"
        Emph(listOf(Plain("foo"))) and Emph(listOf(Plain("bar"))) renders "<em>foo</em><em>bar</em>"
    }

    @Test
    fun `render bold`() {
        Bold(listOf(Plain(""))) renders ""
        Bold(listOf(Plain("foo"), Plain("bar"))) renders "<strong>foobar</strong>"
        Bold(listOf(Plain("foo"))) and Bold(listOf(Plain("bar"))) renders "<strong>foo</strong><strong>bar</strong>"
    }

    @Test
    fun `render struck`() {
        Struck(listOf(Plain(""))) renders ""
        Struck(listOf(Plain("foo"), Plain("bar"))) renders "<del>foobar</del>"
        Struck(listOf(Plain("foo"))) and Struck(listOf(Plain("bar"))) renders "<del>foo</del><del>bar</del>"
    }

    @Test
    fun `render code`() {
        Code("") renders ""
        Code("foo") renders "<code>foo</code>"
        Code("foo") and Code("bar") renders "<code>foo</code><code>bar</code>"
    }

    @Test
    fun `render link`() {
        Link("", "") renders ""
        Link("foo", "bar") renders """<a href="foo">bar</a>"""
    }

    @Test
    fun `render anchor`() {
        Anchor("foo") renders """<a id="foo"></a>"""
    }

    @Test
    fun `render image`() {
        Image("", "") renders ""
        Image("foo", "bar") renders """<img src="foo" alt="bar" />"""
    }

    @Test
    fun `render paragraph`() {
        Normal(emptyList()) renders ""
        Normal(listOf(Plain("foo"))) renders "<p>foo</p>"
        Normal(Plain("foo") and Plain("bar")) renders "<p>foobar</p>"
    }

    @Test
    fun `render heading`() {
        Heading(1, emptyList()) renders ""
        Heading(2, listOf(Plain("foo"))) renders "<h2>foo</h2>"
        Heading(3, Plain("foo") and Plain("bar")) renders "<h3>foobar</h3>"
    }

    @Test
    fun `render pre`() {
        Pre("", "") renders ""
        Pre("", "foo\nbar") renders "<pre>foo\nbar</pre>"
    }

    @Test
    fun `render quote`() {
        Quote(emptyList()) renders ""
        Quote(listOf(Normal(listOf(Plain("foo"))))) renders "<quote><p>foo</p></quote>"
    }

    @Test
    fun `render u-list`() {
        Ulist(emptyList()) renders ""

        val items = listOf(
                listOf(Normal(listOf(Plain("foo")))),
                listOf(Normal(listOf(Plain("bar")))))

        Ulist(items) renders "<ul><li><p>foo</p></li><li><p>bar</p></li></ul>"
    }

    @Test
    fun `render o-list`() {
        Olist(emptyList()) renders ""

        val items = listOf(
                listOf(Normal(listOf(Plain("foo")))),
                listOf(Normal(listOf(Plain("bar")))))

        Olist(items) renders "<ol><li><p>foo</p></li><li><p>bar</p></li></ol>"
    }

    private infix fun Text.and(t: Text): List<Text> = listOf(this, t)

    private infix fun List<Text>.and(t: Text): List<Text> = this + t

    private infix fun Text.renders(content: String) {
        val rendered = html.render(listOf(Normal(listOf(this))))

        assertEquals(content, normalize(rendered))
    }

    private infix fun List<Text>.renders(content: String) {
        val rendered = html.render(listOf(Normal(this)))

        assertEquals(content, normalize(rendered))
    }

    private fun normalize(rendered: String): String =
            if (rendered.startsWith("<p>")) rendered.substring(3, rendered.length - 4) else rendered

    private infix fun Paragraph.renders(content: String) {
        val rendered = html.render(listOf(this))

        assertEquals(content, rendered)
    }
}
