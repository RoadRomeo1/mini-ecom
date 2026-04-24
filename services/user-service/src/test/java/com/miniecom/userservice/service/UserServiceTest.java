package com.miniecom.userservice.service;

import com.miniecom.userservice.dto.UserRequest;
import com.miniecom.userservice.dto.UserResponse;
import com.miniecom.userservice.model.User;
import com.miniecom.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository repo;
    @InjectMocks UserService service;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("John Doe", "john@example.com", "9999999999");
        try {
            var f = User.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(user, 1L);
        } catch (Exception ignored) {}
    }

    @Test
    void create_newEmail_savesUser() {
        when(repo.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(repo.save(any())).thenReturn(user);

        UserResponse res = service.create(new UserRequest("John Doe", "john@example.com", "9999999999"));

        assertThat(res.name()).isEqualTo("John Doe");
        assertThat(res.email()).isEqualTo("john@example.com");
        verify(repo).save(any());
    }

    @Test
    void create_duplicateEmail_throwsException() {
        when(repo.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.create(new UserRequest("Jane", "john@example.com", "8888888888")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    void getAll_returnsAllUsers() {
        when(repo.findAll()).thenReturn(List.of(user));

        List<UserResponse> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).email()).isEqualTo("john@example.com");
    }

    @Test
    void getById_existingId_returnsUser() {
        when(repo.findById(1L)).thenReturn(Optional.of(user));

        UserResponse res = service.getById(1L);

        assertThat(res.id()).isEqualTo(1L);
        assertThat(res.name()).isEqualTo("John Doe");
    }

    @Test
    void getById_nonExistingId_throwsException() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found: 99");
    }

    @Test
    void update_existingUser_updatesFields() {
        when(repo.findById(1L)).thenReturn(Optional.of(user));
        when(repo.save(any())).thenReturn(user);

        UserResponse res = service.update(1L, new UserRequest("John Updated", "john@example.com", "1111111111"));

        assertThat(res.name()).isEqualTo("John Updated");
        verify(repo).save(user);
    }

    @Test
    void delete_callsRepository() {
        service.delete(1L);
        verify(repo).deleteById(1L);
    }
}
