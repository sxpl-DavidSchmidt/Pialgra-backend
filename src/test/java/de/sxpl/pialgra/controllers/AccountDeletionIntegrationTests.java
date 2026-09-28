package de.sxpl.pialgra.controllers;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
class AccountDeletionIntegrationTests {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Autowired ObjectMapper json;

    private String register() throws Exception {
        String name = "erase-" + UUID.randomUUID().toString().substring(0, 12);
        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(credentials(name))).andExpect(status().isCreated());
        return name;
    }

    private String credentials(String name) {
        return "{\"username\":\"" + name + "\",\"password\":\"password123\"}";
    }

    private Cookie login(String name) throws Exception {
        return mvc.perform(post("/api/auth/login").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(credentials(name))).andExpect(status().isOk()).andReturn().getResponse().getCookie("SESSION");
    }

    @Test
    void deletesOnlyOwnDataAndRevokesEverySession() throws Exception {
        String name = register();
        String other = register();
        Cookie first = login(name), second = login(name), otherSession = login(other);
        UUID category = UUID.randomUUID();
        jdbc.update("INSERT INTO categories (uuid, user_username, name) VALUES (?, ?, ?)", category, name, "Study");
        jdbc.update("INSERT INTO study_sessions (uuid, user_id, category_id, start_time, end_time) VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", UUID.randomUUID(), name, category);
        jdbc.update("INSERT INTO study_sessions (uuid, user_id, start_time, end_time) VALUES (?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", UUID.randomUUID(), name);
        mvc.perform(delete("/api/v1/users/me").cookie(first)).andExpect(status().isForbidden());
        var tokenResponse = mvc.perform(get("/api/auth/csrf").cookie(first)).andReturn().getResponse();
        String token = json.readTree(tokenResponse.getContentAsString()).get("token").asText();
        var response = mvc.perform(delete("/api/v1/users/me").cookie(first).header("X-CSRF-TOKEN", token))
            .andExpect(status().isNoContent()).andReturn().getResponse();
        assertThat(response.getCookie("SESSION").getMaxAge()).isZero();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE username = ?", Integer.class, name)).isZero();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM categories WHERE user_username = ?", Integer.class, name)).isZero();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM study_sessions WHERE user_id = ?", Integer.class, name)).isZero();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM SPRING_SESSION WHERE PRINCIPAL_NAME = ?", Integer.class, name)).isZero();
        mvc.perform(get("/api/v1/users/me").cookie(first)).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/users/me").cookie(second)).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/users/me").cookie(otherSession)).andExpect(status().isOk());
        mvc.perform(post("/api/auth/login").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(credentials(name))).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(credentials(name))).andExpect(status().isCreated());
        mvc.perform(get("/api/v1/users/me").cookie(second)).andExpect(status().isUnauthorized());
    }

    @Test
    void anonymousDeletionIsRejected() throws Exception {
        mvc.perform(delete("/api/v1/users/me").with(csrf())).andExpect(status().isUnauthorized());
    }
}
