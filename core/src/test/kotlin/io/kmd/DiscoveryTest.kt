package io.kmd

import org.junit.Ignore
import org.junit.Test

class DiscoveryTest {

    @Test
    fun skipBlankLines() {
        "foo\n\n\nbar" makes md {
            p { t("foo") }
            p { t("bar") }
        }

        "\n\n\nfoo" makes md { p { t("foo") } }

        "* foo\n\n\n* bar" makes md {
            ul {
                li { p { t("foo") } }
                li { p { t("bar") } }
            }
        }
    }

    @Test
    fun emptyQuoteLine() {
        ">\n> foo" makes md {
            q { p { t("foo") } }
        }

        "foo\n>\n> bar" makes md {
            p { t("foo") }
            q { p { t("bar") } }
        }
    }

    @Test
    fun spaceOrTab() {
        "# foo" makes md { h1 { t("foo") } }
        "#\tfoo" makes md { h1 { t("foo") } }

        "> foo" makes md { q { p { t("foo") } } }
        ">\tfoo" makes md { q { p { t("foo") } } }

        "! foo" makes md { ol { li { p { t("foo") } } } }
        "!\tfoo" makes md { ol { li { p { t("foo") } } } }
    }

    @Test
    fun allHeadsWithSpaceAndTab() {
        "# foo" makes md { h1 { t("foo") } }
        "#\tfoo" makes md { h1 { t("foo") } }

        "## foo" makes md { h2 { t("foo") } }
        "##\tfoo" makes md { h2 { t("foo") } }

        "### foo" makes md { h3 { t("foo") } }
        "###\tfoo" makes md { h3 { t("foo") } }

        "#### foo" makes md { h4 { t("foo") } }
        "####\tfoo" makes md { h4 { t("foo") } }

        "##### foo" makes md { h5 { t("foo") } }
        "#####\tfoo" makes md { h5 { t("foo") } }

        "###### foo" makes md { h6 { t("foo") } }
        "######\tfoo" makes md { h6 { t("foo") } }
    }

    @Test
    fun escapeInPre() {
        "```\n\\```\n```" makes md { pre("", "\\```\n") }
        "```\n\\\\```\n```" makes md { pre("", "\\\\```\n") }
        "```\n\\\\\\\\```\n```" makes md { pre("", "\\\\\\\\```\n") }
        //TODO: get it
    }

    @Test
    fun listWithIndent() {
        """
        * foo
         bar
          baz
        """ makes md { ul { li { p { t("foo bar baz") } } } }

        """
        * foo
         * bar
        """ makes md {
            ul {
                li {
                    p { t("foo") }
                    ul { li { p { t("bar") } } }
                }
            }
        }
    }

    @Test
    fun emptyEmphOrBold() {
        "**" makes md {
            p { t("**") }
        }

        "__" makes md {
            p { t("__") }
        }
    }

    @Test
    fun discovery() {
        val list = mutableListOf(1, 2, 3, 4, 5, 6, 7)

        list.takeWhile { it < 5 }.map { list.removeAt(0); it }.forEach { }
    }

    @Ignore
    @Test
    fun todo() {
        // TODO: live demo
        // TODO: anchor???
        // TODO: Ordered list
    }
}


