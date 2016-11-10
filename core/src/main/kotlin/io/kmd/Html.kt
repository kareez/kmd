package io.kmd

class Html {
    fun render(paragraphs: List<Paragraph>): String {
        return paragraphs.map { toHtml(it).render() }.joinToString("")
    }

    private fun toHtml(t: Text): HtmlElement {
        return when (t) {
            is Text.Plain -> HtmlText(t.text)
            is Text.Emph -> HtmlEmph(t.elements.map { toHtml(it) })
            is Text.Bold -> HtmlBold(t.elements.map { toHtml(it) })
            is Text.Struck -> HtmlStruck(t.elements.mapNotNull { toHtml(it) })
            is Text.Code -> HtmlCode(t.text)
            is Text.Link -> HtmlLink(t.src, t.desc)
            is Text.Anchor -> HtmlAnchor(t.id)
            is Text.Image -> HtmlImage(t.src, t.alt)
        }
    }

    private fun toHtml(p: Paragraph): HtmlElement {
        return when (p) {
            is Paragraph.Normal -> HtmlParagraph(p.elements.map { toHtml(it) })
            is Paragraph.Heading -> HtmlHeading(p.i, p.elements.map { toHtml(it) })
            is Paragraph.Pre -> HtmlPre(p.option, p.text)
            is Paragraph.Quote -> HtmlQuote(p.paragraphs.map { toHtml(it) })
            is Paragraph.Ulist -> HtmlUlist(p.items.map { it.map { toHtml(it) } })
            is Paragraph.Olist -> HtmlOlist(p.items.map { it.map { toHtml(it) } })
        }
    }
}

private abstract class HtmlElement {
    companion object {
        private const val NOTHING = ""
    }

    open protected fun tag(): String = NOTHING

    open protected fun attributes(): List<Pair<String, String>> = emptyList()

    open protected fun hasBody(): Boolean = true

    protected open fun content(): String = NOTHING

    fun render(): String {
        val content = content()
        val attributes = attributes()
        val t = tag()

        if (t == NOTHING) return content

        if (attributes.isEmpty() && content == NOTHING) return NOTHING

        val joinedAttributes = attributes
                .map { """ ${it.first}="${it.second}"""" }
                .joinToString("")

        return if (!hasBody())
            "<$t$joinedAttributes />"
        else
            "<$t$joinedAttributes>$content</$t>"
    }
}

private data class HtmlParagraph(val elements: List<HtmlElement>) : HtmlElement() {

    override fun tag() = "p"

    override fun content(): String =
            elements.map { it.render() }.joinToString("")
}

private data class HtmlHeading(val i: Int, val elements: List<HtmlElement>) : HtmlElement() {

    override fun tag() = "h$i"

    override fun content(): String =
            elements.map { it.render() }.joinToString("")
}

private data class HtmlPre(val option: String, val text: String) : HtmlElement() {

    override fun tag() = "pre"

    override fun content(): String = text
}

private data class HtmlQuote(val elements: List<HtmlElement>) : HtmlElement() {

    override fun tag() = "quote"

    override fun content(): String =
            elements.map { it.render() }.joinToString("")
}

private data class HtmlUlist(val elements: List<List<HtmlElement>>) : HtmlElement() {

    override fun tag() = "ul"

    private val subTag = "li"

    override fun content(): String {
        return elements.map { li ->
            li.map { "<$subTag>${it.render()}</$subTag>" }.joinToString("")
        }.joinToString("")
    }
}

private data class HtmlOlist(val elements: List<List<HtmlElement>>) : HtmlElement() {

    override fun tag() = "ol"

    private val subTag = "li"

    override fun content(): String {
        return elements.map { li ->
            li.map { "<$subTag>${it.render()}</$subTag>" }.joinToString("")
        }.joinToString("")
    }
}

private data class HtmlText(val text: String) : HtmlElement() {

    override fun hasBody(): Boolean = false

    override fun content(): String = text
}

private data class HtmlEmph(val elements: List<HtmlElement>) : HtmlElement() {

    override fun tag() = "em"

    override fun content(): String = elements.map { it.render() }.joinToString("")
}

private data class HtmlBold(val elements: List<HtmlElement>) : HtmlElement() {
    override fun tag() = "strong"

    override fun content(): String = elements.map { it.render() }.joinToString("")
}

private data class HtmlStruck(val elements: List<HtmlElement>) : HtmlElement() {

    override fun tag() = "del"

    override fun content(): String = elements.map { it.render() }.joinToString("")
}

private data class HtmlCode(val code: String) : HtmlElement() {

    override fun tag() = "code"

    override fun content(): String = code
}

private data class HtmlLink(val src: String, val desc: String) : HtmlElement() {

    override fun tag() = "a"

    override fun attributes(): List<Pair<String, String>> =
            listOf(
                    Pair("href", src))
                    .filter { it.second.isNotEmpty() }

    override fun content(): String = desc
}

private data class HtmlAnchor(val id: String) : HtmlElement() {

    override fun tag() = "a"

    override fun attributes(): List<Pair<String, String>> =
            listOf(
                    Pair("id", id))
                    .filter { it.second.isNotEmpty() }
}

private data class HtmlImage(val src: String, val alt: String) : HtmlElement() {

    override fun tag() = "img"

    override fun attributes(): List<Pair<String, String>> =
            listOf(
                    Pair("src", src),
                    Pair("alt", alt))
                    .filter { it.second.isNotEmpty() }

    override fun hasBody(): Boolean = false
}