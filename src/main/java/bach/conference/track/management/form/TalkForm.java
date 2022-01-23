package bach.conference.track.management.form;

import bach.conference.track.management.model.Talk;
import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.validation.BindingResult;

@Data
public class TalkForm {

    @Pattern(regexp = "([a-zA-Z]+\\s?)+", message = "([a-zA-Z]+?\\s)+")
    private final String title;

    @Min(value = 1, message = "The duration must be bigger than 0!")
    @Max(value = 60, message = "The duration must be equal or smaller than 60!")
    @Pattern(regexp = "-?\\d+", message = "The duration must be integer!")
    private final String duration;

    public void validate(final BindingResult bindingResult, final List<Talk> allTalks) {
        if (bindingResult.hasErrors()) {
            return;
        }

        final List<String> titles = allTalks.stream().map(Talk::getTitle).toList(); //NOPMD
        if (titles.contains(this.title)) { //NOPMD
            bindingResult.rejectValue(
                    "title",
                    "",
                    "Talk '" + this.title + "' already exists! See #" + (titles.indexOf(this.title) + 1)
            );
        }
    }
}
