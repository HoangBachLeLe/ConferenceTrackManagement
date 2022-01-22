package bach.conference.track.management.controller;

import bach.conference.track.management.service.TalkService;
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
}
