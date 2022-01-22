package bach.conference.track.management.form;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TalkForm {

    @Pattern(regexp = "([a-zA-Z]+\\s?)+", message = "([a-zA-Z]+?\\s)+")
    private final String title;

    @Min(value = 1, message = "The duration must be bigger than 0!")
    @Max(value = 60, message = "The duration must be equal or smaller than 60!")
    @Pattern(regexp = "-?\\d+", message = "The duration must be integer!")
    private final String duration;
}
