package bach.conference.track.management.form;

import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
public class InputStringForm {

    @Pattern(regexp = "([a-zA-Z]+\\s?)+(\\d{1,2}min|lightning)", message = "([a-zA-Z]+\\s?)+\\s(\\d{1,2}min)|(lightning)")
    private final String inputString;
}
