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

class InputStringFormTest {

    List<Talk> repository = List.of(
            new Talk("User Interface CSS in Rails Apps", 30L),
            new Talk("Pair Programming vs Noise", 30L),
            new Talk("Ruby on Rails Legacy App Maintenance", 40L)
    );

    @Test
    void valid() {
        InputStringForm form = new InputStringForm(
                "Spring Magic 30min"
        );
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        form.validate(bindingResult, this.repository);

        verify(bindingResult, never()).rejectValue(any(), any(), any());
    }

    @DisplayName("talk already exists")
    @Test
    void invalid() {
        InputStringForm form = new InputStringForm(
                "Pair Programming vs Noise 30min"
        );
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        form.validate(bindingResult, this.repository);


        verify(bindingResult).rejectValue(
                "inputString",
                "",
                "Talk 'Pair Programming vs Noise' already exists! See #2"
        );
    }
}