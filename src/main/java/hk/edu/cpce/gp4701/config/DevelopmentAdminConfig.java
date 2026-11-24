package hk.edu.cpce.gp4701.config;

import java.util.Locale;
import hk.edu.cpce.gp4701.identity.AccountRepository;
import hk.edu.cpce.gp4701.model.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DevelopmentAdminConfig {
    @Bean
    ApplicationRunner createDevelopmentAdmin(
            AccountRepository accounts,
            PasswordEncoder encoder,
            @Value("${sas.dev.admin-email}") String adminEmail,
            @Value("${sas.dev.admin-password}") String adminPassword) {
        return args -> {
            if (adminPassword.isBlank()) {
                return;
            }
            String email = adminEmail.strip().toLowerCase(Locale.ROOT);
            var existing = accounts.findByEmail(email);
            if (existing.isPresent()) {
                if (!"ADMIN".equals(existing.get().getRole())) {
                    throw new IllegalStateException("The development admin email already belongs to a member");
                }
                return;
            }
            Account admin = new Account();
            admin.setEmail(email);
            admin.setPasswordHash(encoder.encode(adminPassword));
            admin.setRole("ADMIN");
            accounts.save(admin);
        };
    }
}
