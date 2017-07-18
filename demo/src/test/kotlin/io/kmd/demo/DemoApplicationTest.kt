package io.kmd.demo

import io.kmd.Html
import io.kmd.Parser
import org.hamcrest.CoreMatchers.containsString
import org.junit.Before
import org.junit.Test
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

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
                .andExpect(status().isFound)
                .andExpect(redirectedUrl("/"))
                .andExpect(request().sessionAttribute("markdown", containsString("#### KMD")))
    }

    @Test
    fun `view - the updated page`() {
        mvc.perform(
                post("/view")
                        .param("markdown", "Hello __World__"))
                .andExpect(status().isFound)
                .andExpect(redirectedUrl("/"))
                .andExpect(request().sessionAttribute("markdown", containsString("Hello __World__")))
    }
}
