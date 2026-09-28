package de.sxpl.pialgra;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import tools.jackson.databind.ObjectMapper;

import tools.jackson.databind.JsonNode;

import java.time.OffsetDateTime;

import static de.sxpl.pialgra.Browser.studySession;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.docker.compose.enabled=false",
        "app.session.cookie.secure=false"
})
@Import(TestDatabaseConfiguration.class)
class StudySessionTests {
    @LocalServerPort int port;
    @Autowired ObjectMapper json;

    @Test
    void studySessionsAreSavedAndInvalidTimesAreRejected() throws Exception {
        var browser = new Browser(port, json).signIn();
        String category = browser.createCategory();
        String session = browser.write("POST", "/api/v1/study-sessions", studySession(category, "2000-01-01T11:00:00Z"), 201).path("uuid").asString();

        browser.write(
                "POST",
                "/api/v1/study-sessions",
                studySession(category, "2000-01-01T09:00:00Z"),
                400
        );

        JsonNode saved = browser.request("GET", "/api/v1/users/me/study-sessions", null, 200);

        assertThat(saved.size()).isEqualTo(1);

        assertThat(saved.get(0).path("uuid").asString()).isEqualTo(session);

        assertThat(saved.get(0).path("category").path("uuid").asString()).isEqualTo(category);

        assertThat(
                OffsetDateTime.parse(saved.get(0).path("startTime").asString()).toInstant()
        ).isEqualTo(OffsetDateTime.parse("2000-01-01T10:00:00Z").toInstant());

        assertThat(
                OffsetDateTime.parse(saved.get(0).path("endTime").asString()).toInstant()
        ).isEqualTo(OffsetDateTime.parse("2000-01-01T11:00:00Z").toInstant());
    }
}
