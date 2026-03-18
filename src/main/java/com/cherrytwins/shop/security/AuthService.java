package com.cherrytwins.shop.security;

import com.cherrytwins.shop.common.exception.BadRequestException;
import com.cherrytwins.shop.security.dto.LoginRequest;
import com.cherrytwins.shop.security.dto.RegisterRequest;
import com.cherrytwins.shop.users.domain.User;
import com.cherrytwins.shop.users.domain.UserRole;
import com.cherrytwins.shop.users.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthEmailService authEmailService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthEmailService authEmailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authEmailService = authEmailService;
    }

    @Transactional
    public String register(RegisterRequest req) {
        userRepository.findByEmailIgnoreCase(req.getEmail()).ifPresent(u -> {
            throw new BadRequestException("Email already exists");
        });

        User u = new User();
        u.setEmail(req.getEmail().trim().toLowerCase());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setFullName(req.getFullName());
        u.setPhone(req.getPhone());
        u.setRole(UserRole.CUSTOMER);
        u.setActive(true);

        // ✅ importante: al registrar queda NO verificado
        u.setEmailVerified(false);

        u = userRepository.save(u);

        // ✅ 1) envía correo de verificación (real)
        authEmailService.sendVerificationEmail(u.getId());

        // (opcional) puedes devolver token aunque no esté verificado,
        // o devolver 201 y pedir que verifique. Yo recomiendo devolver token
        // pero bloquear acceso a acciones sensibles si no verificó.
        return jwtService.generateToken(UserPrincipal.from(u));
    }

    // login lo vemos en la sección B

    @Transactional(readOnly = true)
    public String login(LoginRequest req) {
        User u = userRepository.findByEmailIgnoreCase(req.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        // 1) usuario activo
        if (!u.isActive()) {
            throw new BadRequestException("User is inactive");
        }

        // 2) email verificado ✅ (bloqueo real)
        if (!u.isEmailVerified()) {
            // mensaje claro para el cliente
            throw new BadRequestException("Email not verified. Please verify your email.");
        }

        // 3) password
        if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new BadRequestException("Invalid credentials");
        }

        return jwtService.generateToken(UserPrincipal.from(u));
    }
}