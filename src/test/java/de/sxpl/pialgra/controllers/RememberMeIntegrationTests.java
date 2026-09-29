package de.sxpl.pialgra.controllers;

import de.sxpl.pialgra.security.LoginSessionPolicy;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.session.SessionRepository;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.web.servlet.MockMvc;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest(properties = "app.session.cookie.secure=true")
@AutoConfigureMockMvc
@SuppressWarnings({"rawtypes", "unchecked"})
class RememberMeIntegrationTests {
    @Autowired MockMvc mvc;
    @Autowired SessionRepository sessions;

    private String credentials() throws Exception {
        String body = "{\"username\":\"remember-" + UUID.randomUUID().toString().substring(0, 10)
                + "\",\"password\":\"password123\"}";
        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(body)).andExpect(status().isCreated());
        return body;
    }

    private String remembered(String credentials) {
        return credentials.substring(0, credentials.length() - 1)
                + ",\"rememberMe\":true}";
    }

    private Cookie login(String body, Cookie... previous) throws Exception {
        var request = post("/api/auth/login").with(csrf());
        if (previous.length > 0) request.cookie(previous);
        return mvc.perform(request
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andReturn().getResponse().getCookie("SESSION");
    }

    private String id(Cookie cookie) {
        return new String(Base64.getDecoder().decode(cookie.getValue()), StandardCharsets.UTF_8);
    }

    @Test
    void defaultAndAnonymousCookiesAreNonpersistent() throws Exception {
        Cookie anonymous = mvc.perform(get("/api/auth/csrf")).andExpect(status().isOk())
                .andReturn().getResponse().getCookie("SESSION");
        assertThat(anonymous.getMaxAge()).isEqualTo(-1);
        Cookie cookie = login(credentials());
        assertThat(cookie.getMaxAge()).isEqualTo(-1);
        var stored = sessions.findById(id(cookie));
        assertThat(stored).isNotNull();
        assertThat((Boolean) stored.getAttribute(LoginSessionPolicy.REMEMBER_ME)).isFalse();
        long recorded = stored.getAttribute(LoginSessionPolicy.RECORDED_AT);
        long deadline = stored.getAttribute(LoginSessionPolicy.EXPIRES_AT);
        assertThat(deadline - recorded).isEqualTo(LoginSessionPolicy.DEFAULT_SECONDS * 1000L);
    }

    @Test
    void consentPersistsWithPrincipalAndLogoutRevokesCookieAndSession() throws Exception {
        Cookie cookie = login(remembered(credentials()));
        assertThat(cookie.getMaxAge()).isBetween(LoginSessionPolicy.REMEMBER_SECONDS - 5, LoginSessionPolicy.REMEMBER_SECONDS);
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getSecure()).isTrue();
        assertThat(cookie.getAttribute("SameSite")).isEqualTo("Lax");
        var stored = sessions.findById(id(cookie));
        assertThat(stored).isNotNull();
        assertThat((Boolean) stored.getAttribute(LoginSessionPolicy.REMEMBER_ME)).isTrue();
        SecurityContext context = stored.getAttribute("SPRING_SECURITY_CONTEXT");
        assertThat(context.getAuthentication().getName()).startsWith("remember-");
        long recorded = stored.getAttribute(LoginSessionPolicy.RECORDED_AT);
        long deadline = stored.getAttribute(LoginSessionPolicy.EXPIRES_AT);
        assertThat(deadline - recorded).isEqualTo(LoginSessionPolicy.REMEMBER_SECONDS * 1000L);
        mvc.perform(get("/api/v1/users/me").cookie(cookie)).andExpect(status().isOk());
        assertThat((Long) sessions.findById(id(cookie)).getAttribute(LoginSessionPolicy.EXPIRES_AT)).isEqualTo(deadline);
        var response = mvc.perform(post("/api/auth/logout").cookie(cookie).with(csrf()))
                .andExpect(status().isNoContent()).andReturn().getResponse();
        assertThat(response.getCookie("SESSION").getMaxAge()).isZero();
        assertThat(sessions.findById(id(cookie))).isNull();
        mvc.perform(get("/api/v1/users/me").cookie(cookie)).andExpect(status().isUnauthorized());
    }

    @Test
    void uncheckedLoginReplacesPersistentCookieAndClearsPreviousChoice() throws Exception {
        String body = credentials();
        Cookie old = login(remembered(body));
        Cookie replacement = login(body, old);
        assertThat(replacement.getMaxAge()).isEqualTo(-1);
        assertThat(replacement.getValue()).isNotEqualTo(old.getValue());
        assertThat((Boolean) sessions.findById(id(replacement)).getAttribute(LoginSessionPolicy.REMEMBER_ME)).isFalse();
        mvc.perform(get("/api/v1/users/me").cookie(old)).andExpect(status().isUnauthorized());
    }

    @Test
    void expiredSessionsAreRejectedEvenWhenCookieIsReplayed() throws Exception {
        for (boolean remember : new boolean[]{false, true}) {
            String body = credentials();
            Cookie cookie = login(remember ? remembered(body) : body);
            var stored = sessions.findById(id(cookie));
            stored.setAttribute(LoginSessionPolicy.EXPIRES_AT, System.currentTimeMillis() - 1);
            sessions.save(stored);
            mvc.perform(get("/api/v1/users/me").cookie(cookie)).andExpect(status().isUnauthorized());
            assertThat(sessions.findById(id(cookie))).isNull();
        }
    }

    @Test
    void failedLoginsDoNotIssuePersistentCookies() throws Exception {
        String body = credentials();
        var response = mvc.perform(post("/api/auth/login").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(remembered(body).replace("password123", "wrong-password")))
                .andExpect(status().isUnauthorized()).andReturn().getResponse();
        Cookie cookie = response.getCookie("SESSION");
        assertThat(cookie == null || cookie.getMaxAge() <= 0).isTrue();
    }
}
