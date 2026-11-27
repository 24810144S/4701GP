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
import org.springframework.web.bind.annotation.RequestParam;

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

    public ApplicationController(AccountRepository accountRepository,
                                 ProgrammeLevelRepository programmeLevels,
                                 AdmissionApplicationRepository applicationRepository) {
        this.accountRepository = accountRepository;
        this.programmeLevels = programmeLevels;
        this.applicationRepository = applicationRepository;
    }

    // U10: 提交申請頁面
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

    // U11: 我的申請列表
    @GetMapping("/applications")
    public String myApplications(Principal principal, Model model) {
        Account member = accountRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Member not found"));
        List<AdmissionApplication> applications = applicationRepository.findByMemberOrderBySubmittedAtDesc(member);
        model.addAttribute("applications", applications);
        return "application/list";
    }

    // U12: 申請詳情與接受/拒絕頁面
    @GetMapping("/applications/{id}")
    public String detail(@PathVariable Long id, Principal principal, Model model) {
        AdmissionApplication app = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        // BR05: 確保會員只能查看自己的申請
        if (!app.getMember().getEmail().equals(principal.getName())) {
            throw new SecurityException("Access denied");
        }

        model.addAttribute("app", app);
        return "application/detail";
    }
}