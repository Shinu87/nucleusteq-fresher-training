package com.capstone.interviewtracker.service.impl;

import com.capstone.interviewtracker.model.Candidate;
import com.capstone.interviewtracker.model.Interview;
import com.capstone.interviewtracker.model.Panel;
import com.capstone.interviewtracker.service.EmailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

/**
 * Handles sending emails using SMTP.
 *
 * Supports interview notifications, panel activation,
 * and candidate onboarding emails.
 */
@Service
public class EmailServiceImpl implements EmailService {

        private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a");

        private final JavaMailSender mailSender;

        @Value("${spring.mail.username}")
        private String fromEmail;

        public EmailServiceImpl(JavaMailSender mailSender) {
                this.mailSender = mailSender;
        }

        /**
         * Sends interview assignment email to panel member.
         *
         * @param panel     panel member
         * @param interview interview details
         */
        @Override
        public void sendInterviewAssignmentEmail(
                        Panel panel,
                        Interview interview) {

                try {

                        String candidateName = interview.getCandidate().getName();

                        String jobTitle = interview.getCandidate()
                                        .getJobDescription()
                                        .getTitle();

                        String stage = interview.getStage().name();

                        String scheduledAt = interview.getScheduledAt()
                                        .format(FORMATTER);

                        String focusArea = interview.getFocusArea() != null
                                        ? interview.getFocusArea()
                                        : "General";

                        String meetingUrl = interview.getMeetingUrl();

                        String meetingLine = (meetingUrl != null && !meetingUrl.isBlank())
                                        ? "Meeting URL    : " + meetingUrl + "\n"
                                        : "Meeting URL    : (TBD by HR)\n";

                        String subject = "Interview Assignment: "
                                        + stage
                                        + " - "
                                        + candidateName;

                        String body = "Dear " + panel.getName() + ",\n\n"
                                        + "You have been assigned an interview.\n\n"
                                        + "------------------------------\n"
                                        + "Candidate     : " + candidateName + "\n"
                                        + "Job Role      : " + jobTitle + "\n"
                                        + "Stage         : " + stage + "\n"
                                        + "Scheduled At  : " + scheduledAt + "\n"
                                        + "Focus Area    : " + focusArea + "\n"
                                        + meetingLine
                                        + "------------------------------\n\n"
                                        + "Please check the system for full details.\n\n"
                                        + "Regards,\n"
                                        + "Interview Tracking System";

                        sendPlainEmail(panel.getEmail(), subject, body);

                        logger.info(
                                        "Interview email sent to panel {} for interview {}",
                                        panel.getEmail(),
                                        interview.getId());

                } catch (Exception e) {

                        logger.error(
                                        "Failed to send interview email to panel {}",
                                        panel.getEmail(),
                                        e);
                }
        }

        /**
         * Sends panel activation email with password setup link.
         *
         * @param panelEmail      panel email
         * @param panelName       panel name
         * @param setPasswordLink password setup URL
         */
        @Override
        public void sendPanelActivationEmail(
                        String panelEmail,
                        String panelName,
                        String setPasswordLink) {

                try {

                        String subject = "Set Your Password – Interview Tracking System";

                        String plainBody = "Dear " + panelName + ",\n\n"
                                        + "Your panel account has been created.\n\n"
                                        + "Please set your password using the link below:\n\n"
                                        + setPasswordLink + "\n\n"
                                        + "Link valid for 24 hours.\n\n"
                                        + "Login Email: " + panelEmail + "\n\n"
                                        + "You can view interviews, profiles and submit feedback.\n\n"
                                        + "Regards,\n"
                                        + "HR Team";

                        String htmlBody = buildActivationHtml(
                                        panelName,
                                        "Your panel account has been created.",
                                        "Click below to set your password.",
                                        setPasswordLink,
                                        panelEmail,
                                        new String[] {
                                                        "View assigned interviews",
                                                        "Access candidate details",
                                                        "Submit feedback"
                                        },
                                        "Contact HR if this was unexpected.",
                                        "HR Team – Interview System");

                        sendMultipartEmail(
                                        panelEmail,
                                        subject,
                                        plainBody,
                                        htmlBody);

                        logger.info(
                                        "Panel activation email sent to {}",
                                        panelEmail);

                } catch (Exception e) {

                        logger.error(
                                        "Failed to send panel activation email to {}",
                                        panelEmail,
                                        e);
                }
        }

        /**
         * Sends signup email to candidate with password setup link.
         *
         * @param candidateEmail  candidate email
         * @param candidateName   candidate name
         * @param setPasswordLink password setup URL
         */
        @Override
        public void sendCandidateSignupEmail(
                        String candidateEmail,
                        String candidateName,
                        String setPasswordLink) {

                try {

                        String subject = "Welcome – Set Your Password – Interview Tracking System";

                        // Plain text email (fallback)
                        String plainBody = "Dear " + candidateName + ",\n\n"
                                        + "Thank you for signing up.\n\n"
                                        + "Set your password using the link below:\n\n"
                                        + setPasswordLink + "\n\n"
                                        + "Link valid for 24 hours.\n\n"
                                        + "Login Email: " + candidateEmail + "\n\n"
                                        + "You can browse jobs, apply, and track applications.\n\n"
                                        + "Regards,\n"
                                        + "Interview Tracking System";

                        // HTML email version
                        String htmlBody = buildActivationHtml(
                                        candidateName,
                                        "Thank you for signing up.",
                                        "Click below to set your password.",
                                        setPasswordLink,
                                        candidateEmail,
                                        new String[] {
                                                        "Browse job openings",
                                                        "Apply for roles",
                                                        "Track application status"
                                        },
                                        "Ignore if you did not sign up.",
                                        "Interview Tracking System");

                        sendMultipartEmail(
                                        candidateEmail,
                                        subject,
                                        plainBody,
                                        htmlBody);

                        logger.info(
                                        "Candidate signup email sent to {}",
                                        candidateEmail);

                } catch (Exception e) {

                        logger.error(
                                        "Failed to send signup email to {}",
                                        candidateEmail,
                                        e);
                }
        }

        /**
         * Sends HR-initiated onboarding email to candidate with password setup link.
         *
         * @param candidateEmail  candidate email
         * @param candidateName   candidate name
         * @param setPasswordLink password setup URL
         */
        @Override
        public void sendCandidateOnboardingEmail(
                        String candidateEmail,
                        String candidateName,
                        String setPasswordLink) {

                try {

                        String subject = "You've Been Onboarded – Set Your Password – Interview Tracking System";

                        String plainBody = "Dear " + candidateName + ",\n\n"
                                        + "You have been onboarded by HR.\n\n"
                                        + "Set your password using the link below:\n\n"
                                        + setPasswordLink + "\n\n"
                                        + "Link valid for 24 hours.\n\n"
                                        + "Login Email: " + candidateEmail + "\n\n"
                                        + "After login you can complete profile, upload resume, and track progress.\n\n"
                                        + "Regards,\n"
                                        + "Interview Tracking System";

                        String htmlBody = buildActivationHtml(
                                        candidateName,
                                        "You have been onboarded by HR.",
                                        "Click below to set your password.",
                                        setPasswordLink,
                                        candidateEmail,
                                        new String[] {
                                                        "Complete your profile",
                                                        "Upload resume",
                                                        "Track interview progress"
                                        },
                                        "Contact HR if unexpected.",
                                        "Interview Tracking System");

                        sendMultipartEmail(
                                        candidateEmail,
                                        subject,
                                        plainBody,
                                        htmlBody);

                        logger.info(
                                        "Candidate onboarding email sent to {}",
                                        candidateEmail);

                } catch (Exception e) {

                        logger.error(
                                        "Failed to send onboarding email to {}",
                                        candidateEmail,
                                        e);
                }
        }

        /**
         * Sends interview scheduled notification email to the candidate.
         *
         * @param candidate candidate details
         * @param interview interview details
         */
        @Override
        public void sendInterviewScheduledEmail(Candidate candidate, Interview interview) {

                try {

                        String stage = interview.getStage().name();
                        String scheduledAt = interview.getScheduledAt().format(FORMATTER);
                        String focusArea = interview.getFocusArea() != null
                                        ? interview.getFocusArea()
                                        : "General";

                        String meetingUrl = interview.getMeetingUrl();
                        String meetingLine = (meetingUrl != null && !meetingUrl.isBlank())
                                        ? "Meeting URL   : " + meetingUrl + "\n"
                                        : "Meeting URL   : (Will be shared by HR)\n";

                        String panelNames = interview.getPanels()
                                        .stream()
                                        .map(Panel::getName)
                                        .reduce((a, b) -> a + ", " + b)
                                        .orElse("TBD");

                        String jobTitle = candidate.getJobDescription().getTitle();

                        String subject = "Interview Scheduled: " + stage + " Round – " + jobTitle;

                        String body = "Dear " + candidate.getName() + ",\n\n"
                                        + "Your interview has been scheduled. Please find the details below:\n\n"
                                        + "------------------------------\n"
                                        + "Job Role      : " + jobTitle + "\n"
                                        + "Stage         : " + stage + "\n"
                                        + "Scheduled At  : " + scheduledAt + "\n"
                                        + "Focus Area    : " + focusArea + "\n"
                                        + "Interviewer(s): " + panelNames + "\n"
                                        + meetingLine
                                        + "------------------------------\n\n"
                                        + "Please be available 5 minutes before the scheduled time.\n\n"
                                        + "Regards,\n"
                                        + "Interview Tracking System";

                        sendPlainEmail(candidate.getEmail(), subject, body);

                        logger.info(
                                        "Interview scheduled email sent to candidate {} for interview {}",
                                        candidate.getEmail(),
                                        interview.getId());

                } catch (Exception e) {

                        logger.error(
                                        "Failed to send interview scheduled email to candidate {}",
                                        candidate.getEmail(),
                                        e);
                }
        }

        /*
         * Private helper methods
         */

        /**
         * Sends a plain text email using SimpleMailMessage.
         *
         * @param to      recipient email
         * @param subject email subject
         * @param body    email content
         */
        private void sendPlainEmail(String to, String subject, String body) {

                SimpleMailMessage message = new SimpleMailMessage();

                message.setFrom(fromEmail);
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);

                mailSender.send(message);
        }

        /**
         * Sends a multipart email with plain text and HTML content.
         *
         * @param to        recipient email
         * @param subject   email subject
         * @param plainBody plain text content
         * @param htmlBody  HTML content
         * @throws Exception if email sending fails
         */
        private void sendMultipartEmail(
                        String to,
                        String subject,
                        String plainBody,
                        String htmlBody) throws Exception {

                MimeMessage mime = mailSender.createMimeMessage();

                MimeMessageHelper helper = new MimeMessageHelper(
                                mime,
                                true,
                                StandardCharsets.UTF_8.name());

                helper.setFrom(fromEmail);
                helper.setTo(to);
                helper.setSubject(subject);

                helper.setText(plainBody, htmlBody);

                mailSender.send(mime);
        }

        /**
         * Builds HTML content for activation emails.
         * Includes a password setup button and fallback link.
         *
         * @param name       user name
         * @param intro      intro message
         * @param cta        call-to-action text
         * @param link       activation URL
         * @param loginEmail login email
         * @param bullets    feature list
         * @param disclaimer footer disclaimer
         * @param signoff    email sign-off text
         * @return HTML email content
         */
        private String buildActivationHtml(
                        String name,
                        String intro,
                        String cta,
                        String link,
                        String loginEmail,
                        String[] bullets,
                        String disclaimer,
                        String signoff) {

                String safeLink = htmlEscape(link);

                StringBuilder bulletsHtml = new StringBuilder();
                for (String b : bullets) {
                        bulletsHtml.append("<li style=\"margin:6px 0;\">")
                                        .append(htmlEscape(b))
                                        .append("</li>");
                }

                return "<!DOCTYPE html>"
                                + "<html><head><meta charset=\"UTF-8\"></head>"
                                + "<body style=\"margin:0;padding:0;background:#f4f4f8;"
                                + "font-family:Segoe UI,Arial,sans-serif;color:#1a1a2e;\">"
                                + "<div style=\"max-width:560px;margin:24px auto;background:#ffffff;"
                                + "border-radius:12px;padding:28px 32px;"
                                + "box-shadow:0 2px 12px rgba(0,0,0,0.06);\">"
                                + "<h2 style=\"margin:0 0 16px;color:#5b21b6;font-size:20px;\">"
                                + "Interview Tracking System</h2>"
                                + "<p style=\"font-size:15px;\">Dear " + htmlEscape(name) + ",</p>"
                                + "<p style=\"font-size:14px;line-height:1.55;\">" + htmlEscape(intro) + "</p>"
                                + "<p style=\"font-size:14px;line-height:1.55;\">" + htmlEscape(cta) + "</p>"
                                + "<p style=\"text-align:center;margin:28px 0;\">"
                                + "<a href=\"" + safeLink + "\" "
                                + "style=\"display:inline-block;padding:12px 28px;"
                                + "background:linear-gradient(135deg,#667eea,#764ba2);"
                                + "color:#ffffff;text-decoration:none;border-radius:8px;"
                                + "font-weight:600;font-size:14px;\">"
                                + "Set Your Password</a></p>"
                                + "<p style=\"font-size:12.5px;color:#666;line-height:1.5;\">"
                                + "If the button does not work, copy this URL:<br/>"
                                + "<a href=\"" + safeLink + "\" style=\"color:#5b21b6;word-break:break-all;\">"
                                + safeLink + "</a></p>"
                                + "<p style=\"font-size:13px;color:#444;\">"
                                + "This link is valid for <b>24 hours</b> and can be used once.</p>"
                                + "<hr style=\"border:none;border-top:1px solid #eee;margin:20px 0;\"/>"
                                + "<p style=\"font-size:13px;\">"
                                + "After setting your password, log in using "
                                + "<b>" + htmlEscape(loginEmail) + "</b>.</p>"
                                + "<p style=\"font-size:13px;margin-bottom:6px;\">You can:</p>"
                                + "<ul style=\"font-size:13px;color:#333;padding-left:18px;margin:0 0 14px;\">"
                                + bulletsHtml
                                + "</ul>"
                                + "<p style=\"font-size:12.5px;color:#777;line-height:1.5;\">"
                                + htmlEscape(disclaimer)
                                + "</p>"
                                + "<p style=\"font-size:13px;margin-top:18px;\">Regards,<br/>"
                                + "<b>" + htmlEscape(signoff) + "</b></p>"
                                + "</div></body></html>";
        }

        /**
         * Escapes HTML special characters in a string.
         * Prevents breaking HTML structure when injecting dynamic values.
         *
         * @param s input string
         * @return escaped string safe for HTML usage
         */
        private String htmlEscape(String s) {
                if (s == null) {
                        return "";
                }

                return s.replace("&", "&amp;")
                                .replace("<", "&lt;")
                                .replace(">", "&gt;")
                                .replace("\"", "&quot;")
                                .replace("'", "&#39;");
        }

}