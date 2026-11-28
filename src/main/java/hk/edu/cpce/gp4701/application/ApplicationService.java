package hk.edu.cpce.gp4701.application;

import hk.edu.cpce.gp4701.model.Account;
import hk.edu.cpce.gp4701.model.AdmissionApplication;
import hk.edu.cpce.gp4701.model.ProgrammeLevel;
import hk.edu.cpce.gp4701.school.ProgrammeLevelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class ApplicationService {

    private final AdmissionApplicationRepository applicationRepository;
    private final ProgrammeLevelRepository programmeLevels;

    public ApplicationService(AdmissionApplicationRepository applicationRepository,
                              ProgrammeLevelRepository programmeLevels) {
        this.applicationRepository = applicationRepository;
        this.programmeLevels = programmeLevels;
    }

    @Transactional
    public AdmissionApplication submitApplication(Account member, Long programmeId, Integer level) throws Exception {
        ProgrammeLevel progLevel = programmeLevels.findAll().stream()
                .filter(pl -> pl.getProgramme().getId().equals(programmeId) && pl.getLevel().equals(level))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No admission seats are offered for this specific program and level."));

        LocalDateTime nowHK = LocalDateTime.now(ZoneId.of("Asia/Hong_Kong"));
        if (nowHK.isAfter(progLevel.getProgramme().getApplicationDeadline())) {
            throw new IllegalStateException("Submission rejected: The application deadline for this program has passed.");
        }

        boolean hasDuplicate = applicationRepository.findByMemberOrderBySubmittedAtDesc(member).stream()
                .anyMatch(app -> app.getProgrammeLevel().getId().equals(progLevel.getId()));
        
        if (hasDuplicate) {
            throw new IllegalStateException("Duplicate entry: You have already submitted an application for this program and level.");
        }

        AdmissionApplication application = new AdmissionApplication();
        application.setMember(member); // Relationship to Account (assumes JPA @ManyToOne mapping)
        application.setProgrammeLevel(progLevel);
        application.setSubmittedAt(nowHK);
        application.setDecision(null);        
        application.setPublishedAt(null);      
        application.setOfferResponse(null);    
        application.setRespondedAt(null);

        return applicationRepository.save(application);
    }

    @Transactional
    public void recordOfferResponse(Long id, Account member, String response) {
        AdmissionApplication app = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found."));

        if (!app.getMember().getId().equals(member.getId())) {
            throw new SecurityException("Access denied.");
        }
        if (app.getPublishedAt() == null || !"Success".equalsIgnoreCase(app.getDecision())) {
            throw new IllegalStateException("No active published offer is available to respond to.");
        }
        if (app.getOfferResponse() != null) {
            throw new IllegalStateException("Response locked: You have already submitted a response for this offer.");
        }

        app.setOfferResponse(response);
        app.setRespondedAt(LocalDateTime.now(ZoneId.of("Asia/Hong_Kong")));
        
        applicationRepository.save(app);
    }
}