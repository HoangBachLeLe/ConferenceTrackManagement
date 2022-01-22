package bach.conference.track.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bach.conference.track.management.exception.TalkNotFoundException;
import bach.conference.track.management.model.Talk;
import bach.conference.track.management.repository.TalkRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TalkServiceTest {

    @Mock
    private TalkRepository repository;

    @InjectMocks
    private TalkService service;

    @Test
    void findAllTalks() {
        service.findAllTalks();

        verify(repository).findAll();
    }

    @DisplayName("Test inputs are as on pdf file")
    @Test
    void splitTalksIntoTracks1() {
        List<Talk> allTalks = List.of(
                new Talk("Writing Fast Tests Against Enterprise Rails", 60L),
                new Talk("Overdoing it in Python", 45L),
                new Talk("Lua for the Masses", 30L),
                new Talk("Ruby Errors from Mismatched Gem Versions", 45L),
                new Talk("Ruby on Rails: Why We Should Move On", 60L),
                new Talk("Common Ruby Errors", 45L),
                new Talk("Pair Programming vs Noise", 45L),
                new Talk("Programming in the Boondocks of Seattle", 30L),
                new Talk("Ruby vs. Clojure for Back-End Development", 30L),
                new Talk("User Interface CSS in Rails Apps", 30L),
                new Talk("Communicating Over Distance", 60L),
                new Talk("Rails Magic", 60L),
                new Talk("Woah", 30L),
                new Talk("Sit Down and Write", 30L),
                new Talk("Accounting-Driven Development", 45L),
                new Talk("Clojure Ate Scala (on my project)", 45L),
                new Talk("A World Without HackerNews", 30L),
                new Talk("Ruby on Rails Legacy App Maintenance", 60L),
                new Talk("Rails for Python Developers", 5L)
        );

        List<List<String>> tracks = service.splitTalksIntoTracks(allTalks)
                .stream()
                .flatMap(Collection::stream)
                .toList();

        assertThat(tracks).containsExactly(
                List.of("09:00 AM", "Writing Fast Tests Against Enterprise Rails", "60"),
                List.of("10:00 AM", "Overdoing it in Python", "45"),
                List.of("10:45 AM", "Lua for the Masses", "30"),
                List.of("11:15 AM", "Ruby Errors from Mismatched Gem Versions", "45"),
                List.of("12:00 PM", "Lunch", ""),
                List.of("01:00 PM", "Ruby on Rails: Why We Should Move On", "60"),
                List.of("02:00 PM", "Common Ruby Errors", "45"),
                List.of("02:45 PM", "Pair Programming vs Noise", "45"),
                List.of("03:30 PM", "Programming in the Boondocks of Seattle", "30"),
                List.of("04:00 PM", "Ruby vs. Clojure for Back-End Development", "30"),
                List.of("04:30 PM", "User Interface CSS in Rails Apps", "30"),
                List.of("05:00 PM", "Networking Event", ""),
                List.of("09:00 AM", "Communicating Over Distance", "60"),
                List.of("10:00 AM", "Rails Magic", "60"),
                List.of("11:00 AM", "Woah", "30"),
                List.of("11:30 AM", "Sit Down and Write", "30"),
                List.of("12:00 PM", "Lunch", ""),
                List.of("01:00 PM", "Accounting-Driven Development", "45"),
                List.of("01:45 PM", "Clojure Ate Scala (on my project)", "45"),
                List.of("02:30 PM", "A World Without HackerNews", "30"),
                List.of("03:00 PM", "Ruby on Rails Legacy App Maintenance", "60"),
                List.of("04:00 PM", "Rails for Python Developers", "5"),
                List.of("05:00 PM", "Networking Event", "")
        );
    }

    @DisplayName("morning talk sessions must finish by 12 noon")
    @Test
    void splitTalksIntoTracks2() {
        List<Talk> allTalks = List.of(
                new Talk("Talk 1", 60L),
                new Talk("Talk 2", 45L),
                new Talk("Talk 3", 30L),
                new Talk("Talk 4", 50L)
        );

        List<List<String>> tracks = service.splitTalksIntoTracks(allTalks)
                .stream()
                .flatMap(Collection::stream)
                .toList();

        assertThat(tracks).containsExactly(
                List.of("09:00 AM", "Talk 1", "60"),
                List.of("10:00 AM", "Talk 2", "45"),
                List.of("10:45 AM", "Talk 3", "30"),
                List.of("12:00 PM", "Lunch", ""),
                List.of("01:00 PM", "Talk 4", "50"),
                List.of("04:00 PM", "Networking Event", "")
        );
    }

    @DisplayName("only one talk")
    @Test
    void splitTalksIntoTracks3() {
        List<Talk> allTalks = List.of(
                new Talk("Talk 1", 30L)
        );

        List<List<String>> tracks = service.splitTalksIntoTracks(allTalks)
                .stream()
                .flatMap(Collection::stream)
                .toList();

        assertThat(tracks).containsExactly(
                List.of("09:00 AM", "Talk 1", "30"),
                List.of("12:00 PM", "Lunch", ""),
                List.of("04:00 PM", "Networking Event", "")
        );
    }

    @DisplayName("Talk is found")
    @Test
    void getTalk1() {
        long talkId = 1L;
        Talk talk = new Talk("Talk 1", 30L);
        when(repository.findById(talkId)).thenReturn(
                Optional.of(talk)
        );

        Talk result = service.getTalk(talkId);

        verify(repository).findById(talkId);
        assertThat(result).isEqualTo(talk);
    }

    @DisplayName("Talk is not found")
    @Test
    void getTalk2() {
        long talkId = 1L;

        assertThrows(TalkNotFoundException.class,
                () -> service.getTalk(talkId)
        );
    }

    @Test
    void deleteTalk() {
        long talkId = 1L;

        service.deleteTalk(talkId);

        verify(repository).deleteById(talkId);
    }

    @Test
    void saveTalk() {
        String title = "Talk 1";
        long duration = 40L;

        service.saveTalk(title, duration);

        verify(repository).save(any(Talk.class));
    }

    @Test
    void addTalkByInputString() {
        String inputString = "Spring 40min";

        service.addTalkByInputString(inputString);

        verify(repository).save(any(Talk.class));
    }
}