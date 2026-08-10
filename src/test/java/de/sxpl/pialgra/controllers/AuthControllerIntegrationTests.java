package de.sxpl.pialgra.controllers;

import de.sxpl.pialgra.domain.dtos.auth.LoginDto;
import de.sxpl.pialgra.domain.dtos.user.CreateUserDto;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class AuthControllerIntegrationTests {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;

    private static int usernameCounter = 0;

    @Autowired
    public AuthControllerIntegrationTests(MockMvc mockMvc, ObjectMapper objectMapper, JdbcTemplate jdbcTemplate) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    private String nextUsername() {
        return "authuser" + usernameCounter++;
    }

    private void register(String username, String password) throws Exception {
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateUserDto(username, password))))
                .andExpect(status().isCreated());
    }

    private Cookie login(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginDto(username, password))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andReturn();

        Cookie sessionCookie = result.getResponse().getCookie("SESSION");
        assertThat(sessionCookie).isNotNull();
        return sessionCookie;
    }

    @Test
    public void registerReturnsCreatedUserWithoutPassword() throws Exception {
        String username = nextUsername();

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateUserDto(username, "password123"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    public void registerRejectsDuplicateUsername() throws Exception {
        String username = nextUsername();
        register(username, "password123");

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateUserDto(username, "password123"))))
                .andExpect(status().isConflict());
    }

    @Test
    public void registerRejectsInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateUserDto("ab", "short"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    public void registerDoesNotCreateAnAuthenticatedSession() throws Exception {
        String username = nextUsername();

        MvcResult result = mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateUserDto(username, "password123"))))
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(result.getResponse().getCookie("SESSION")).isNull();
    }

    @Test
    public void loginIssuesHttpOnlySessionCookieBackedByTheDatabase() throws Exception {
        String username = nextUsername();
        register(username, "password123");

        Cookie sessionCookie = login(username, "password123");

        assertThat(sessionCookie.isHttpOnly()).isTrue();
        assertThat(sessionCookie.getValue()).isNotBlank();

        Integer storedSessions = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM SPRING_SESSION WHERE PRINCIPAL_NAME = ?",
                Integer.class,
                username
        );
        assertThat(storedSessions).isEqualTo(1);
    }

    @Test
    public void loginRejectsWrongPassword() throws Exception {
        String username = nextUsername();
        register(username, "password123");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginDto(username, "wrong-password"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginRejectsUnknownUser() throws Exception {
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginDto("does-not-exist", "password123"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void meRequiresAnAuthenticatedSession() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void meReturnsTheLoggedInUserForASessionCookie() throws Exception {
        String username = nextUsername();
        register(username, "password123");
        Cookie sessionCookie = login(username, "password123");

        mockMvc.perform(get("/api/me").cookie(sessionCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    public void logoutInvalidatesTheSession() throws Exception {
        String username = nextUsername();
        register(username, "password123");
        Cookie sessionCookie = login(username, "password123");

        mockMvc.perform(post("/api/logout").cookie(sessionCookie))
                .andExpect(status().isNoContent());

        Integer storedSessions = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM SPRING_SESSION WHERE PRINCIPAL_NAME = ?",
                Integer.class,
                username
        );
        assertThat(storedSessions).isZero();

        mockMvc.perform(get("/api/me").cookie(sessionCookie))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void otherApiEndpointsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isUnauthorized());

        String username = nextUsername();
        register(username, "password123");
        Cookie sessionCookie = login(username, "password123");

        mockMvc.perform(get("/api/v1/users").cookie(sessionCookie))
                .andExpect(status().isOk());
    }
}
