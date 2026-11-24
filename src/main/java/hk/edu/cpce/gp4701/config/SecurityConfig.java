package hk.edu.cpce.gp4701.config;

import java.util.Locale;
import hk.edu.cpce.gp4701.identity.AccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/school/**", "/register", "/verify-email",
                        "/login", "/css/**", "/images/**", "/error").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/profile", "/applications", "/applications/**").hasRole("MEMBER")
                .requestMatchers("/signed-in").hasAnyRole("MEMBER", "ADMIN")
                .anyRequest().denyAll())
            .formLogin(login -> login
                .loginPage("/login")
                .usernameParameter("email")
                .defaultSuccessUrl("/signed-in"));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(AccountRepository accounts) {
        return email -> accounts.findByEmail(email.strip().toLowerCase(Locale.ROOT))
                .map(account -> User.withUsername(account.getEmail())
                        .password(account.getPasswordHash())
                        .roles(account.getRole())
                        .disabled("MEMBER".equals(account.getRole()) && account.getVerifiedAt() == null)
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }
}
