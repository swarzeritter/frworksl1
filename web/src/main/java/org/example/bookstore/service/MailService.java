package org.example.bookstore.service;

import jakarta.mail.internet.MimeMessage;
import org.example.bookstore.domain.BookEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateProcessor templateProcessor;

    public MailService(JavaMailSender mailSender, EmailTemplateProcessor templateProcessor) {
        this.mailSender = mailSender;
        this.templateProcessor = templateProcessor;
    }

    public void sendNewBookEmail(BookEntity book) {
        Map<String, Object> model = new HashMap<>();
        model.put("title", book.getTitle());
        model.put("author", book.getAuthor());
        model.put("year", book.getYear());
        
        // Format date in Java to avoid FreeMarker configuration issues
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        model.put("added", book.getAddedAt().format(formatter));

        String html = templateProcessor.process("new_book.ftl", model);

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            helper.setTo("testmail@gmail.com"); // Replace with actual recipient
            helper.setSubject("Нова книга в каталозі: " + book.getTitle());
            helper.setText(html, true);
            helper.setFrom("bookapp@somecool.email.com");

            mailSender.send(message);

        } catch (Exception e) {
            // Log error but don't fail the request
            e.printStackTrace();
        }
    }
}

