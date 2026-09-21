package de.sxpl.pialgra.service.impl;

import de.sxpl.pialgra.domain.entities.ImageEntity;
import de.sxpl.pialgra.domain.entities.UserEntity;
import de.sxpl.pialgra.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_encodesPasswordBeforeSaving() {
        UserEntity userEntity = new UserEntity("username", "raw-password", null, null, null);
        when(passwordEncoder.encode("raw-password")).thenReturn("{bcrypt}encoded-password");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserEntity result = userService.createUser(userEntity);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());

        assertThat(captor.getValue().getPassword()).isEqualTo("{bcrypt}encoded-password");
        assertThat(captor.getValue().getCreatedAt()).isNotNull();
        assertThat(captor.getValue().getProfilePicture()).isNotNull();
        assertThat(captor.getValue().getProfilePicture().getImageData()).isNotEmpty();
        assertThat(result.getPassword()).isEqualTo("{bcrypt}encoded-password");
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getProfilePicture()).isNotNull();
        assertThat(result.getProfilePicture().getImageData()).isNotEmpty();
    }

    @Test
    void createUser_preservesExistingProfilePicture() {
        ImageEntity customImage = new ImageEntity(UUID.randomUUID(), new byte[]{1, 2, 3});
        UserEntity userEntity = new UserEntity("username", "raw-password", customImage, null, null);
        when(passwordEncoder.encode("raw-password")).thenReturn("{bcrypt}encoded-password");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserEntity result = userService.createUser(userEntity);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());

        assertThat(captor.getValue().getProfilePicture()).isEqualTo(customImage);
        assertThat(result.getProfilePicture()).isEqualTo(customImage);
    }
}