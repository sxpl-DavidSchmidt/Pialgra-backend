package de.sxpl.pialgra.controllers;

import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.repositories.ImageRepository;
import de.sxpl.pialgra.service.UserService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser("profile-owner")
class ProfilePictureIntegrationTests {
    private static final String URL = "/api/v1/users/me/profile-picture";
    @Autowired MockMvc mvc;
    @Autowired UserService users;
    @Autowired ImageRepository images;
    @Autowired EntityManager entityManager;

    @BeforeEach
    void createUser() {
        UserEntity user = new UserEntity();
        user.setUsername("profile-owner");
        user.setPassword("password123");
        users.createUser(user);
        reload();
    }

    private void reload() {
        entityManager.flush();
        entityManager.clear();
    }

    private byte[] defaultBytes() throws Exception {
        return new ClassPathResource("static/images/default_profile_picture.png").getContentAsByteArray();
    }

    private MockMultipartFile picture(String format, int width, int height) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB), format, bytes);
        return new MockMultipartFile("image", "picture." + format, "image/" + format, bytes.toByteArray());
    }

    @Test
    void newAccountUsesProvidedDefaultImage() throws Exception {
        mvc.perform(get(URL)).andExpect(status().isOk())
                .andExpect(jsonPath("$.imageData").value(Base64.getEncoder().encodeToString(defaultBytes())));
    }

    @Test
    void uploadPersistsAsPngAndRemoveRestoresDefaultWithoutOrphanImages() throws Exception {
        long count = images.count();
        var originalId = users.findByUsername("profile-owner").orElseThrow().getProfilePicture().getUuid();
        mvc.perform(multipart(HttpMethod.PUT, URL).with(csrf()).file(picture("jpeg", 64, 64)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.imageData").isNotEmpty());
        reload();
        var saved = users.findByUsername("profile-owner").orElseThrow().getProfilePicture();
        assertThat(saved.getUuid()).isEqualTo(originalId);
        assertThat(saved.getImageData()).startsWith((byte) 137, (byte) 80, (byte) 78, (byte) 71);
        mvc.perform(get(URL)).andExpect(status().isOk())
                .andExpect(jsonPath("$.imageData").value(Base64.getEncoder().encodeToString(saved.getImageData())));

        mvc.perform(delete(URL).with(csrf())).andExpect(status().isOk())
                .andExpect(jsonPath("$.imageData").value(Base64.getEncoder().encodeToString(defaultBytes())));
        reload();
        assertThat(users.findByUsername("profile-owner").orElseThrow().getProfilePicture().getImageData()).isEqualTo(defaultBytes());
        assertThat(images.count()).isEqualTo(count);
    }

    @Test
    void acceptsAnImageAtTheTenMegabyteBoundary() throws Exception {
        byte[] padded = java.util.Arrays.copyOf(picture("png", 32, 32).getBytes(), 10 * 1024 * 1024);
        mvc.perform(multipart(HttpMethod.PUT, URL).with(csrf())
                .file(new MockMultipartFile("image", "boundary.png", "image/png", padded)))
                .andExpect(status().isOk());
        reload();
        assertThat(users.findByUsername("profile-owner").orElseThrow().getProfilePicture().getImageData().length)
                .isLessThan(10 * 1024 * 1024);
    }

    @Test
    void invalidUploadsLeaveExistingPictureIntact() throws Exception {
        mvc.perform(multipart(HttpMethod.PUT, URL).with(csrf()).file(picture("png", 64, 32))).andExpect(status().isBadRequest());
        mvc.perform(multipart(HttpMethod.PUT, URL).with(csrf()).file(picture("png", 513, 513))).andExpect(status().isBadRequest());
        mvc.perform(multipart(HttpMethod.PUT, URL).with(csrf()).file(picture("gif", 32, 32))).andExpect(status().isBadRequest());
        mvc.perform(multipart(HttpMethod.PUT, URL).with(csrf()).file(new MockMultipartFile("image", new byte[0]))).andExpect(status().isBadRequest());
        mvc.perform(multipart(HttpMethod.PUT, URL).with(csrf()).file(new MockMultipartFile("image", "fake.png", "image/png", new byte[]{1, 2, 3})))
                .andExpect(status().isBadRequest());
        mvc.perform(multipart(HttpMethod.PUT, URL).with(csrf()).file(new MockMultipartFile("image", new byte[10 * 1024 * 1024 + 1])))
                .andExpect(status().isPayloadTooLarge());
        reload();
        assertThat(users.findByUsername("profile-owner").orElseThrow().getProfilePicture().getImageData()).isEqualTo(defaultBytes());
    }

    @Test
    void changingOneAccountsPictureDoesNotChangeAnother() throws Exception {
        UserEntity other = new UserEntity();
        other.setUsername("other-profile-owner");
        other.setPassword("password123");
        users.createUser(other);
        mvc.perform(multipart(HttpMethod.PUT, URL).with(csrf()).file(picture("png", 32, 32))).andExpect(status().isOk());
        reload();
        mvc.perform(get(URL).with(user("other-profile-owner"))).andExpect(status().isOk())
                .andExpect(jsonPath("$.imageData").value(Base64.getEncoder().encodeToString(defaultBytes())));
    }

    @Test
    void pictureEndpointsRequireAuthentication() throws Exception {
        mvc.perform(get(URL).with(anonymous())).andExpect(status().isUnauthorized());
        mvc.perform(delete(URL).with(csrf()).with(anonymous())).andExpect(status().isUnauthorized());
        mvc.perform(multipart(HttpMethod.PUT, URL).with(csrf()).file(picture("png", 32, 32)).with(anonymous()))
                .andExpect(status().isUnauthorized());
    }
}
