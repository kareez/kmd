package io.kmd

import io.kmd.Paragraph.*
import io.kmd.ParagraphType.*
import io.kmd.Text.*


sealed class Text {

    data class Plain(val text: String) : Text()

    data class Emph(val elements: List<Text>) : Text()

    data class Bold(val elements: List<Text>) : Text()

    data class Struck(val elements: List<Text>) : Text()

    data class Code(val text: String) : Text()

    data class Link(val src: String, val desc: String) : Text()

    data class Anchor(val id: String) : Text()

    data class Image(val src: String, val alt: String) : Text()
}

sealed class Paragraph {

    data class Normal(val elements: List<Text>) : Paragraph()

    data class Heading(val i: Int, val elements: List<Text>) : Paragraph()

    data class Pre(val option: String, val text: String) : Paragraph()

    data class Quote(val paragraphs: List<Paragraph>) : Paragraph()

    data class Ulist(val items: List<List<Paragraph>>) : Paragraph()

    data class Olist(val items: List<List<Paragraph>>) : Paragraph()
}

enum class ParagraphType {
    OLIST {
        override fun match(s: String) = s.startsWith("! ") || s.startsWith("!\t")
    },
    ULIST {
        override fun match(s: String) = s.startsWith("* ") || s.startsWith("*\t")
    },
    QUOTE {
        override fun match(s: String) = s.startsWith("> ") || s.startsWith(">\t") || s == ">"
    },
    PRE {
        override fun match(s: String) = s.startsWith("```")
    },
    HEADING_1 {
        override fun match(s: String) = s.startsWith("# ") || s.startsWith("#\t")
    },
    HEADING_2 {
        override fun match(s: String) = s.startsWith("## ") || s.startsWith("##\t")
    },
    HEADING_3 {
        override fun match(s: String) = s.startsWith("### ") || s.startsWith("###\t")
    },
    HEADING_4 {
        override fun match(s: String) = s.startsWith("#### ") || s.startsWith("####\t")
    },
    HEADING_5 {
        override fun match(s: String) = s.startsWith("##### ") || s.startsWith("#####\t")
    },
    HEADING_6 {
        override fun match(s: String) = s.startsWith("###### ") || s.startsWith("######\t")
    },
    NORMAL {
        override fun match(s: String) = true
    };

    abstract fun match(s: String): Boolean

    companion object {
        fun from(s: String): ParagraphType = when {
            OLIST.match(s) -> OLIST
            ULIST.match(s) -> ULIST
            QUOTE.match(s) -> QUOTE
            PRE.match(s) -> PRE
            HEADING_6.match(s) -> HEADING_6
            HEADING_5.match(s) -> HEADING_5
            HEADING_4.match(s) -> HEADING_4
            HEADING_3.match(s) -> HEADING_3
            HEADING_2.match(s) -> HEADING_2
            HEADING_1.match(s) -> HEADING_1
            else -> NORMAL
        }
    }
}

class Parser {
    companion object {
        private const val NOT_FOUND = -1
    }

    fun parse(text: String): List<Paragraph> =
            collect({ readParagraph(0, it) }, text.split("\n").map { Line.new(it) }.toMutableList())

    private fun String.unescape(): String = this.filterIndexed { i, ch -> i == this.length - 1 || ch != '\\' }

    private fun String.unescape(first: Int, last: Int): String = this.substring(first, last).trim().unescape()

    private fun collect(f: (MutableList<Line>) -> Paragraph?, lines: MutableList<Line>): List<Paragraph> =
            generateSequence { f(lines) }.toList()

    private fun push(lines: MutableList<Line>, s: String, indent: Int, first: Int = 2) =
            lines.add(0, Line.new(s.substring(first), indent + first))

    private fun skipBlankLine(lines: MutableList<Line>) {
        while (lines.isNotEmpty() && lines.first().isBlank) {
            lines.removeAt(0)
        }
    }

    private fun readParagraph(indent: Int, lines: MutableList<Line>, skipBlank: Boolean = true): Paragraph? {
        if (skipBlank) {
            skipBlankLine(lines)
        }

        if (lines.isEmpty()) {
            return null
        }

        val line = lines.first()
        return if (line.isBlank) {
            lines.removeAt(0)
            null

        } else if (line.indent < indent) {
            null

        } else {
            lines.removeAt(0)
            readNonEmpty(line, lines)
        }
    }

    private fun readNonEmpty(line: Line, lines: MutableList<Line>): Paragraph? =
            when (ParagraphType.from(line.content)) {
                HEADING_6 -> readHeading(6, line.content)
                HEADING_5 -> readHeading(5, line.content)
                HEADING_4 -> readHeading(4, line.content)
                HEADING_3 -> readHeading(3, line.content)
                HEADING_2 -> readHeading(2, line.content)
                HEADING_1 -> readHeading(1, line.content)
                PRE -> readPre(line.content.substring(3), lines)
                QUOTE -> {
                    lines.add(0, line)
                    readQuote(line.indent, lines)
                }
                ULIST -> {
                    push(lines, line.content, line.indent)
                    readUlist(line.indent, lines)
                }
                OLIST -> {
                    push(lines, line.content, line.indent)
                    readOlist(line.indent, lines)
                }
                else -> {
                    lines.add(0, line)
                    readNormal(lines)
                }
            }

    private fun readHeading(level: Int, s: String): Paragraph =
            Heading(level,
                    scan(s.substring(level + 1), State(s.length - level - 1), 0))

    private fun readPre(s: String, lines: MutableList<Line>): Paragraph {
        val kind = s.trim()

        fun build(ls: List<String>): Pre = Pre(kind, ls.joinToString(separator = "\n", postfix = "\n"))

        fun unescape(c: String): String = if (c.matches(Regex("^\\\\+```$"))) c.substring(1) else c

        fun readUntilEnd(firstIndent: Int, ls: MutableList<String>): Pre {
            return when {
                lines.isEmpty() -> build(ls)
                lines.first().content == "```" -> {
                    lines.removeAt(0)
                    build(ls)
                }
                else -> {
                    val line = lines.removeAt(0)
                    val space = "".padEnd(Math.max(0, line.indent - firstIndent), ' ')
                    ls.add(space + unescape(line.content))
                    readUntilEnd(firstIndent, ls)
                }
            }
        }

        return when {
            lines.isEmpty() -> build(emptyList())
            lines.first().content == "```" -> {
                lines.removeAt(0)
                build(emptyList())
            }
            else -> {
                val line = lines.removeAt(0)
                readUntilEnd(line.indent, mutableListOf(line.content))
            }
        }
    }

    private fun readQuote(indent: Int, lines: MutableList<Line>): Paragraph? {
        val quotedLines = lines.takeWhile { !it.isBlank && it.indent >= indent && it.content[0] == '>' }.map {
            lines.removeAt(0)

            val content = it.content.substring(1)
            val trimmed = content.trimStart()
            Line(trimmed, content.length - trimmed.length, trimmed.isEmpty())
        }.toMutableList()

        val paragraphs = collect({ readParagraph(0, it) }, quotedLines)

        return if (paragraphs.isEmpty()) null else Paragraph.Quote(paragraphs)
    }

    private fun readUlist(indent: Int, lines: MutableList<Line>): Paragraph? {
        return readList(indent, lines, ::Ulist, { ULIST.match(it) })
    }

    private fun readOlist(indent: Int, lines: MutableList<Line>): Paragraph? {
        return readList(indent, lines, ::Olist, { OLIST.match(it) })
    }

    private fun readList(indent: Int,
                         lines: MutableList<Line>,
                         make: (List<List<Paragraph>>) -> Paragraph,
                         match: (String) -> Boolean): Paragraph? {

        fun readItem(lineIndent: Int) = collect({ readParagraph(lineIndent + 1, it) }, lines)

        fun readAll(items: MutableList<List<Paragraph>>): Paragraph? {
            skipBlankLine(lines)
            return if (lines.isNotEmpty() && lines.first().indent >= indent && match(lines.first().content)) {
                val line = lines.removeAt(0)
                push(lines, line.content, line.indent)
                items.add(readItem(line.indent))
                readAll(items)
            } else {
                make(items)
            }
        }

        return readAll(mutableListOf(readItem(indent)))
    }

    private fun readNormal(lines: MutableList<Line>): Paragraph {
        val content = lines.takeWhile {
            !it.isBlank && ParagraphType.from(it.content) == NORMAL
        }.map {
            lines.removeAt(0).content
        }.joinToString(" ")

        return Normal(scan(content, State(content.length), 0))
    }

    private fun scan(content: String, state: State, n: Int): List<Text> {
        if (n >= state.max) {
            return state.push()
        }

        return when {
            content.matchesAt(n, "**", state.max) -> delimited("**", content, state, n) { first, last ->
                Bold(scan(content, State(last), first))
            }
            content.matchesAt(n, "__", state.max) -> delimited("__", content, state, n) { first, last ->
                Bold(scan(content, State(last), first))
            }
            content.matchesAt(n, "*", state.max) -> delimited("*", content, state, n) { first, last ->
                Emph(scan(content, State(last), first))
            }
            content.matchesAt(n, "_", state.max) -> delimited("_", content, state, n) { first, last ->
                Emph(scan(content, State(last), first))
            }
            content.matchesAt(n, "`", state.max) -> delimited("`", content, state, n, true) { first, last ->
                Code(content.unescape(first, last))
            }
            content.matchesAt(n, "~~", state.max) -> delimited("~~", content, state, n) { first, last ->
                Struck(scan(content, State(last), first))
            }
            content.matchesAt(n, "![", state.max) -> maybeLink("![", content, state, n + 2) { (src, desc) ->
                Image(src, desc)
            }
            content.matchesAt(n, "[", state.max) -> maybeLink("[", content, state, n + 1, Ref::toText)
            content.matchesAt(n, "\\", state.max) && (n + 1 < state.max) -> {
                state.put(content[n + 1])
                scan(content, state, n + 2)
            }
            else -> {
                state.put(content[n])
                scan(content, state, n + 1)
            }
        }
    }

    private fun delimited(delimiter: String,
                          content: String,
                          state: State,
                          n: Int,
                          space: Boolean = false,
                          build: (first: Int, last: Int) -> Text): List<Text> {

        fun scanFromNextChar(): List<Text> {
            state.put(content[n])
            return scan(content, state, n + 1)
        }

        val m = scanPast(delimiter, content, state.max, n + delimiter.length)
        if (m == NOT_FOUND) {
            return scanFromNextChar()
        }

        val first = n + delimiter.length
        val last = m - delimiter.length

        return if (!space && (content[first].isWhitespace() || content[last - 1].isWhitespace())) {
            scanFromNextChar()
        } else {
            scan(content, state.pushThenCloneWith(build(first, last)), m)
        }
    }

    private fun scanPast(delimiter: String, content: String, max: Int, n: Int): Int {
        var m = n
        while (m < max) {

            val index = content.indexOf(delimiter, m)
            if (index == NOT_FOUND || index >= max) {
                break
            }

            if (content[index - 1] != '\\') {
                return index + delimiter.length
            }
            m++
        }

        return NOT_FOUND
    }

    private fun maybeLink(delimiter: String, content: String, state: State, n: Int, build: (Ref) -> Text): List<Text> {

        val endOfDesc = scanPast("]", content, state.max, n)
        if (endOfDesc == NOT_FOUND || endOfDesc > state.max || content[endOfDesc] != '(') {
            state.put(delimiter)
            return scan(content, state, n)
        }

        val endOfUri = scanPast(")", content, state.max, endOfDesc + 1)
        if (endOfUri == NOT_FOUND) {
            state.put(delimiter)
            return scan(content, state, n)
        }

        val ref = Ref(content.unescape(endOfDesc + 1, endOfUri - 1), content.unescape(n, endOfDesc - 1))
        return scan(content, state.pushThenCloneWith(build(ref)), endOfUri)
    }

    private fun String.matchesAt(n: Int, delimiter: String, max: Int): Boolean {
        return delimiter.length + n <= max && this.substring(n, n + delimiter.length) == delimiter
    }

    private data class State(val max: Int, private val fragments: List<Text> = emptyList()) {

        private val current: MutableList<Char> = mutableListOf()

        fun put(c: Char) = current.add(c)

        fun put(s: String) = s.forEach { c -> current.add(c) }

        fun push(): List<Text> = when (this.current.isNotEmpty()) {
            true -> this.fragments + Plain(this.current.joinToString(""))
            else -> this.fragments
        }

        fun pushThenCloneWith(element: Text): State = State(this.max, this.push() + element)
    }

    private data class Ref(val src: String, val desc: String) {

        fun toText(): Text = when {
            src.isBlank() && desc.isBlank() -> Plain("")
            src.isBlank() -> Link(desc, desc)
            desc.isBlank() && src[0] == '#' -> Anchor(src)
            else -> Link(src, desc)
        }
    }

    private data class Line(val content: String, val indent: Int, val isBlank: Boolean) {
        companion object {
            const val TAB_SIZE = 8

            fun new(line: String, indent: Int = 0): Line {
                val spaces = line.takeWhile(Char::isWhitespace).map { if (it == '\t') TAB_SIZE else 1 }.sum()
                val content = line.trim()

                return Line(content, indent + spaces, content.isEmpty())
            }
        }
    }
}