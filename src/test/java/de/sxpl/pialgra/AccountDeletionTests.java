package de.sxpl.pialgra;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static de.sxpl.pialgra.Browser.credentials;
import static de.sxpl.pialgra.Browser.studySession;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.docker.compose.enabled=false",
        "app.session.cookie.secure=false"
})
@Import(TestDatabaseConfiguration.class)
class AccountDeletionTests {
    @LocalServerPort int port;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate database;


    @Test
    void deletingAnAccountRemovesItsDataAndRevokesAllLogins() throws Exception {
        var browser = new Browser(port, json);
        String username = browser.register();
        browser.login(username);
        UUID uuid = database.queryForObject("select uuid from users where username = ?", UUID.class, username);
        String category = browser.createCategory();
        browser.write("POST", "/api/v1/study-sessions",
                studySession(category, "2000-01-01T11:00:00Z"), 201);
        var secondLogin = new Browser(port, json);
        secondLogin.login(username);

        browser.write("DELETE", "/api/v1/users/me", null, 204);
        browser.request("GET", "/api/v1/users/me", null, 401);
        secondLogin.request("GET", "/api/v1/users/me", null, 401);
        secondLogin.write("POST", "/api/auth/login", credentials(username), 401);

        assertThat(database.queryForObject("select count(*) from users where uuid = ?", Long.class, uuid)).isZero();
        assertThat(database.queryForObject("select count(*) from categories where user_id = ?", Long.class, uuid)).isZero();
        assertThat(database.queryForObject("select count(*) from study_sessions where user_id = ?", Long.class, uuid)).isZero();
    }
}
