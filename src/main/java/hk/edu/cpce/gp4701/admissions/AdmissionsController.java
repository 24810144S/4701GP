package hk.edu.cpce.gp4701.admissions;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hk.edu.cpce.gp4701.application.AdmissionApplicationRepository;
import hk.edu.cpce.gp4701.model.AdmissionApplication;
import hk.edu.cpce.gp4701.model.Programme;
import hk.edu.cpce.gp4701.model.ProgrammeLevel;
import hk.edu.cpce.gp4701.school.ProgrammeLevelRepository;

@Controller
@RequestMapping("/admin/admissions")
public class AdmissionsController {

    private static final ZoneId HKT = ZoneId.of("Asia/Hong_Kong");

    private final AdmissionApplicationRepository applicationRepository;
    private final ProgrammeLevelRepository programmeLevelRepository;

    public AdmissionsController(AdmissionApplicationRepository applicationRepository,
                                ProgrammeLevelRepository programmeLevelRepository) {
        this.applicationRepository = applicationRepository;
        this.programmeLevelRepository = programmeLevelRepository;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Long programmeId,
                       @RequestParam(required = false) Integer level,
                       @RequestParam(required = false) String status,
                       Model model) {
        List<ProgrammeLevel> allLevels = programmeLevelRepository.findAll();
        allLevels.sort(Comparator.comparing((ProgrammeLevel pl) -> pl.getProgramme().getId())
                .thenComparing(ProgrammeLevel::getLevel));

        List<QuotaStat> quotaStats = new ArrayList<>();
        Map<Long, Programme> programmeMap = new LinkedHashMap<>();

        for (ProgrammeLevel pl : allLevels) {
            programmeMap.putIfAbsent(pl.getProgramme().getId(), pl.getProgramme());
            long occupied = applicationRepository.countOccupiedSeats(pl.getId());
            quotaStats.add(new QuotaStat(
                    pl.getProgramme().getName(),
                    pl.getLevel(),
                    pl.getSeatLimit(),
                    occupied
            ));
        }

        List<AdmissionApplication> applications = applicationRepository.findAllWithDetailsOrderBySubmittedAtDesc()
                .stream()
                .filter(app -> programmeId == null || app.getProgrammeLevel().getProgramme().getId().equals(programmeId))
                .filter(app -> level == null || app.getProgrammeLevel().getLevel().intValue() == level)
                .filter(app -> matchesStatus(app, status))
                .toList();

        model.addAttribute("quotaStats", quotaStats);
        model.addAttribute("programmes", new ArrayList<>(programmeMap.values()));
        model.addAttribute("applications", applications);
        model.addAttribute("filterProgrammeId", programmeId);
        model.addAttribute("filterLevel", level);
        model.addAttribute("filterStatus", status);

        return "admissions/list";
    }

    private boolean matchesStatus(AdmissionApplication app, String status) {
        if (status == null || status.isBlank()) {
            return true;
        }
        if ("UNPUBLISHED".equalsIgnoreCase(status)) {
            return app.getPublishedAt() == null;
        }
        return app.getDecision() != null && app.getDecision().equalsIgnoreCase(status);
    }

    @PostMapping("/{id}/decision")
    @Transactional
    public String saveDecision(@PathVariable Long id,
                               @RequestParam(required = false) String decision,
                               RedirectAttributes redirectAttributes) {
        if (decision == null || decision.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Please select a decision before saving.");
            return "redirect:/admin/admissions";
        }

        AdmissionApplication app = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + id));

        String normalizedDecision;
        if ("Success".equalsIgnoreCase(decision)) {
            normalizedDecision = "SUCCESS";
        } else if ("Wait".equalsIgnoreCase(decision)) {
            normalizedDecision = "WAIT";
        } else if ("Fail".equalsIgnoreCase(decision)) {
            normalizedDecision = "FAIL";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid decision value.");
            return "redirect:/admin/admissions";
        }

        boolean alreadyOccupyingSeat = "SUCCESS".equalsIgnoreCase(app.getDecision())
                && (app.getOfferResponse() == null || !"DECLINED".equalsIgnoreCase(app.getOfferResponse()));

        if ("SUCCESS".equals(normalizedDecision) && !alreadyOccupyingSeat) {
            ProgrammeLevel pl = app.getProgrammeLevel();
            long occupied = applicationRepository.countOccupiedSeats(pl.getId());
            if (occupied >= pl.getSeatLimit()) {
                redirectAttributes.addFlashAttribute("error",
                        "Seat limit exceeded for " + pl.getProgramme().getName() + " S" + pl.getLevel()
                                + " (" + occupied + "/" + pl.getSeatLimit() + ") (BR07).");
                return "redirect:/admin/admissions";
            }
        }

        app.setDecision(normalizedDecision);
        applicationRepository.save(app);

        redirectAttributes.addFlashAttribute("success",
                "Decision for Application #" + id + " saved as " + normalizedDecision + ".");
        return "redirect:/admin/admissions";
    }

    @PostMapping("/{id}/publish")
    @Transactional
    public String publishOne(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        AdmissionApplication app = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + id));

        if (app.getDecision() == null || app.getDecision().isBlank()) {
            redirectAttributes.addFlashAttribute("error",
                    "Please save a decision for Application #" + id + " before publishing.");
            return "redirect:/admin/admissions";
        }

        app.setPublishedAt(LocalDateTime.now(HKT));
        applicationRepository.save(app);

        System.out.println("==========================================");
        System.out.println("[DEV EMAIL] Result published for Application #" + id
                + " (" + app.getMember().getEmail() + ") -> Decision: " + app.getDecision());
        System.out.println("==========================================");

        redirectAttributes.addFlashAttribute("success",
                "Application #" + id + " result (" + app.getDecision() + ") published successfully.");
        return "redirect:/admin/admissions";
    }

    @PostMapping("/publish-all")
    @Transactional
    public String publishAll(RedirectAttributes redirectAttributes) {
        List<AdmissionApplication> allApps = applicationRepository.findAllWithDetailsOrderBySubmittedAtDesc();
        LocalDateTime nowHkt = LocalDateTime.now(HKT);
        int publishedCount = 0;

        for (AdmissionApplication app : allApps) {
            if (app.getDecision() != null && !app.getDecision().isBlank() && app.getPublishedAt() == null) {
                app.setPublishedAt(nowHkt);
                applicationRepository.save(app);
                publishedCount++;

                System.out.println("[DEV EMAIL] Batch published Application #" + app.getId()
                        + " (" + app.getMember().getEmail() + ") -> Decision: " + app.getDecision());
            }
        }

        if (publishedCount == 0) {
            redirectAttributes.addFlashAttribute("error",
                    "No unpublished applications with saved decisions to publish.");
        } else {
            redirectAttributes.addFlashAttribute("success",
                    "Published " + publishedCount + " admission result(s) successfully (BR06 / BR10).");
        }
        return "redirect:/admin/admissions";
    }
}