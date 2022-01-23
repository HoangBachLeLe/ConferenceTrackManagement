package bach.conference.track.management.form;

import bach.conference.track.management.model.Talk;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.validation.BindingResult;

@Data
public class InputStringForm {

    @Pattern(regexp = "([a-zA-Z.():-]+\\s?)+(\\d{1,2}min|lightning)", message = "([a-zA-Z.():-]+\\s?)+(\\d{1,2}min|lightning)")
    private final String inputString;

    public void validate(final BindingResult bindingResult, final List<Talk> allTalks) {
        if (bindingResult.hasErrors()) {
            return;
        }

        final List<String> strings = new ArrayList<>(Arrays.asList(inputString.split(" ")));
        strings.remove(strings.size() - 1);
        final String title = String.join(" ", strings);

        final List<String> titles = allTalks.stream().map(Talk::getTitle).toList(); //NOPMD
        if (titles.contains(title)) { //NOPMD
            bindingResult.rejectValue(
                    "inputString",
                    "",
                    "Talk '" + title + "' already exists! See #" + (titles.indexOf(title) + 1)
            );
        }
    }
}
