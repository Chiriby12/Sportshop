package com.sportshop.notifications.infraestructure.driver_adapters.email;

import com.sportshop.notifications.domain.model.gateway.EmailSenderGateway;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class EmailSenderGatewayImpl implements EmailSenderGateway {

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML
            mailSender.send(message);
            log.info("Email enviado a: {}", to);
        } catch (Exception e) {
            // No lanzamos excepción para no afectar el flujo principal
            log.warn("No se pudo enviar el email a {}: {}", to, e.getMessage());
        }
    }
}
