#### KMD (Markdown to HTML Converter)

> __Kotlin implementatin of [mfp/ocsiblog](https://github.com/mfp/ocsiblog). A stand-alone, minimalistic blogging engine.__

Try the [Live Demo](http://kmd-demo.cfapps.io/)

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