package com.teachertransfer.service;

import com.teachertransfer.entity.Teacher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import java.io.IOException;

@Service
public class EmailService {

    @Value("${sendgrid.api-key:}")
    private String sendGridApiKey;

    @Value("${sendgrid.from-email:noreply@teachertransfer.in}")
    private String fromEmail;

    @Value("${sendgrid.from-name:TeacherTransfer}")
    private String fromName;

    public void sendWelcomeEmail(Teacher teacher) {
        if (sendGridApiKey == null || sendGridApiKey.isEmpty()) {
            System.out.println("SendGrid API key not configured. Skipping welcome email for: " + teacher.getEmail());
            return;
        }

        String subject = "Welcome to TeacherTransfer, " + teacher.getName() + "!";
        String htmlContent = buildWelcomeHtml(teacher);

        try {
            Email from = new Email(fromEmail, fromName);
            Email to = new Email(teacher.getEmail());
            Content content = new Content("text/html", htmlContent);
            Mail mail = new Mail(from, subject, to, content);

            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            System.out.println("Welcome email sent to " + teacher.getEmail() + " (status: " + response.getStatusCode() + ")");
        } catch (IOException e) {
            System.err.println("Failed to send welcome email to " + teacher.getEmail() + ": " + e.getMessage());
        }
    }

    private String buildWelcomeHtml(Teacher teacher) {
        return "<!DOCTYPE html>"
            + "<html><head><meta charset='utf-8'></head>"
            + "<body style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'>"
            + "<div style='background: linear-gradient(135deg, #2563eb, #4f46e5); padding: 30px; border-radius: 12px; text-align: center;'>"
            + "<h1 style='color: white; margin: 0;'>Welcome to TeacherTransfer!</h1>"
            + "</div>"
            + "<div style='padding: 30px 20px;'>"
            + "<p style='font-size: 18px; color: #1f2937;'>Hi <strong>" + teacher.getName() + "</strong>,</p>"
            + "<p style='font-size: 16px; color: #4b5563; line-height: 1.6;'>Your account has been created successfully. "
            + "You can now start discovering mutual transfer opportunities with other Bihar government school teachers.</p>"
            + "<div style='background: #f3f4f6; border-radius: 8px; padding: 20px; margin: 20px 0;'>"
            + "<h3 style='color: #1f2937; margin-top: 0;'>What's Next?</h3>"
            + "<ul style='color: #4b5563; line-height: 1.8;'>"
            + "<li>Browse your mutual matches on the Dashboard</li>"
            + "<li>Send transfer interests to teachers you'd like to swap with</li>"
            + "<li>Respond to interests received from others</li>"
            + "</ul></div>"
            + "<p style='font-size: 14px; color: #9ca3af;'>If you have any questions, feel free to reach out to our support team.</p>"
            + "<p style='font-size: 14px; color: #9ca3af;'>The TeacherTransfer Team</p>"
            + "</div></body></html>";
    }
}
