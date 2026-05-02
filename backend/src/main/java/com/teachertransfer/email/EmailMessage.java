package com.teachertransfer.email;

public record EmailMessage(
    String to,
    String subject,
    String htmlBody,
    EmailType type
) {}
