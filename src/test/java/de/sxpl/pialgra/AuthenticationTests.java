package de.sxpl.pialgra;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static de.sxpl.pialgra.Browser.credentials;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.docker.compose.enabled=false",
        "app.session.cookie.secure=false"
})
@Import(TestDatabaseConfiguration.class)
class AuthenticationTests {
    @LocalServerPort int port;
    @Autowired ObjectMapper json;

    @Test
    void registrationLoginAndLogoutProtectPrivateEndpoints() throws Exception {
        Browser browser = new Browser(port, json);
        browser.request("GET", "/api/v1/users/me", null, 401);
        browser.request("POST", "/api/auth/register", credentials("blocked"), 401);

        String username = browser.register();
        browser.write("POST", "/api/auth/login", Map.of("username", username, "password", "wrong-password"), 401);

        browser.login(username);
        assertThat(
                browser.request("GET", "/api/v1/users/me", null, 200).path("username").asString()
        ).isEqualTo(username);

        browser.clearCsrfToken();
        browser.request("POST", "/api/v1/categories", Map.of("name", "Blocked"), 403);

        browser.write("POST", "/api/auth/logout", null, 204);
        browser.request("GET", "/api/v1/users/me", null, 401);
    }
}
