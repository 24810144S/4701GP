package hk.edu.cpce.gp4701.application;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hk.edu.cpce.gp4701.identity.AccountRepository;
import hk.edu.cpce.gp4701.model.Account;
import hk.edu.cpce.gp4701.model.AdmissionApplication;
import hk.edu.cpce.gp4701.model.Programme;
import hk.edu.cpce.gp4701.model.ProgrammeLevel;
import hk.edu.cpce.gp4701.school.ProgrammeLevelRepository;

@Controller
public class ApplicationController {

    private final AccountRepository accountRepository;
    private final ProgrammeLevelRepository programmeLevels;
    private final AdmissionApplicationRepository applicationRepository;
    private final ApplicationService applicationService;

    public ApplicationController(AccountRepository accountRepository,
                                 ProgrammeLevelRepository programmeLevels,
                                 AdmissionApplicationRepository applicationRepository,
                                 ApplicationService applicationService) {
        this.accountRepository = accountRepository;
        this.programmeLevels = programmeLevels;
        this.applicationRepository = applicationRepository;
        this.applicationService = applicationService;
    }

    // U10: GET - Show Application Form
    @GetMapping("/applications/new")
    public String newApplication(@RequestParam(required = false) Long programmeId,
                                 Principal principal,
                                 Model model) {
        Account account = accountRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Member not found"));

        List<ProgrammeLevel> allLevels = programmeLevels.findAll();
        allLevels.sort(Comparator.comparing((ProgrammeLevel pl) -> pl.getProgramme().getId())
                                 .thenComparing(ProgrammeLevel::getLevel));

        Map<Programme, List<ProgrammeLevel>> programmeMap = new LinkedHashMap<>();
        for (ProgrammeLevel pl : allLevels) {
            programmeMap.computeIfAbsent(pl.getProgramme(), k -> new ArrayList<>()).add(pl);
        }

        model.addAttribute("account", account);
        model.addAttribute("programmeMap", programmeMap);
        model.addAttribute("selectedProgrammeId", programmeId);
        model.addAttribute("nowHkt", LocalDateTime.now(ZoneId.of("Asia/Hong_Kong")));
        return "application/new";
    }

    // U10: POST - Submit Application Form
    @PostMapping("/applications")
    public String handleApplyForm(@RequestParam("programmeId") Long programmeId,
                                  @RequestParam("level") Integer level,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes) {
        Account account = accountRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Member not found"));
        try {
            applicationService.submitApplication(account, programmeId, level);
            return "redirect:/applications?submitted=true";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/applications/new";
        }
    }

    // U11: GET - My Applications List
    @GetMapping("/applications")
    public String myApplications(Principal principal, Model model) {
        Account member = accountRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Member not found"));
        List<AdmissionApplication> applications = applicationRepository.findByMemberOrderBySubmittedAtDesc(member);
        model.addAttribute("applications", applications);
        return "application/list";
    }

    // U12: GET - Application Detail & Offer Response Page
    @GetMapping("/applications/{id}")
    public String detail(@PathVariable Long id, Principal principal, Model model) {
        AdmissionApplication app = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        // BR05: Ensure member can only view their own application
        if (!app.getMember().getEmail().equals(principal.getName())) {
            throw new SecurityException("Access denied");
        }

        model.addAttribute("app", app);
        return "application/detail";
    }

    // U12: POST - Accept Offer
    @PostMapping("/applications/{id}/accept")
    public String acceptOffer(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        Account account = accountRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Member not found"));
        try {
            applicationService.recordOfferResponse(id, account, "ACCEPTED");
            return "redirect:/applications/" + id + "?responded=true";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/applications/" + id;
        }
    }

    // U12: POST - Decline Offer
    @PostMapping("/applications/{id}/decline")
    public String declineOffer(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        Account account = accountRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Member not found"));
        try {
            applicationService.recordOfferResponse(id, account, "DECLINED");
            return "redirect:/applications/" + id + "?responded=true";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/applications/" + id;
        }
    }
}
