package com.capstone.interviewtracker.service;

import com.capstone.interviewtracker.model.Interview;
import com.capstone.interviewtracker.model.Panel;

/**
 * Service interface for sending email notifications.
 * Handles all email communication related to interviews,
 * onboarding, and account activation.
 */
public interface EmailService {

        /**
         * Sends interview assignment email to a panel member.
         *
         * @param panel     panel member assigned to interview
         * @param interview interview details
         */
        void sendInterviewAssignmentEmail(Panel panel, Interview interview);

        /**
         * Sends panel activation email containing a password setup link.
         * Panel uses this link to set their password and activate account.
         *
         * @param panelEmail      panel email address
         * @param panelName       panel member name
         * @param setPasswordLink password setup URL
         */
        void sendPanelActivationEmail(String panelEmail, String panelName,
                        String setPasswordLink);

        /**
         * Sends candidate signup email containing a password setup link.
         * Candidate uses this link to complete registration and set password.
         *
         * @param candidateEmail  candidate email address
         * @param candidateName   candidate name
         * @param setPasswordLink password setup URL
         */
        void sendCandidateSignupEmail(String candidateEmail, String candidateName,
                        String setPasswordLink);

        /**
         * Sends onboarding email when HR registers a candidate manually.
         * Contains password setup link for account activation.
         *
         * @param candidateEmail  candidate email address
         * @param candidateName   candidate name
         * @param setPasswordLink password setup URL
         */
        void sendCandidateOnboardingEmail(String candidateEmail, String candidateName,
                        String setPasswordLink);
}