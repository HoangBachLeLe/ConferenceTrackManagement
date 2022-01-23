package bach.conference.track.management.form;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bach.conference.track.management.model.Talk;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;

class TalkFormTest {

    List<Talk> repository = List.of(
            new Talk("Writing Fast Tests Against Enterprise Rails", 50L),
            new Talk("Common Ruby Errors", 30L),
            new Talk("Rails Magic", 30L)
    );

    @Test
    void valid() {
        TalkForm form = new TalkForm(
                "Spring Magic",
                "30L"
        );
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        form.validate(bindingResult, this.repository);

        verify(bindingResult, never()).rejectValue(any(), any(), any());
    }

    @DisplayName("talk already exists")
    @Test
    void invalid() {
        TalkForm form = new TalkForm(
                "Rails Magic",
                "30L"
        );
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        form.validate(bindingResult, this.repository);

        verify(bindingResult).rejectValue(
                "title",
                "",
                "Talk '" + form.getTitle() + "' already exists! See #"
                        + (repository.stream().map(Talk::getTitle).toList().indexOf(form.getTitle()) + 1)
        );
    }
}