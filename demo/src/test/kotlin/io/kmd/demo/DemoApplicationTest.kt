package io.kmd.demo

import io.kmd.Html
import io.kmd.Parser
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner::class)
class DemoApplicationTest {

    private lateinit var mvc: MockMvc

    @Before
    fun setup() {
        this.mvc = standaloneSetup(MainController(Parser(), Html())).build()
    }

    @Test
    fun `home - the default page`() {
        mvc.perform(get("/"))
                .andExpect(status().isOk)
                .andExpect(model().attribute("markdown", containsString("#### KMD")))
                .andExpect(model().attribute("markup", containsString("<h4>KMD (Markdown to HTML Converter)</h4>")))
    }

    @Test
    fun `reset - the default page`() {
        mvc.perform(get("/reset"))
                .andExpect(status().isOk)
                .andExpect(model().attribute("markdown", containsString("#### KMD")))
                .andExpect(model().attribute("markup", containsString("<h4>KMD (Markdown to HTML Converter)</h4>")))
    }

    @Test
    fun `view - the updated page`() {
        mvc.perform(
                post("/view")
                        .param("markdown", "Hello __World__"))
                .andExpect(status().isOk)
                .andExpect(model().attribute("markdown", containsString("Hello __World__")))
                .andExpect(model().attribute("markup", equalTo("<p>Hello <strong>World</strong></p>")))
    }
}
