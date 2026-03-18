package com.cherrytwins.shop.security;

import com.cherrytwins.shop.common.exception.BadRequestException;
import com.cherrytwins.shop.common.exception.NotFoundException;
import com.cherrytwins.shop.common.util.EmailService;
import com.cherrytwins.shop.common.util.MailTemplateService;
import com.cherrytwins.shop.security.domain.EmailVerificationToken;
import com.cherrytwins.shop.security.domain.PasswordResetToken;
import com.cherrytwins.shop.security.repository.EmailVerificationTokenRepository;
import com.cherrytwins.shop.security.repository.PasswordResetTokenRepository;
import com.cherrytwins.shop.security.util.TokenUtil;
import com.cherrytwins.shop.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;

@Service
public class AuthEmailService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository evtRepo;
    private final PasswordResetTokenRepository prtRepo;
    private final PasswordEncoder passwordEncoder;
    private final MailTemplateService mailTemplateService;

    @Value("${app.frontend.base-url:http://localhost:3000}")
    private String frontendBaseUrl;

    @Value("${app.security.verify-email-exp-minutes:60}")
    private long verifyExpMinutes;

    @Value("${app.security.reset-password-exp-minutes:30}")
    private long resetExpMinutes;

    public AuthEmailService(EmailService emailService,
                            UserRepository userRepository,
                            EmailVerificationTokenRepository evtRepo,
                            PasswordResetTokenRepository prtRepo,
                            PasswordEncoder passwordEncoder,
                            MailTemplateService mailTemplateService) {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.evtRepo = evtRepo;
        this.prtRepo = prtRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailTemplateService = mailTemplateService;
    }

    @Transactional
    public void sendVerificationEmail(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.isEmailVerified()) return;

        evtRepo.deleteAllByUserId(userId);

        String rawToken = TokenUtil.generateRawToken();
        String hash = TokenUtil.sha256Hex(rawToken);

        EmailVerificationToken t = new EmailVerificationToken();
        t.setUserId(userId);
        t.setTokenHash(hash);
        t.setExpiresAt(OffsetDateTime.now().plusMinutes(verifyExpMinutes));
        evtRepo.save(t);

        String verifyUrl = frontendBaseUrl + "/verify-email?token=" + rawToken;

        String html = mailTemplateService.render("verify-email", Map.of(
                "name", user.getFullName() == null ? "cliente" : user.getFullName(),
                "verifyUrl", verifyUrl
        ));

        emailService.sendHtml(user.getEmail(), "Verifica tu correo - CherryTwins", html);
    }

    @Transactional
    public void verifyEmail(String rawToken) {
        String hash = TokenUtil.sha256Hex(rawToken);

        EmailVerificationToken t = evtRepo.findByTokenHash(hash)
                .orElseThrow(() -> new BadRequestException("Invalid token"));

        if (t.getUsedAt() != null) throw new BadRequestException("Token already used");
        if (OffsetDateTime.now().isAfter(t.getExpiresAt())) throw new BadRequestException("Token expired");

        var user = userRepository.findById(t.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setEmailVerified(true);
        t.setUsedAt(OffsetDateTime.now());
    }

    @Transactional
    public void forgotPassword(String email) {
        var userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isEmpty()) return;

        var user = userOpt.get();

        prtRepo.deleteAllByUserId(user.getId());

        String rawToken = TokenUtil.generateRawToken();
        String hash = TokenUtil.sha256Hex(rawToken);

        PasswordResetToken t = new PasswordResetToken();
        t.setUserId(user.getId());
        t.setTokenHash(hash);
        t.setExpiresAt(OffsetDateTime.now().plusMinutes(resetExpMinutes));
        prtRepo.save(t);

        String resetUrl = frontendBaseUrl + "/reset-password?token=" + rawToken;

        String html = mailTemplateService.render("reset-password", Map.of(
                "name", user.getFullName() == null ? "cliente" : user.getFullName(),
                "resetUrl", resetUrl,
                "minutes", resetExpMinutes
        ));

        emailService.sendHtml(user.getEmail(), "Recuperación de contraseña - CherryTwins", html);
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        String hash = TokenUtil.sha256Hex(rawToken);

        PasswordResetToken t = prtRepo.findByTokenHash(hash)
                .orElseThrow(() -> new BadRequestException("Invalid token"));

        if (t.getUsedAt() != null) throw new BadRequestException("Token already used");
        if (OffsetDateTime.now().isAfter(t.getExpiresAt())) throw new BadRequestException("Token expired");

        var user = userRepository.findById(t.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        t.setUsedAt(OffsetDateTime.now());
    }
}