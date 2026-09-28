package de.sxpl.pialgra;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class Browser {
    private final int port;
    private final ObjectMapper json;

    Browser(int port, ObjectMapper json) {
        this.port = port;
        this.json = json;
    }

    Browser signIn() throws Exception {
        login(register());
        return this;
    }

    static Map<String, String> credentials(String username) {
        return Map.of("username", username, "password", "test-password");
    }

    static Map<String, String> studySession(String category, String end) {
        return Map.of("categoryUuid", category, "startTime", "2000-01-01T12:00:00+02:00", "endTime", end);
    }

    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
            .connectTimeout(Duration.ofSeconds(10)).build();
    private String csrfHeader;
    private String csrfToken;

    void clearCsrfToken() {
        csrfToken = null;
    }

    String register() throws Exception {
        String username = "user-" + UUID.randomUUID().toString().substring(0, 20);
        write("POST", "/api/auth/register", credentials(username), 201);
        return username;
    }

    void login(String username) throws Exception {
        write("POST", "/api/auth/login", credentials(username), 200);
    }

    String createCategory() throws Exception {
        return write("POST", "/api/v1/categories",
                Map.of("name", "Reading", "color", "#123456"), 201).path("uuid").asString();
    }

    JsonNode write(String method, String path, Object body, int expectedStatus) throws Exception {
        JsonNode csrf = request("GET", "/api/auth/csrf", null, 200);
        csrfHeader = csrf.path("headerName").asString();
        csrfToken = csrf.path("token").asString();
        return request(method, path, body, expectedStatus);
    }

    JsonNode request(String method, String path, Object body, int expectedStatus) throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .method(method, body == null ? HttpRequest.BodyPublishers.noBody()
                        : HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body)));
        if (csrfToken != null && !method.equals("GET")) {
            request.header(csrfHeader, csrfToken);
        }
        var response = client.send(request.build(), HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).as("%s %s: %s", method, path, response.body())
                .isEqualTo(expectedStatus);
        return response.body().isBlank() ? json.getNodeFactory().nullNode() : json.readTree(response.body());
    }
}
