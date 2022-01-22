package bach.conference.track.management.controller;

import bach.conference.track.management.form.TalkForm;
import bach.conference.track.management.model.Talk;
import bach.conference.track.management.service.TalkService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collections;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
public class TalkController {

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    private final TalkService service;

    @GetMapping("/")
    public String getIndex(final Model model) {
        model.addAttribute("allTalks", service.findAllTalks());
        model.addAttribute("talkForm", new TalkForm("", ""));
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

    @PostMapping("/deleteTalk/{id}")
    public String deleteTalk(@PathVariable("id") final long talkId, final RedirectAttributes redirectAttr) {
        final Talk talk = service.getTalk(talkId);
        service.deleteTalk(talkId);
        redirectAttr.addFlashAttribute(
                "message",
                "Talk '" + talk.getTitle() + "' (" + talk.getDuration() + " min) was deleted." //NOPMD
        );
        return "redirect:/";
    }

    @SuppressWarnings("PMD.OnlyOneReturn")
    @PostMapping("/addTalk")
    public String addTalk(@Valid final TalkForm form, final BindingResult bindingResult, final Model model,
            final RedirectAttributes redirectAttr) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allTalks", service.findAllTalks());
            return "index";
        }

        service.saveTalk(
                form.getTitle(),
                Integer.parseInt(form.getDuration())
        );

        redirectAttr.addFlashAttribute("message",
                "New talk was added: "
                        + form.getTitle() + " ("
                        + form.getDuration() + " min)"
        );
        return "redirect:/";
    }
}
