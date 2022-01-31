package bach.conference.track.management.service;

import bach.conference.track.management.exception.TalkNotFoundException;
import bach.conference.track.management.model.Talk;
import bach.conference.track.management.repository.TalkRepository;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@SuppressWarnings("PMD.LongVariable")
@Service
public class TalkService {

    private static final LocalTime MORNING_SESSION_BEGIN = LocalTime.of(9, 0);
    private static final LocalTime MORNING_SESSION_END = LocalTime.of(12, 0);
    private static final LocalTime AFTERNOON_SESSION_BEGIN = LocalTime.of(13, 0);
    private static final LocalTime AFTERNOON_SESSION_END = LocalTime.of(17, 0);
    private static final LocalTime NETWORKING_EVENT_BEGIN = LocalTime.of(16, 0);

    private transient LocalTime lastTalkEnd = NETWORKING_EVENT_BEGIN;

    private static final String TIME_FORMAT = "hh:mm a";

    private final transient TalkRepository repository;

    public TalkService(final TalkRepository repository) {
        this.repository = repository;
    }

    public List<Talk> findAllTalks() {
        return repository.findAll();
    }

    public List<List<List<String>>> splitTalksIntoTracks(final List<Talk> talksFromDatabase) {
        lastTalkEnd = NETWORKING_EVENT_BEGIN;
        final List<Talk> allTalks = new ArrayList<>(talksFromDatabase);
        final List<List<List<String>>> tracks = new ArrayList<>();

        while (!allTalks.isEmpty()) {
            final List<List<String>> morningTracks =
                    this.splitTalksIntoSession(allTalks, MORNING_SESSION_BEGIN, MORNING_SESSION_END, "Lunch");
            final List<List<String>> afternoonTracks =
                    this.splitTalksIntoSession(allTalks, AFTERNOON_SESSION_BEGIN, AFTERNOON_SESSION_END, "Networking Event");
            morningTracks.addAll(afternoonTracks); //NOPMD
            tracks.add(morningTracks);
        }

        for (final List<List<String>> track : tracks) {
            for (final List<String> talks : track) {
                final String replaceTime = AFTERNOON_SESSION_END.format(DateTimeFormatter.ofPattern(TIME_FORMAT));
                if (talks.contains(replaceTime)) {
                    talks.set(
                            talks.indexOf(replaceTime),
                            lastTalkEnd.format(DateTimeFormatter.ofPattern(TIME_FORMAT))
                    );
                }
            }
        }

        return tracks;
    }

    @SuppressWarnings({"PMD.OnlyOneReturn", "PMD.DataflowAnomalyAnalysis", "PMD.LawOfDemeter", "PMD.ConfusingTernary"})
    private List<List<String>> splitTalksIntoSession(final List<Talk> talks, final LocalTime sessionBegin, final LocalTime sessionEnd,
            final String talkName) {
        final List<List<String>> session = new ArrayList<>();
        LocalTime sessionTime = sessionBegin;
        while (!talks.isEmpty()) {

            final Talk talk = talks.get(0);
            final Long duration = talk.getDuration();
            final LocalTime sessionTimeNew = sessionTime.plusMinutes(duration);

            if (!sessionTimeNew.isAfter(sessionEnd)) {
                session.add(List.of(
                        sessionTime.format(DateTimeFormatter.ofPattern(TIME_FORMAT)),
                        talk.getTitle(),
                        talk.getDuration().toString()
                ));
                talks.remove(0);
                sessionTime = sessionTimeNew;
            } else {
                session.add(new ArrayList<>(List.of(
                        sessionEnd.format(DateTimeFormatter.ofPattern(TIME_FORMAT)),
                        talkName,
                        ""
                )));
                lastTalkEnd = Stream.of(lastTalkEnd, sessionTime).max(LocalTime::compareTo).get();
                return session;
            }
        }
        session.add(new ArrayList<>(List.of(
                sessionEnd.format(DateTimeFormatter.ofPattern(TIME_FORMAT)),
                talkName,
                ""
        )));
        lastTalkEnd = Stream.of(lastTalkEnd, sessionTime).max(LocalTime::compareTo).get();
        return session;
    }

    public Talk getTalk(final long talkId) {
        return repository.findById(talkId) //NOPMD
                .orElseThrow(() -> new TalkNotFoundException("Talk by id " + talkId + " was not found"));
    }

    public void deleteTalk(final long talkId) {
        repository.deleteById(talkId);
    }

    public void deleteAllTalks() {
        repository.deleteAll();
    }

    public void saveTalk(final String title, final long duration) {
        repository.save(new Talk(title, duration));
    }

    public void addTalkByInputString(final String inputString) {
        final List<String> strings = new ArrayList<>(Arrays.asList(inputString.split(" ")));
        final String durationString = strings.remove(strings.size() - 1);

        long duration;
        if ("lightning".equals(durationString)) {
            duration = 5L;
        } else {
            duration = Integer.parseInt(
                    durationString.replace("min", "")
            );
        }

        final String title = String.join(" ", strings);

        this.saveTalk(title, duration);
    }
}
