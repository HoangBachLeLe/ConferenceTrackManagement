package bach.conference.track.management.repository;

import bach.conference.track.management.model.Talk;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface TalkRepository extends CrudRepository<Talk, Long> {

    @Override
    List<Talk> findAll();

}
