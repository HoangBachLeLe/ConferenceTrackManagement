package bach.conference.track.management.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;

@SuppressWarnings({"PMD.ShortClassName", "PMD.UnusedPrivateField"})
@Getter
public class Talk {

    @SuppressWarnings("PMD.ShortVariable")
    private @Id Long id;
    private final String title;
    private final Long duration;

    public Talk(final String title, final Long duration) {
        this.title = title;
        this.duration = duration;
    }
}
