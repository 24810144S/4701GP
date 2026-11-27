package hk.edu.cpce.gp4701.identity;

import org.springframework.stereotype.Controller;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;

import hk.edu.cpce.gp4701.model.Account;

@Controller
public class IdentityController {
    private final AccountRepository accountRepository;

    public IdentityController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    
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
    public String profilePage(Principal principal, Model model) {
        Account account = accountRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Account not found"));
        model.addAttribute("account", account);
        return "identity/profile";
    }

    @GetMapping("/signed-in")
    public String signedIn(Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        return admin ? "redirect:/admin/admissions" : "redirect:/applications";
    }
    
}
