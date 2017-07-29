package io.kmd

class Html {

    fun render(paragraphs: List<Paragraph>): String =
            paragraphs.map { it.toHtml().render() }.joinToString("")

    private fun Text.toHtml(): HtmlElement = when (this) {
        is Text.Plain -> HtmlText(text)
        is Text.Emph -> HtmlEmph(elements.map { it.toHtml() })
        is Text.Bold -> HtmlBold(elements.map { it.toHtml() })
        is Text.Struck -> HtmlStruck(elements.mapNotNull { it.toHtml() })
        is Text.Code -> HtmlCode(text)
        is Text.Link -> HtmlLink(src, desc)
        is Text.Anchor -> HtmlAnchor(id)
        is Text.Image -> HtmlImage(src, alt)
    }

    private fun Paragraph.toHtml(): HtmlElement = when (this) {
        is Paragraph.Normal -> HtmlParagraph(elements.map { it.toHtml() })
        is Paragraph.Heading -> HtmlHeading(i, elements.map { it.toHtml() })
        is Paragraph.Pre -> HtmlPre(option, text)
        is Paragraph.Quote -> HtmlQuote(paragraphs.map { it.toHtml() })
        is Paragraph.Ulist -> HtmlUlist(items.map { it.map { it.toHtml() } })
        is Paragraph.Olist -> HtmlOlist(items.map { it.map { it.toHtml() } })
    }
}

private abstract class HtmlElement {

    companion object {
        private const val NOTHING = ""
    }

    open protected fun tag(): String = NOTHING

    open protected fun attributes(): List<Attribute> = emptyList()

    open protected fun hasBody(): Boolean = true

    protected open fun content(): String = NOTHING

    fun render(): String {
        val content = content()
        val attributes = attributes()
        val tag = tag()

        return when {
            tag == NOTHING -> content
            content == NOTHING && attributes.isEmpty() -> NOTHING
            else -> {
                val joinedAttributes = attributes
                        .map { it.render() }
                        .joinToString(separator = " ", prefix = " ")
                        .trimEnd()

                if (!hasBody())
                    "<$tag$joinedAttributes />"
                else
                    "<$tag$joinedAttributes>$content</$tag>"
            }
        }
    }
}

private data class HtmlParagraph(val elements: List<HtmlElement>) : HtmlElement() {

    override fun tag() = "p"

    override fun content() =
            elements.map { it.render() }.joinToString("")
}

private data class HtmlHeading(val i: Int, val elements: List<HtmlElement>) : HtmlElement() {

    override fun tag() = "h$i"

    override fun content() =
            elements.map { it.render() }.joinToString("")
}

private data class HtmlPre(val option: String, val text: String) : HtmlElement() {

    override fun tag() = "pre"

    override fun content() = text
}

private data class HtmlQuote(val elements: List<HtmlElement>) : HtmlElement() {

    override fun tag() = "quote"

    override fun content() =
            elements.map { it.render() }.joinToString("")
}

private data class HtmlUlist(val elements: List<List<HtmlElement>>) : HtmlElement() {

    override fun tag() = "ul"

    override fun content() =
            elements.map { li ->
                li.map {
                    "<li>${it.render()}</li>"
                }.joinToString("")
            }.joinToString("")
}

private data class HtmlOlist(val elements: List<List<HtmlElement>>) : HtmlElement() {

    override fun tag() = "ol"

    override fun content() =
            elements.map { li ->
                li.map {
                    "<li>${it.render()}</li>"
                }.joinToString("")
            }.joinToString("")
}

private data class HtmlText(val text: String) : HtmlElement() {

    override fun hasBody() = false

    override fun content() = text
}

private data class HtmlEmph(val elements: List<HtmlElement>) : HtmlElement() {

    override fun tag() = "em"

    override fun content() = elements.map { it.render() }.joinToString("")
}

private data class HtmlBold(val elements: List<HtmlElement>) : HtmlElement() {
    override fun tag() = "strong"

    override fun content() = elements.map { it.render() }.joinToString("")
}

private data class HtmlStruck(val elements: List<HtmlElement>) : HtmlElement() {

    override fun tag() = "del"

    override fun content() = elements.map { it.render() }.joinToString("")
}

private data class HtmlCode(val code: String) : HtmlElement() {

    override fun tag() = "code"

    override fun content() = code
}

private data class HtmlLink(val src: String, val desc: String) : HtmlElement() {

    override fun tag() = "a"

    override fun attributes() =
            listOf(
                    Attribute("href", src)
            ).filter { it.value.isNotEmpty() }

    override fun content() = desc
}

private data class HtmlAnchor(val id: String) : HtmlElement() {

    override fun tag() = "a"

    override fun attributes() =
            listOf(
                    Attribute("id", id)
            ).filter { it.value.isNotEmpty() }
}

private data class HtmlImage(val src: String, val alt: String) : HtmlElement() {

    override fun tag() = "img"

    override fun attributes() =
            listOf(
                    Attribute("src", src),
                    Attribute("alt", alt)
            ).filter { it.value.isNotEmpty() }

    override fun hasBody() = false
}

private data class Attribute(val name: String, val value: String) {

    fun render(): String = """$name="$value""""
}