package de.sxpl.pialgra.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.List;
import java.util.UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryUpdateIntegrationTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void updatesOwnedCategoryAndRejectsInvalidOrUnauthorizedChanges() throws Exception {
        String owner = "cat-" + UUID.randomUUID().toString().substring(0, 12);
        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("username", owner, "password", "password123"))))
                .andExpect(status().isCreated());
        var created = mvc.perform(post("/api/v1/categories").with(user(owner)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("name", "Original", "color", "#0348DD"))))
                .andExpect(status().isCreated()).andReturn().getResponse();
        String uuid = json.readTree(created.getContentAsString()).get("uuid").asText();
        String path = "/api/v1/categories/" + uuid;
        String changes = json.writeValueAsString(Map.of("name", "  Renamed  ", "color", "#15803D"));
        mvc.perform(put(path).with(user(owner)).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(changes))
                .andExpect(status().isOk()).andExpect(jsonPath("$.uuid").value(uuid))
                .andExpect(jsonPath("$.name").value("Renamed")).andExpect(jsonPath("$.color").value("#15803D"));
        mvc.perform(put(path).with(user("another-user")).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(changes))
                .andExpect(status().isNotFound());
        mvc.perform(put("/api/v1/categories/" + UUID.randomUUID()).with(user(owner)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(changes)).andExpect(status().isNotFound());
        for (var invalid : List.of(Map.of("name", " ", "color", "#15803D"),
                Map.of("name", "x".repeat(101), "color", "#15803D"),
                Map.of("name", "Valid", "color", "red"), Map.of("name", "Valid"))) {
            mvc.perform(put(path).with(user(owner)).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content(json.writeValueAsString(invalid))).andExpect(status().isBadRequest());
        }
        mvc.perform(get("/api/v1/users/me/categories").with(user(owner)))
                .andExpect(status().isOk()).andExpect(jsonPath("$[?(@.uuid == '" + uuid + "')].name").value(org.hamcrest.Matchers.contains("Renamed")))
                .andExpect(jsonPath("$[?(@.uuid == '" + uuid + "')].color").value(org.hamcrest.Matchers.contains("#15803D")));
    }
}
