package de.sxpl.pialgra.controllers;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
class SessionSecurityIntegrationTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void browserCsrfFlowRotatesSessionAndInvalidatesOldTokens() throws Exception {
        var tokenResponse = mvc.perform(get("/api/auth/csrf")).andExpect(status().isOk()).andReturn().getResponse();
        Cookie anonymous = tokenResponse.getCookie("SESSION");
        String token = json.readTree(tokenResponse.getContentAsString()).get("token").asText();
        assertThat(anonymous).isNotNull();
        String username = "csrf-" + UUID.randomUUID().toString().substring(0, 12);
        String credentials = "{\"username\":\"" + username + "\",\"password\":\"password123\"}";
        mvc.perform(post("/api/auth/register").cookie(anonymous).header("X-CSRF-TOKEN", token)
            .contentType(MediaType.APPLICATION_JSON).content(credentials)).andExpect(status().isCreated());
        var loginResponse = mvc.perform(post("/api/auth/login").cookie(anonymous).header("X-CSRF-TOKEN", token)
            .contentType(MediaType.APPLICATION_JSON).content(credentials)).andExpect(status().isOk()).andReturn().getResponse();
        Cookie authenticated = loginResponse.getCookie("SESSION");
        assertThat(authenticated).isNotNull();
        assertThat(authenticated.getValue()).isNotEqualTo(anonymous.getValue());
        mvc.perform(get("/api/v1/users/me").cookie(anonymous)).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/users/me").cookie(authenticated)).andExpect(status().isOk());
        mvc.perform(post("/api/v1/categories").cookie(authenticated).header("X-CSRF-TOKEN", token)
            .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Invalid token\"}")).andExpect(status().isForbidden());
        var refreshed = mvc.perform(get("/api/auth/csrf").cookie(authenticated)).andExpect(status().isOk()).andReturn().getResponse();
        String newToken = json.readTree(refreshed.getContentAsString()).get("token").asText();
        mvc.perform(post("/api/v1/categories").cookie(authenticated).header("X-CSRF-TOKEN", newToken)
            .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Study\"}")).andExpect(status().isCreated());
        mvc.perform(post("/api/auth/logout").cookie(authenticated).header("X-CSRF-TOKEN", newToken)).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/users/me").cookie(authenticated)).andExpect(status().isUnauthorized());
    }

    @Test
    void registrationCannotReplaceAnExistingAccountAndRejectsOversizedUnicodePasswords() throws Exception {
        String name = "dup-" + UUID.randomUUID().toString().substring(0, 12);
        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"" + name + "\",\"password\":\"original123\"}")).andExpect(status().isCreated());
        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"" + name + "\",\"password\":\"replacement123\"}")).andExpect(status().isConflict());
        mvc.perform(post("/api/auth/login").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"" + name + "\",\"password\":\"original123\"}")).andExpect(status().isOk());
        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"unicode-test\",\"password\":\"" + "é".repeat(40) + "\"}")).andExpect(status().isBadRequest());
    }
}
