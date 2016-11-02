package io.kmd

import org.junit.Test

class ParserTest {

    @Test
    fun plain() {
        "foo" makes md { p { t("foo") } }
    }

    @Test
    fun emph() {
        "*foo*" makes md { p { e { t(("foo")) } } }
        "_foo_" makes md { p { e { t("foo") } } }

        "**" makes md { p { t("**") } }
        "_ _" makes md { p { t("_ _") } }
        "*foo *" makes md { p { t("*foo *") } }
        "_ foo_" makes md { p { t("_ foo_") } }
        "_ foo _" makes md { p { t("_ foo _") } }
    }

    @Test
    fun bold() {
        "**foo**" makes md { p { b { t("foo") } } }
        "__foo__" makes md { p { b { t("foo") } } }

        "****" makes md { p { b { } } }
        "__ __" makes md { p { t("__ __") } }
        "**foo **" makes md { p { t("**foo **") } }
        "__ foo__" makes md { p { t("__ foo__") } }
        "__ foo __" makes md { p { t("__ foo __") } }
    }

    @Test
    fun boldOrEmph() {
        "_foo_*bar***baz**__qax__" makes md {
            p {
                e { t("foo") }
                e { t("bar") }
                b { t("baz") }
                b { t("qax") }
            }
        }

        "_*foo*_" makes md {
            p {
                e { e { t("foo") } }
            }
        }

        "_[desc](src)_" makes md { p { e { l("src", "desc") } } }
    }

    @Test
    fun code() {
        "`foo`" makes md { p { c("foo") } }

        "``" makes md { p { c("") } }
        "` `" makes md { p { c("") } }
        "` foo`" makes md { p { c("foo") } }
        "`foo `" makes md { p { c("foo") } }
        "` foo `" makes md { p { c("foo") } }
    }

    @Test
    fun struck() {
        "~~foo~~" makes md { p { s { t("foo") } } }
    }

    @Test
    fun link() {
        "[desc](src)" makes md { p { l("src", "desc") } }
    }

    @Test
    fun anchor() {
        "[](#dst)" makes md { p { a("#dst") } }
    }

    @Test
    fun image() {
        "![alt](src)" makes md { p { i("src", "alt") } }
    }

    @Test
    fun normal() {
        "~~foo __bar__~~" makes md {
            p {
                s {
                    t("foo ")
                    b { t("bar") }
                }
            }
        }

        "[]()" makes md { p { t("") } }

        "[http://foo.com]()" makes md { p { l("http://foo.com", "http://foo.com") } }

        "*foo* ~~*foo*__bar___baz_~~" makes md {
            p {
                e { t("foo") }
                t(" ")
                s {
                    e { t("foo") }
                    b { t("bar") }
                    e { t("baz") }
                }
            }
        }

        "foo [](#internal-link). [back](#internal-link)" makes md {
            p {
                t("foo ")
                a("#internal-link")
                t(". ")
                l("#internal-link", "back")
            }
        }

        "foo *bar* *baz* __foobar__ _foobar_[desc](target)[alt](image)." makes md {
            p {
                t("foo ")
                e { t("bar") }
                t(" ")
                e { t("baz") }
                t(" ")
                b { t("foobar") }
                t(" ")
                e { t("foobar") }
                l("target", "desc")
                l("image", "alt")
                t(".")
            }
        }

        "foo __bar\nbaz__ foobar" makes md {
            p {
                t("foo ")
                b { t("bar baz") }
                t(" foobar")
            }

            "foo ~~bar\nbaz~~ foobar" makes md {
                p {
                    t("foo ")
                    s { t("bar baz") }
                    t(" foobar")
                }
            }
        }
    }

    @Test
    fun unmatchedNormal() {
        "foo * bar" makes md { p { t("foo * bar") } }

        "foo _ bar" makes md { p { t("foo _ bar") } }

        "foo __ bar" makes md { p { t("foo __ bar") } }

        "foo == bar" makes md { p { t("foo == bar") } }

        "foo == bar\n\nbaz ==" makes md {
            p { t("foo == bar") }
            p { t("baz ==") }
        }
    }

    @Test
    fun heading() {
        "# foo [desc](src)" makes md {
            h1 {
                t("foo ")
                l("src", "desc")
            }
        }

        "## foo [desc](src)" makes md {
            h2 {
                t("foo ")
                l("src", "desc")
            }
        }

        "### foo [desc](src)" makes md {
            h3 {
                t("foo ")
                l("src", "desc")
            }
        }

        "#### foo [desc](src)" makes md {
            h4 {
                t("foo ")
                l("src", "desc")
            }
        }

        "##### foo [desc](src)" makes md {
            h5 {
                t("foo ")
                l("src", "desc")
            }
        }

        "###### foo [desc](src)" makes md {
            h6 {
                t("foo ")
                l("src", "desc")
            }
        }
    }

    @Test
    fun pre() {
        "```foo\nhi\n```" makes md { pre("foo", "hi\n") }

        "foo * bar\n```\na\n b\n  c\n```\n\n```whatever\na\\0\\1\\2\n b\n  c\n```\n  " makes md {
            p { t("foo * bar") }
            pre("", "a\n b\n  c\n")
            pre("whatever", "a\\0\\1\\2\n b\n  c\n")
        }

        """```foobar
           a
            b
             c
           ```""" makes md { pre("foobar", "a\n b\n  c\n") }

        """   ```foo
                 a
                  b
                   c
              ```""" makes md { pre("foo", "a\n b\n  c\n") }

        """```
              a
               \```
                \\```
                 ````
           ```""" makes md { pre("", "a\n ```\n  \\```\n   ````\n") }
    }

    @Test
    fun quote() {
        "> xxx" makes md { q { p { t("xxx") } } }

        "> \n> xxx\n> " makes md { q { p { t("xxx") } } }

        "> > xxx" makes md { q { q { p { t("xxx") } } } }

        """foo says:

           > xxx:
           > * xxx
           >   yyy
           > * __2__
           > * _2_
           > * *3*
           > > yyy
           > > > zzz
           > > aaa

        """ makes md {
            p { t("foo says:") }
            q {
                p { t("xxx:") }
                ul {
                    li { p { t("xxx yyy") } }
                    li { p { b { t("2") } } }
                    li { p { e { t("2") } } }
                    li { p { e { t("3") } } }
                }
                q {
                    p { t("yyy") }
                    q { p { t("zzz") } }
                    p { t("aaa") }
                }
            }
        }

        """> * one
           >
           >   xxx
           > * two
        """ makes md {
            q {
                ul {
                    li {
                        p { t("one") }
                        p { t("xxx") }
                    }
                    li { p { t("two") } }
                }
            }
        }
    }

    @Test
    fun list() {
        "* foo\n*bar*\n* baz" makes md {
            ul {
                li {
                    p {
                        t("foo ")
                        e { t("bar") }
                    }
                }
                li { p { t("baz") } }
            }
        }

        "* foo\nbar \n   baz\n* baz" makes md {
            ul {
                li { p { t("foo bar baz") } }
                li { p { t("baz") } }
            }
        }

        "* foo\n\n bar\n* baz" makes md {
            ul {
                li {
                    p { t("foo") }
                    p { t("bar") }
                }
                li { p { t("baz") } }
            }
        }

        "* foo" makes md { ul { li { p { t("foo") } } } }

        "* foo\n* bar" makes md {
            ul {
                li { p { t("foo") } }
                li { p { t("bar") } }
            }
        }

        "* foo\n\n* bar" makes md {
            ul {
                li { p { t("foo") } }
                li { p { t("bar") } }
            }
        }

        "* foo\n\n * bar" makes md {
            ul {
                li {
                    p { t("foo") }
                    ul { li { p { t("bar") } } }
                }
            }
        }

        "* foo\n\n * bar\n ! 1\n ! 2\n! 3" makes md {
            ul {
                li {
                    p { t("foo") }
                    ul { li { p { t("bar") } } }
                    ol {
                        li { p { t("1") } }
                        li { p { t("2") } }
                    }
                }
            }
            ol { li { p { t("3") } } }
        }

        "* foo\n\n * bar\n ! 1\n ! 2\n!3" makes md {
            ul {
                li {
                    p { t("foo") }
                    ul { li { p { t("bar") } } }
                    ol {
                        li { p { t("1") } }
                        li { p { t("2 !3") } }
                    }
                }
            }
        }

        """
         *   some
             paragraph

             And another one.

         *   two
         *   three
        """ makes md {
            ul {
                li {
                    p { t("some paragraph") }
                    p { t("And another one.") }
                }
                li { p { t("two") } }
                li { p { t("three") } }
            }
        }

        "*\tfoo\n*bar\n baz*\n\n xxx\n\n* baz" makes md {
            ul {
                li {
                    p {
                        t("foo ")
                        e { t("bar baz") }
                    }
                    p { t("xxx") }
                }
                li { p { t("baz") } }
            }
        }

        "foo\n*\tbar" makes md {
            p { t("foo") }
            ul { li { p { t("bar") } } }
        }

        """
         !  one
         !  two
         !  three
        """ makes md {
            ol {
                li { p { t("one") } }
                li { p { t("two") } }
                li { p { t("three") } }
            }
        }
    }
}


































