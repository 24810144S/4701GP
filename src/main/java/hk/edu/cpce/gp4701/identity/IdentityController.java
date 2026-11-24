package hk.edu.cpce.gp4701.identity;

import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IdentityController {
    @GetMapping("/register")
    public String register() {
        return "identity/register";
    }

    @GetMapping("/verify-email")
    public String verifyEmail() {
        return "identity/verify-email";
    }

    @GetMapping("/login")
    public String login() {
        return "identity/login";
    }

    @GetMapping("/profile")
    public String profile() {
        return "identity/profile";
    }

    @GetMapping("/signed-in")
    public String signedIn(Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        return admin ? "redirect:/admin/admissions" : "redirect:/applications";
    }
}
