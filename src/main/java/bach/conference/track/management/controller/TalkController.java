package bach.conference.track.management.controller;

import bach.conference.track.management.model.Talk;
import bach.conference.track.management.service.TalkService;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class TalkController {

    private final TalkService service;

    @GetMapping("/")
    public String getIndex(final Model model) {
        model.addAttribute("allTalks", service.findAllTalks());
        return "index";
    }

    @GetMapping("/tracks")
    public String tracksWithTalks(final Model model) {
        model.addAttribute("tracksWithTalks", service.splitTalksIntoTracks(
                service.findAllTalks()
        ));
        return "tracks";
    }

    @GetMapping("/randomOrder")
    public String randomOrder(final Model model) {
        final List<Talk> allTalks = service.findAllTalks();
        Collections.shuffle(allTalks);
        model.addAttribute("tracksWithTalks", service.splitTalksIntoTracks(
                allTalks
        ));
        return "tracks";
    }
}
