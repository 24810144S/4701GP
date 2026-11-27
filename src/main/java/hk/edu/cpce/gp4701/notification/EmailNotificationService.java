package hk.edu.cpce.gp4701.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import hk.edu.cpce.gp4701.model.Account;
import hk.edu.cpce.gp4701.model.AdmissionApplication;

@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${app.mail.from:admissions@cis.edu.hk}")
    private String fromAddress;

    /**
     * 電郵 1：註冊 6 位數 OTP 驗證信 (FR02 / BR02)
     */
    public void sendRegistrationOtpEmail(String toEmail, String fullName, String otpCode) {
        String subject = "[CIS Admissions] Verify Your Membership Email (OTP Code)";
        String body = String.format(
                "Dear %s,\n\n" +
                "Thank you for registering for a Christian International School (CIS) Membership account.\n\n" +
                "Your 6-digit email verification code (OTP) is: %s\n\n" +
                "Please enter this code on the verification page to activate your account.\n" +
                "(Note: Unverified accounts cannot log in or submit admission applications.)\n\n" +
                "Best regards,\n" +
                "CIS Admissions Office",
                fullName, otpCode
        );
        dispatchEmail(toEmail, subject, body);
    }

    /**
     * 電郵 2：線上入學申請提交確認信 (FR06)
     */
    public void sendApplicationSubmittedEmail(Account applicant, AdmissionApplication application) {
        String programmeName = application.getProgrammeLevel().getProgramme().getName();
        Byte level = application.getProgrammeLevel().getLevel();

        String subject = String.format("[CIS Admissions] Application #%d Received - %s (S%d)",
                application.getId(), programmeName, level);
        String body = String.format(
                "Dear %s,\n\n" +
                "We have received your online admission application. Below are your submission details:\n\n" +
                "- Application ID: #%d\n" +
                "- Programme: %s\n" +
                "- Entry Level: Secondary %d (S%d)\n" +
                "- Submitted At: %s (HKT)\n" +
                "- Current Status: Pending Review (Unpublished)\n\n" +
                "You will receive another notification email once the Admissions Office publishes your review result.\n\n" +
                "Best regards,\n" +
                "CIS Admissions Office",
                applicant.getFullName(),
                application.getId(),
                programmeName,
                level,
                level,
                application.getSubmittedAt()
        );
        dispatchEmail(applicant.getEmail(), subject, body);
    }

    /**
     * 電郵 3：招生審核結果正式公布通知信 (BR06 / BR10)
     */
    public void sendAdmissionResultPublishedEmail(AdmissionApplication application) {
        Account applicant = application.getMember();
        String programmeName = application.getProgrammeLevel().getProgramme().getName();
        Byte level = application.getProgrammeLevel().getLevel();
        String decision = application.getDecision();

        String actionHint;
        if ("Success".equalsIgnoreCase(decision)) {
            actionHint = "Congratulations! You have been offered admission. Please log in to your CIS Member Portal to ACCEPT or DECLINE your offer.";
        } else if ("Wait".equalsIgnoreCase(decision)) {
            actionHint = "Your application has been placed on the WAITING LIST. If a seat becomes available, our Admissions Office will notify you.";
        } else {
            actionHint = "We regret to inform you that your application was not successful this time. Thank you for your interest in CIS.";
        }

        String subject = String.format("[CIS Admissions] Official Result Published - Application #%d", application.getId());
        String body = String.format(
                "Dear %s,\n\n" +
                "The admission review result for your application has been officially published:\n\n" +
                "- Application ID: #%d\n" +
                "- Programme & Level: %s (S%d)\n" +
                "- Official Decision: %s\n\n" +
                "%s\n\n" +
                "Best regards,\n" +
                "CIS Admissions Office",
                applicant.getFullName(),
                application.getId(),
                programmeName,
                level,
                decision,
                actionHint
        );
        dispatchEmail(applicant.getEmail(), subject, body);
    }

    /**
     * 統一寄信底層：若尚未設定真實 SMTP 帳號，則於 Console 印出完整信件內容（方便測試與演示）
     */
    private void dispatchEmail(String to, String subject, String body) {
        log.info("=== [EMAIL NOTIFICATION] To: {} | Subject: {} ===\n{}", to, subject, body);

        if (mailSender == null || mailUsername == null || mailUsername.isBlank()) {
            log.warn("SMTP credentials not configured; email logged to console only.");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email successfully dispatched to {}", to);
        } catch (Exception ex) {
            log.error("Failed to send SMTP email to {}: {}", to, ex.getMessage());
        }
    }
}