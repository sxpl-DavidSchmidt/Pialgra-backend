package de.sxpl.pialgra;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static de.sxpl.pialgra.Browser.studySession;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.docker.compose.enabled=false",
        "app.session.cookie.secure=false"
})
@Import(TestDatabaseConfiguration.class)
class DataIsolationTests {
    @LocalServerPort
    int port;
    @Autowired
    ObjectMapper json;


    @Test
    void usersCannotSeeOrChangeEachOthersData() throws Exception {
        var owner = new Browser(port, json).signIn();
        var other = new Browser(port, json).signIn();

        String category = owner.createCategory();
        String session = owner.write("POST", "/api/v1/study-sessions", studySession(category, "2000-01-01T11:00:00Z"), 201).path("uuid").asString();

        assertThat(
                other.request("GET", "/api/v1/users/me/categories", null, 200)
                        .valueStream()
                        .map(node -> node.path("uuid").asString())
                        .toList()
        ).doesNotContain(category);

        assertThat(
                other.request("GET", "/api/v1/users/me/study-sessions", null, 200).size()
        ).isZero();

        other.write(
                "PUT",
                "/api/v1/categories/" + category,
                Map.of("name", "Stolen", "color", "#654321"),
                404
        );

        other.write(
                "DELETE",
                "/api/v1/categories/" + category,
                null,
                404
        );

        other.write(
                "POST",
                "/api/v1/study-sessions",
                studySession(category, "2000-01-01T11:00:00Z"),
                404
        );

        other.write(
                "PUT",
                "/api/v1/study-sessions/" + session,
                studySession(other.createCategory(), "2000-01-01T11:00:00Z"),
                404
        );

        other.write(
                "DELETE",
                "/api/v1/study-sessions/" + session,
                null,
                404
        );

        assertThat(
                owner.request("GET", "/api/v1/users/me/categories", null, 200)
                        .valueStream()
                        .map(node -> node.path("name").asString())
                        .toList()
        ).contains("Reading").doesNotContain("Stolen");

        assertThat(
                owner.request("GET", "/api/v1/users/me/study-sessions", null, 200)
                        .get(0)
                        .path("uuid")
                        .asString()
        ).isEqualTo(session);
    }
}
