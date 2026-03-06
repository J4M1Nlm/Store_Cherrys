package com.cherrytwins.shop.security;

import com.cherrytwins.shop.security.dto.LoginRequest;
import com.cherrytwins.shop.security.dto.RegisterRequest;
import com.cherrytwins.shop.users.domain.User;
import com.cherrytwins.shop.users.domain.UserRole;
import com.cherrytwins.shop.users.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    public String register(RegisterRequest req) {
        if (userRepository.existsByEmailIgnoreCase(req.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setEmail(req.getEmail().trim().toLowerCase());
        user.setFullName(req.getFullName());
        user.setPhone(req.getPhone());
        user.setRole(UserRole.CUSTOMER);
        user.setActive(true);
        user.setEmailVerified(false);
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));

        userRepository.save(user);

        return jwtService.generateToken(UserPrincipal.from(user));
    }

    public String login(LoginRequest req) {
        try {
            var authToken = new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword());
            authManager.authenticate(authToken);
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid credentials");
        }

        var user = userRepository.findByEmailIgnoreCase(req.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        return jwtService.generateToken(UserPrincipal.from(user));
    }
}
