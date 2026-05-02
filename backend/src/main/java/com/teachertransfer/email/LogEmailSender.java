package com.teachertransfer.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(LogEmailSender.class);

    @Override
    public void send(EmailMessage message) {
        log.info("=== EMAIL (LOG MODE) ===");
        log.info("To:      {}", message.to());
        log.info("Subject: {}", message.subject());
        log.info("Type:    {}", message.type());
        log.info("Body:\n{}", message.htmlBody());
        log.info("=== END EMAIL ===");
    }
}
