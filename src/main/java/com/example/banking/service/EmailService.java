package com.example.banking.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String from;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");


    @Async
    public void sendWelcomeEmail(String to, String name) {
        Context context = new Context();
        context.setVariable("name", name);

        sendEmail(to, "Welcome to Digital Banking",
                "emails/welcome", context);
    }

    @Async
    public void sendTransactionEmail(String to, String name,
                                     String type, BigDecimal amount,
                                     String accountNumber,
                                     BigDecimal balanceAfter,
                                     String description) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("type", type);
        context.setVariable("amount", amount);
        context.setVariable("accountNumber", accountNumber);
        context.setVariable("balanceAfter", balanceAfter);
        context.setVariable("description", description);
        context.setVariable("date", LocalDateTime.now().format(FORMATTER));

        String subject = switch (type) {
            case "DEPOSIT"    -> " Deposit Confirmed";
            case "WITHDRAWAL" -> "Withdrawal Confirmed";
            case "TRANSFER"   -> "Transfer Confirmed";
            default           -> "Transaction Notification";
        };

        sendEmail(to, subject, "emails/transaction", context);
    }

    @Async
    public void sendFraudAlertEmail(String to, String name,
                                    String reason, String accountNumber,
                                    String details) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("reason", reason);
        context.setVariable("accountNumber", accountNumber);
        context.setVariable("details", details);
        context.setVariable("date", LocalDateTime.now().format(FORMATTER));

        sendEmail(to, "Fraud Alert - Suspicious Activity Detected",
                "emails/fraud-alert", context);
    }

    private void sendEmail(String to, String subject,
                           String template, Context context) {
        try {
            String html = templateEngine.process(template, context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("📧 Email sent to {} - {}", to, subject);

        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
