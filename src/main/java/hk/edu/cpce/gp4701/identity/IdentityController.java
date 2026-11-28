package hk.edu.cpce.gp4701.identity;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;

import hk.edu.cpce.gp4701.model.Account;

@Controller
public class IdentityController {

    private static final ZoneId HKT = ZoneId.of("Asia/Hong_Kong");
    private static final SecureRandom RANDOM = new SecureRandom();

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public IdentityController(AccountRepository accountRepository,
                              PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String register() {
        return "identity/register";
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam String fullName,
                                 @RequestParam String hkid,
                                 @RequestParam String phone,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 Model model) {
        String normalizedEmail = email.strip().toLowerCase(Locale.ROOT);
        String trimmedName = fullName.strip();
        String trimmedHkid = hkid.strip().toUpperCase(Locale.ROOT);
        String trimmedPhone = phone.strip();

        if (trimmedName.isEmpty() || trimmedHkid.isEmpty() || trimmedPhone.isEmpty()
                || normalizedEmail.isEmpty() || password.length() < 6) {
            model.addAttribute("error", "Please fill in all required fields (password at least 6 characters).");
            model.addAttribute("fullName", trimmedName);
            model.addAttribute("hkid", trimmedHkid);
            model.addAttribute("phone", trimmedPhone);
            model.addAttribute("email", normalizedEmail);
            return "identity/register";
        }

        // 1. 清理並驗證 HKID 格式（支援 A123456(7) 或 A1234567，含雙字母開頭如 AB123456(A)）
        String rawHkid = trimmedHkid.replaceAll("[\\s()]", "");
        if (!rawHkid.matches("^[A-Z]{1,2}[0-9]{6}[0-9A]$")) {
            model.addAttribute("error", "Invalid HKID format. Example: A123456(7) or A1234567.");
            model.addAttribute("fullName", trimmedName);
            model.addAttribute("hkid", trimmedHkid);
            model.addAttribute("phone", trimmedPhone);
            model.addAttribute("email", normalizedEmail);
            return "identity/register";
        }

        // 統一格式化為標準帶括號形式，例如 A123456(7)
        String normalizedHkid = rawHkid.substring(0, rawHkid.length() - 1)
                + "(" + rawHkid.charAt(rawHkid.length() - 1) + ")";

        // 2. 檢查 Email 是否重複
        if (accountRepository.existsByEmail(normalizedEmail)) {
            model.addAttribute("error", "This email address is already registered.");
            model.addAttribute("fullName", trimmedName);
            model.addAttribute("hkid", normalizedHkid);
            model.addAttribute("phone", trimmedPhone);
            model.addAttribute("email", normalizedEmail);
            return "identity/register";
        }

        // 3. 檢查 HKID 是否重複（同時比對帶括號與無括號，防止舊測試資料漏網）
        if (accountRepository.existsByHkid(normalizedHkid) || accountRepository.existsByHkid(rawHkid)) {
            model.addAttribute("error", "This HKID is already registered.");
            model.addAttribute("fullName", trimmedName);
            model.addAttribute("hkid", normalizedHkid);
            model.addAttribute("phone", trimmedPhone);
            model.addAttribute("email", normalizedEmail);
            return "identity/register";
        }

        // 產生 6 位數一次性驗證碼 (000000 - 999999)
        String rawCode = String.format("%06d", RANDOM.nextInt(1_000_000));

        Account account = new Account();
        account.setEmail(normalizedEmail);
        account.setPasswordHash(passwordEncoder.encode(password));
        account.setRole("MEMBER");
        account.setFullName(trimmedName);
        account.setHkid(normalizedHkid);
        account.setPhone(trimmedPhone);
        account.setVerifiedAt(null);
        account.setVerificationCodeHash(passwordEncoder.encode(rawCode));

        accountRepository.save(account);

        // TODO: 待成員 E 完成 EmailService 後，改為呼叫 emailService.sendVerificationCode(normalizedEmail, rawCode);
        System.out.println("==========================================");
        System.out.println("[DEV EMAIL] Verification code for " + normalizedEmail + " : " + rawCode);
        System.out.println("==========================================");

        String encodedEmail = UriUtils.encodeQueryParam(normalizedEmail, StandardCharsets.UTF_8);
        return "redirect:/verify-email?email=" + encodedEmail;
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam(required = false) String email, Model model) {
        model.addAttribute("email", email);
        return "identity/verify-email";
    }

    @PostMapping("/verify-email")
    public String handleVerifyEmail(@RequestParam String email,
                                    @RequestParam String code,
                                    Model model) {
        String normalizedEmail = email.strip().toLowerCase(Locale.ROOT);
        String trimmedCode = code.strip();

        Optional<Account> optAccount = accountRepository.findByEmail(normalizedEmail);
        if (optAccount.isEmpty()) {
            model.addAttribute("error", "Invalid or expired code.");
            model.addAttribute("email", normalizedEmail);
            return "identity/verify-email";
        }

        Account account = optAccount.get();
        // BR03: 必須仍處於未驗證狀態、驗證碼存在且比對相符
        if (account.getVerifiedAt() != null
                || account.getVerificationCodeHash() == null
                || !passwordEncoder.matches(trimmedCode, account.getVerificationCodeHash())) {
            model.addAttribute("error", "Invalid or expired code.");
            model.addAttribute("email", normalizedEmail);
            return "identity/verify-email";
        }

        // 驗證成功：寫入香港時間並清空驗證碼雜湊，確保不可重複使用
        account.setVerifiedAt(LocalDateTime.now(HKT));
        account.setVerificationCodeHash(null);
        accountRepository.save(account);

        return "redirect:/login?verified=true";
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