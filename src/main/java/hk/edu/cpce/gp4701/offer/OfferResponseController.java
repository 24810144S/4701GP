package hk.edu.cpce.gp4701.offer;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hk.edu.cpce.gp4701.application.AdmissionApplicationRepository;
import hk.edu.cpce.gp4701.model.Account;
import hk.edu.cpce.gp4701.model.AdmissionApplication;
import jakarta.servlet.http.HttpSession;

@Controller
public class OfferResponseController {

    private final AdmissionApplicationRepository applications;

    public OfferResponseController(AdmissionApplicationRepository applications) {
        this.applications = applications;
    }

    @PostMapping("/applications/{id}/offer")
    public String respondToOffer(@PathVariable Long id,
                                 @RequestParam("response") String response,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<AdmissionApplication> optApp = applications.findById(id);
        if (optApp.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Application record not found.");
            return "redirect:/applications";
        }

        AdmissionApplication app = optApp.get();

        // 1. 權限檢查：只能回應自己的申請
        if (!app.getMember().getId().equals(currentUser.getId())) {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to respond to this application.");
            return "redirect:/applications";
        }

        // 2. BR08：必須已正式公布 (publishedAt != null) 且審核結果為 Success
        if (app.getPublishedAt() == null || !"Success".equalsIgnoreCase(app.getDecision())) {
            redirectAttributes.addFlashAttribute("error", "Offer response is only available for published successful applications (BR08).");
            return "redirect:/applications/" + id;
        }

        // 3. BR08：不可重複回應
        if (app.getOfferResponse() != null && !app.getOfferResponse().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "You have already submitted your offer response (" + app.getOfferResponse() + ").");
            return "redirect:/applications/" + id;
        }

        // 4. 驗證回應值並寫入資料庫 (ACCEPTED 或 DECLINED)
        String normalizedResponse = response.trim().toUpperCase();
        if (!"ACCEPTED".equals(normalizedResponse) && !"DECLINED".equals(normalizedResponse)) {
            redirectAttributes.addFlashAttribute("error", "Invalid offer response option.");
            return "redirect:/applications/" + id;
        }

        app.setOfferResponse(normalizedResponse);
        app.setRespondedAt(LocalDateTime.now(ZoneId.of("Asia/Hong_Kong")));
        applications.save(app);

        if ("ACCEPTED".equals(normalizedResponse)) {
            redirectAttributes.addFlashAttribute("success", "Congratulations! You have officially ACCEPTED the admission offer.");
        } else {
            // BR09：拒絕錄取後自動釋出名額
            redirectAttributes.addFlashAttribute("success", "You have DECLINED the admission offer. Your reserved seat has been released (BR09).");
        }

        return "redirect:/applications/" + id;
    }
}