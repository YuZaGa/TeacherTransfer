package com.teachertransfer.email.config;

import com.teachertransfer.email.EmailSender;
import com.teachertransfer.email.LogEmailSender;
import com.teachertransfer.email.SmtpEmailSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class EmailConfig {

    @Bean
    @ConditionalOnProperty(name = "app.email.provider", havingValue = "smtp", matchIfMissing = true)
    public EmailSender smtpEmailSender(JavaMailSender mailSender) {
        return new SmtpEmailSender(mailSender);
    }

    @Bean
    @ConditionalOnProperty(name = "app.email.provider", havingValue = "log")
    public EmailSender logEmailSender() {
        return new LogEmailSender();
    }
}
