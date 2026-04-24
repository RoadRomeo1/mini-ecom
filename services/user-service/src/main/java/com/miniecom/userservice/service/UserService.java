package com.miniecom.userservice.service;

import com.miniecom.userservice.dto.UserRequest;
import com.miniecom.userservice.dto.UserResponse;
import com.miniecom.userservice.model.User;
import com.miniecom.userservice.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    @CacheEvict(value = "users", allEntries = true)
    public UserResponse create(UserRequest req) {
        if (repo.findByEmail(req.email()).isPresent())
            throw new RuntimeException("Email already registered: " + req.email());
        return toResponse(repo.save(new User(req.name(), req.email(), req.phone())));
    }

    @Cacheable(value = "users")
    public List<UserResponse> getAll() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    @Cacheable(value = "user", key = "#id")
    public UserResponse getById(Long id) {
        return repo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    @Caching(
        put  = { @CachePut(value = "user", key = "#id") },
        evict = { @CacheEvict(value = "users", allEntries = true) }
    )
    public UserResponse update(Long id, UserRequest req) {
        User u = repo.findById(id).orElseThrow(() -> new RuntimeException("User not found: " + id));
        u.setName(req.name());
        u.setEmail(req.email());
        u.setPhone(req.phone());
        return toResponse(repo.save(u));
    }

    @Caching(evict = {
        @CacheEvict(value = "user", key = "#id"),
        @CacheEvict(value = "users", allEntries = true)
    })
    public void delete(Long id) {
        repo.deleteById(id);
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getPhone());
    }
}
