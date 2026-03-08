package com.cherrytwins.shop.users.service;

import com.cherrytwins.shop.common.exception.NotFoundException;
import com.cherrytwins.shop.users.repository.UserRepository;
import com.cherrytwins.shop.users.web.dto.UpdateProfileRequest;
import com.cherrytwins.shop.users.web.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getMe(Long userId) {
        var u = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return new UserResponse(
                u.getId(),
                u.getEmail(),
                u.getFullName(),
                u.getPhone(),
                u.getRole(),
                u.isActive(),
                u.isEmailVerified(),
                u.getCreatedAt()
        );
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest req) {
        var u = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        u.setFullName(req.getFullName());
        u.setPhone(req.getPhone());

        // updated_at lo maneja trigger o @PreUpdate
        return new UserResponse(
                u.getId(),
                u.getEmail(),
                u.getFullName(),
                u.getPhone(),
                u.getRole(),
                u.isActive(),
                u.isEmailVerified(),
                u.getCreatedAt()
        );
    }
}
