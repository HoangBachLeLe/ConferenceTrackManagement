package bach.conference.track.management.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

        verify(service).splitTalksIntoTracks();
    }
}