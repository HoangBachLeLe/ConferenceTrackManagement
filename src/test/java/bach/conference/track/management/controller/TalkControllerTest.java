package bach.conference.track.management.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import bach.conference.track.management.service.TalkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = TalkController.class)
class TalkControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    TalkService service;

    @Test
    void getIndex() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().string(containsString("Conference Track Management")));

        verify(service).findAllTalks();
    }

    @Test
    void tracksWithTalks() throws Exception {
        mvc.perform(get("/tracks"))
                .andExpect(status().isOk())
                .andExpect(view().name("tracks"))
                .andExpect(content().string(containsString("Tracks")));

        verify(service).splitTalksIntoTracks(any());
    }

    @Test
    void randomOrder() throws Exception {
        mvc.perform(get("/randomOrder"))
                .andExpect(status().isOk())
                .andExpect(view().name("tracks"))
                .andExpect(content().string(containsString("Tracks")));

        verify(service).splitTalksIntoTracks(any());
    }

    @Test
    void addTalkValid() throws Exception {
        String title = "Pair Programming vs Noise";
        String duration = "35";
        mvc.perform(post("/addTalk")
                        .param("title", title)
                        .param("duration", duration))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"))
                .andExpect(view().name("redirect:/"));

        verify(service).saveTalk(title, Integer.parseInt(duration));
    }

    @Test
    void addTalkInvalid() throws Exception {
        mvc.perform(post("/addTalk")
                        .param("title", "")
                        .param("duration", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().string(containsString("Conference Track Management")))
                .andExpect(content().string(containsString("([a-zA-Z.():-]+\\s?)+")))
                .andExpect(content().string(containsString("The duration must be bigger than 0!")))
                .andExpect(content().string(containsString("The duration must be equal or smaller than 60!")))
                .andExpect(content().string(containsString("The duration must be integer!")))
                .andExpect(content().string(not(containsString("New talk was added"))));

        verify(service, never()).saveTalk(anyString(), any(Integer.class));
    }

    @Test
    void addTalkByInputStringValid() throws Exception {
        String inputString = "Spring lightning";
        mvc.perform(post("/addTalkByInputString")
                        .param("inputString", inputString))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"))
                .andExpect(view().name("redirect:/"));

        verify(service).addTalkByInputString(inputString);
    }

    @Test
    void addTalkByInputStringInvalid() throws Exception {
        mvc.perform(post("/addTalkByInputString")
                        .param("inputString", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().string(containsString("Conference Track Management")))
                .andExpect(content().string(containsString("([a-zA-Z.():-]+\\s?)+(\\d{1,2}min|lightning)")))
                .andExpect(content().string(not(containsString("New talk was added"))));

        verify(service, never()).addTalkByInputString(anyString());
    }
}