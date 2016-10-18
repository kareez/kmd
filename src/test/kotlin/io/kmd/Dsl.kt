package io.kmd

import org.junit.Assert

infix fun String.makes(md: Md) {
    Assert.assertEquals(md.build(), Parser().parse(this))
}

fun md(init: Md.() -> Unit): Md {
    val m = Md()
    m.init()
    return m
}

interface ParagraphContainer {
    val children: MutableList<MdParagraph>

    private fun <T : MdParagraph> initElement(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    fun p(init: MdNormal.() -> Unit) = initElement(MdNormal(), init)

    fun h1(init: MdHead1.() -> Unit) = initElement(MdHead1(), init)

    fun h2(init: MdHead2.() -> Unit) = initElement(MdHead2(), init)

    fun h3(init: MdHead3.() -> Unit) = initElement(MdHead3(), init)

    fun h4(init: MdHead4.() -> Unit) = initElement(MdHead4(), init)

    fun h5(init: MdHead5.() -> Unit) = initElement(MdHead5(), init)

    fun h6(init: MdHead6.() -> Unit) = initElement(MdHead6(), init)

    fun pre(option: String, text: String) = initElement(MdPre(option, text), {})

    fun q(init: MdQuote.() -> Unit) = initElement(MdQuote(), init)

    fun ul(init: MdUnorderedList.() -> Unit) = initElement(MdUnorderedList(), init)

    fun ol(init: MdOrderedList.() -> Unit) = initElement(MdOrderedList(), init)
}

interface TextContainer {
    val children: MutableList<MdText>

    private fun <T : MdText> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    fun t(text: String) = children.add(MdPlain(text))

    fun e(init: MdEmph.() -> Unit) = initTag(MdEmph(), init)

    fun b(init: MdBold.() -> Unit) = initTag(MdBold(), init)

    fun s(init: MdStruck.() -> Unit) = initTag(MdStruck(), init)

    fun c(text: String) = children.add(MdCode(text))

    fun l(src: String, desc: String) = children.add(MdLink(src, desc))

    fun a(src: String) = children.add(MdAnchor(src))

    fun i(src: String, alt: String) = children.add(MdImage(src, alt))
}

class Md : ParagraphContainer {
    override val children = mutableListOf<MdParagraph>()

    fun build(): List<Paragraph> = children.map { it.build() }.toList()
}

abstract class MdParagraph {

    abstract fun build(): Paragraph
}

class MdNormal : MdParagraph(), TextContainer {
    override val children = mutableListOf<MdText>()

    override fun build(): Paragraph = Paragraph.Normal(this.children.map { it.build() }.toList())
}

open class MdHead(val i: Int) : MdParagraph(), TextContainer {
    override val children = mutableListOf<MdText>()

    override fun build(): Paragraph = Paragraph.Heading(i, this.children.map { it.build() }.toList())
}

class MdHead1 : MdHead(1)
class MdHead2 : MdHead(2)
class MdHead3 : MdHead(3)
class MdHead4 : MdHead(4)
class MdHead5 : MdHead(5)
class MdHead6 : MdHead(6)

class MdPre(val option: String, val text: String) : MdParagraph(), TextContainer {
    override val children = mutableListOf<MdText>()

    override fun build(): Paragraph = Paragraph.Pre(option, text)
}

class MdQuote : MdParagraph(), ParagraphContainer {
    override val children = mutableListOf<MdParagraph>()

    override fun build(): Paragraph = Paragraph.Quote(this.children.map { it.build() }.toList())
}

abstract class MdList : MdParagraph() {
    protected val children = mutableListOf<MdListItem>()

    private fun initElement(tag: MdListItem, init: MdListItem.() -> Unit): MdListItem {
        tag.init()
        children.add(tag)
        return tag
    }

    fun li(init: MdListItem.() -> Unit) = initElement(MdListItem(), init)
}

class MdUnorderedList : MdList() {
    override fun build(): Paragraph = Paragraph.Ulist(this.children.map { it.build() }.toList())
}

class MdOrderedList : MdList() {
    override fun build(): Paragraph = Paragraph.Olist(this.children.map { it.build() }.toList())
}

class MdListItem : ParagraphContainer {
    override val children = mutableListOf<MdParagraph>()

    fun build(): List<Paragraph> = this.children.map { it.build() }.toList()
}

abstract class MdText {
    abstract fun build(): Text
}

class MdPlain(val text: String) : MdText() {
    override fun build(): Text = Text.Plain(text)
}

class MdEmph() : MdText(), TextContainer {
    override val children = mutableListOf<MdText>()

    override fun build(): Text = Text.Emph(children.map { it.build() }.toList())
}

class MdBold() : MdText(), TextContainer {
    override val children = mutableListOf<MdText>()

    override fun build(): Text = Text.Bold(children.map { it.build() }.toList())
}

class MdCode(val text: String) : MdText() {
    override fun build(): Text = Text.Code(text)
}

class MdLink(val src: String, val desc: String) : MdText() {
    override fun build(): Text = Text.Link(src, desc)
}

class MdAnchor(val src: String) : MdText() {
    override fun build(): Text = Text.Anchor(src)
}

class MdImage(val src: String, val alt: String) : MdText() {
    override fun build(): Text = Text.Image(src, alt)
}

class MdStruck : MdText(), TextContainer {
    override val children = mutableListOf<MdText>()

    override fun build(): Text = Text.Struck(children.map { it.build() }.toList())
}