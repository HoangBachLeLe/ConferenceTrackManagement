package bach.conference.track.management.exception;

@SuppressWarnings("PMD")
public class TalkNotFoundException extends RuntimeException {

    public TalkNotFoundException(final String message) {
        super(message);
    }
}
