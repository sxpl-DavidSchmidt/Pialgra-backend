package de.sxpl.pialgra.controllers;

import de.sxpl.pialgra.domain.entities.*;
import de.sxpl.pialgra.repositories.*;
import de.sxpl.pialgra.service.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser("security-owner")
class SecurityIntegrationTests {
    @Autowired MockMvc mvc;
    @Autowired UserService users;
    @Autowired CategoryService categories;
    @Autowired StudySessionRepository sessions;
    @Autowired CategoryRepository categoryRepository;
    @Autowired EntityManager entityManager;
    private CategoryEntity own;
    private CategoryEntity other;

    @BeforeEach
    void setup() {
        for (String name : new String[]{"security-owner", "security-other"}) {
            UserEntity user = new UserEntity();
            user.setUsername(name);
            user.setPassword("password123");
            users.createUser(user);
        }
        own = categories.createCategory(new CategoryEntity(null, null, "Own", null), "security-owner");
        other = categories.createCategory(new CategoryEntity(null, null, "Other", null), "security-other");
    }

    @Test
    void categoryColorIsPersistedAndReturned() throws Exception {
        mvc.perform(post("/api/v1/categories").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Colored\",\"color\":\"#12AbEF\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.color").value("#12AbEF"));
        entityManager.flush();
        entityManager.clear();
        assertThat(categories.findByUsername("security-owner"))
                .filteredOn(category -> category.getName().equals("Colored"))
                .singleElement().extracting(CategoryEntity::getColor).isEqualTo("#12AbEF");
        mvc.perform(get("/api/v1/users/me/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == 'Colored')].color").value(org.hamcrest.Matchers.contains("#12AbEF")));
    }

    @Test
    void invalidCategoryColorsReturnFieldValidationErrors() throws Exception {
        for (String color : new String[]{"", "red", "#123", "123456", "#GGGGGG", "#12345678"}) {
            mvc.perform(post("/api/v1/categories").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"Invalid\",\"color\":\"" + color + "\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0].field").value("color"));
        }
    }

    @Test
    void categoryColorRemainsOptional() throws Exception {
        mvc.perform(post("/api/v1/categories").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Uncolored\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.color").value(org.hamcrest.Matchers.nullValue()));
    }

    private String body(UUID category, String start, String end) {
        return "{\"categoryUuid\":\"" + category + "\",\"startTime\":\"" + start + "\",\"endTime\":\"" + end + "\"}";
    }

    @Test
    void sessionEditingAndDeletionPersistAndProtectOwnership() throws Exception {
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 10, 0);
        StudySessionEntity session = sessions.save(new StudySessionEntity(null, own.getUser(), own, start, start.plusMinutes(25)));
        StudySessionEntity foreign = sessions.save(new StudySessionEntity(null, other.getUser(), other, start, start.plusMinutes(25)));
        String path = "/api/v1/study-sessions/" + session.getUuid();
        String valid = body(own.getUuid(), "2026-01-01T13:00:00+02:00", "2026-01-01T13:30:00+02:00");
        mvc.perform(put(path).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(valid))
                .andExpect(status().isOk()).andExpect(jsonPath("$.startTime").value("2026-01-01T11:00:00Z"))
                .andExpect(jsonPath("$.endTime").value("2026-01-01T11:30:00Z"));
        for (UUID id : new UUID[]{foreign.getUuid(), UUID.randomUUID()}) {
            mvc.perform(put("/api/v1/study-sessions/{uuid}", id).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(valid))
                    .andExpect(status().isNotFound());
            mvc.perform(delete("/api/v1/study-sessions/{uuid}", id).with(csrf())).andExpect(status().isNotFound());
        }
        mvc.perform(put(path).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(body(other.getUuid(), "2026-01-01T10:00:00Z", "2026-01-01T10:30:00Z")))
                .andExpect(status().isNotFound());
        mvc.perform(put(path).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(body(own.getUuid(), "2026-01-01T10:00:00Z", "2026-01-01T09:30:00Z")))
                .andExpect(status().isBadRequest());
        mvc.perform(put(path).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        mvc.perform(put(path).contentType(MediaType.APPLICATION_JSON).content(valid)).andExpect(status().isForbidden());
        mvc.perform(delete(path)).andExpect(status().isForbidden());
        mvc.perform(put(path).with(anonymous()).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(valid))
                .andExpect(status().isUnauthorized());
        mvc.perform(delete(path).with(anonymous()).with(csrf())).andExpect(status().isUnauthorized());
        mvc.perform(put(path).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"categoryUuid\":null,\"startTime\":\"2026-01-01T11:00:00Z\",\"endTime\":\"2026-01-01T11:30:00Z\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.category").value(org.hamcrest.Matchers.nullValue()));
        entityManager.flush();
        entityManager.clear();
        assertThat(sessions.findById(session.getUuid()).orElseThrow().getCategory()).isNull();
        assertThat(sessions.findById(session.getUuid()).orElseThrow().getStartTime()).isEqualTo(start.plusHours(1));
        mvc.perform(delete(path).with(csrf())).andExpect(status().isNoContent());
        entityManager.flush();
        entityManager.clear();
        assertThat(sessions.findById(session.getUuid())).isEmpty();
        assertThat(sessions.findById(foreign.getUuid())).isPresent();
        assertThat(categoryRepository.findById(own.getUuid())).isPresent();
        assertThat(users.findByUsername("security-owner")).isPresent();
    }

    @Test
    void foreignAndMissingCategoriesAreRejectedWithoutLeakingThem() throws Exception {
        for (UUID category : new UUID[]{other.getUuid(), UUID.randomUUID()}) {
            mvc.perform(post("/api/v1/study-sessions").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(body(category, "2026-01-01T10:00:00Z", "2026-01-01T10:25:00Z"))).andExpect(status().isNotFound());
        }
        assertThat(sessions.findByUser(users.findByUsername("security-owner").orElseThrow())).isEmpty();
    }

    @Test
    void validatesInputsAndPreservesUtcInstants() throws Exception {
        mvc.perform(post("/api/v1/study-sessions").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(body(own.getUuid(), "2026-01-01T12:00:00+02:00", "2026-01-01T12:25:00+02:00")))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.startTime").value("2026-01-01T10:00:00Z"));
        mvc.perform(post("/api/v1/study-sessions").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(body(own.getUuid(), "2026-01-01T12:00:00Z", "2026-01-01T11:00:00Z"))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/study-sessions").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/categories").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"  \"}"))
            .andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/categories").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"" + "x".repeat(101) + "\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void enforcesCsrfAndAdminOnlyAccountListing() throws Exception {
        mvc.perform(post("/api/v1/categories").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"New\"}"))
            .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/users")).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/users").with(user("admin").roles("ADMIN"))).andExpect(status().isOk());
    }

    @Test
    void onlyConfiguredOriginsCanMakeCredentialedRequests() throws Exception {
        mvc.perform(options("/api/v1/categories").header("Origin", "https://untrusted.example")
            .header("Access-Control-Request-Method", "POST")).andExpect(status().isForbidden());
        mvc.perform(options("/api/v1/categories").header("Origin", "http://localhost:5173")
            .header("Access-Control-Request-Method", "POST").header("Access-Control-Request-Headers", "X-CSRF-TOKEN,Content-Type"))
            .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    void deletingCategoryKeepsSessionsAndClearsTheirCategory() throws Exception {
        UserEntity owner = users.findByUsername("security-owner").orElseThrow();
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 10, 0);
        StudySessionEntity first = sessions.save(new StudySessionEntity(null, owner, own, start, start.plusMinutes(25)));
        StudySessionEntity second = sessions.save(new StudySessionEntity(null, owner, own, start.plusHours(1), start.plusHours(2)));
        StudySessionEntity untouched = sessions.save(new StudySessionEntity(null, other.getUser(), other, start, start.plusMinutes(10)));
        mvc.perform(delete("/api/v1/categories/{uuid}", own.getUuid()).with(csrf())).andExpect(status().isNoContent());
        entityManager.flush();
        entityManager.clear();
        assertThat(categoryRepository.findById(own.getUuid())).isEmpty();
        for (StudySessionEntity original : new StudySessionEntity[]{first, second}) {
            StudySessionEntity saved = sessions.findById(original.getUuid()).orElseThrow();
            assertThat(saved.getCategory()).isNull();
            assertThat(saved.getStartTime()).isEqualTo(original.getStartTime());
            assertThat(saved.getEndTime()).isEqualTo(original.getEndTime());
            assertThat(saved.getUser().getUsername()).isEqualTo("security-owner");
        }
        assertThat(sessions.findById(untouched.getUuid()).orElseThrow().getCategory().getUuid()).isEqualTo(other.getUuid());
        mvc.perform(get("/api/v1/users/me/study-sessions")).andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].category").value(org.hamcrest.Matchers.nullValue()))
            .andExpect(jsonPath("$[1].category").value(org.hamcrest.Matchers.nullValue()));
    }

    @Test
    void deletingEmptyCategoryAndDeletingAgain() throws Exception {
        mvc.perform(delete("/api/v1/categories/{uuid}", own.getUuid()).with(csrf())).andExpect(status().isNoContent());
        mvc.perform(delete("/api/v1/categories/{uuid}", own.getUuid()).with(csrf())).andExpect(status().isNotFound());
        assertThat(users.findByUsername("security-owner")).isPresent();
    }

    @Test
    void categoryDeletionRequiresOwnershipAuthenticationAndCsrf() throws Exception {
        for (UUID uuid : new UUID[]{other.getUuid(), UUID.randomUUID()}) {
            mvc.perform(delete("/api/v1/categories/{uuid}", uuid).with(csrf())).andExpect(status().isNotFound());
        }
        mvc.perform(delete("/api/v1/categories/{uuid}", own.getUuid())).andExpect(status().isForbidden());
        mvc.perform(delete("/api/v1/categories/{uuid}", own.getUuid()).with(anonymous()).with(csrf())).andExpect(status().isUnauthorized());
        assertThat(categoryRepository.findById(own.getUuid())).isPresent();
        assertThat(categoryRepository.findById(other.getUuid())).isPresent();
    }

    @Test
    void deletingSessionOrCategoryDoesNotDeleteItsParents() {
        UserEntity owner = users.findByUsername("security-owner").orElseThrow();
        StudySessionEntity session = sessions.save(new StudySessionEntity(null, owner, own,
            LocalDateTime.of(2026, 1, 1, 10, 0), LocalDateTime.of(2026, 1, 1, 10, 25)));
        sessions.delete(session);
        entityManager.flush();
        entityManager.clear();
        assertThat(categoryRepository.findById(own.getUuid())).isPresent();
        assertThat(users.findByUsername("security-owner")).isPresent();
        categoryRepository.deleteById(own.getUuid());
        entityManager.flush();
        entityManager.clear();
        assertThat(users.findByUsername("security-owner")).isPresent();
    }
}
