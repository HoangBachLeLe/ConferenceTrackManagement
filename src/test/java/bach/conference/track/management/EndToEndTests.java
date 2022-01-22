package bach.conference.track.management;

import static java.util.Map.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class EndToEndTests {

    @Autowired
    TestRestTemplate template;

    @Test
    void testWorkflow() {
        ResponseEntity<String> response;

        // check talks in database
        // make sure talk 'Spring Boot' (55 min) is not in table
        response = this.template.getForEntity("/", String.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody())
                .contains("Conference Track Management")
                .contains("Writing Fast Tests Against Enterprise Rails")
                .contains("60 min")
                .doesNotContain("Spring Boot")
                .doesNotContain("55 min");

        response = this.template.postForEntity(
                "/addTalk",
                formData(of(
                        "title", "Spring Boot",
                        "duration", "55"
                )),
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(FOUND);

        // check if talk 'Spring Boot' is in table
        response = this.template.getForEntity("/", String.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody())
                .contains("Spring Boot")
                .contains("55 min");

        // delete talk 'Spring Boot'
        long talkId = 20;
        response = template.postForEntity(
                "/deleteTalk/{id}", null, null, talkId
        );
        assertThat(response.getStatusCode()).isEqualTo(FOUND);

        // check if talk 'Spring Boot' is not in table
        response = this.template.getForEntity("/", String.class);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody())
                .doesNotContain("Spring Boot");
    }

    @Test
    void notFound() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.TEXT_HTML));
        HttpEntity<?> request = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = template.exchange("/invalidPage", HttpMethod.GET, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(response.getBody())
                .contains("An error has occurred!")
                .contains("Go back to main page.");
    }

    private static MultiValueMap<String, String> formData(Map<String, String> form) {
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        form.forEach(request::add);
        return request;
    }
}