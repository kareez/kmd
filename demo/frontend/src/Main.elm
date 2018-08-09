module Main exposing (..)

import Html exposing (..)
import Html.Attributes exposing (..)
import Html.Events exposing (onInput, onClick)
import Http
import Json.Encode as Encode exposing (string)


main : Program Never Model Msg
main =
    Html.program
        { init = init
        , view = view
        , update = update
        , subscriptions = \_ -> Sub.none
        }



-- MODEL


type alias Model =
    { markdown : String
    , markup : String
    }


init : ( Model, Cmd Msg )
init =
    ( Model sample "", convert sample )


sample : String
sample =
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

Ordered lists are indicated by !.
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
    Last Update: Thursday 9. August 2018
```
"""



-- UPDATE


type Msg
    = Reset
    | Convert String
    | Done (Result Http.Error String)


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        Reset ->
            init

        Convert markdown ->
            ( { model | markdown = markdown }, convert markdown )

        Done (Ok markup) ->
            ( { model | markup = markup }, Cmd.none )

        Done (Err error) ->
            ( { model | markup = "<h3>Ops...!</h3><code>" ++ toString error ++ "</code>" }, Cmd.none )



-- VIEW


view : Model -> Html Msg
view model =
    body []
        [ h3 [ class "headline" ] [ text "A live demo of supported markdowns" ]
        , div [ class "mdl-grid" ]
            [ div [ class "mdl-cell mdl-cell--4-col" ]
                [ button [ class "mdl-button mdl-button--raised", onClick Reset ] [ text "Reset" ] ]
            ]
        , div [ class "mdl-grid" ]
            [ div [ class "mdl-cell mdl-cell--5-col" ]
                [ textarea [ class "mdl-textfield__input", onInput Convert, value model.markdown ] [] ]
            , div [ class "mdl-cell mdl-cell--5-col" ]
                [ div [ property "innerHTML" <| Encode.string model.markup ] [] ]
            ]
        ]



-- HTTP


convert : String -> Cmd Msg
convert markdown =
    let
        request =
            Http.request
                { method = "POST"
                , headers = []
                , url = "https://kmd-backend.cfapps.io/"
                , body = Http.stringBody "text/plain" markdown
                , expect = Http.expectString
                , timeout = Nothing
                , withCredentials = False
                }
    in
        Http.send Done request
