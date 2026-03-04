package com.app.rate_limiter.communication.email.consumer;

import com.app.rate_limiter.common.rabbit.RabbitMQConfig;
import com.app.rate_limiter.communication.email.dto.EmailMessage;
import com.app.rate_limiter.communication.email.dto.InvitationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class EmailConsumer {

    // private final JavaMailSender mailSender;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_EMAIL)
    public void consumeEmailMessage(EmailMessage message) throws Exception {
        log.info("Recibido mensaje de la cola(queue email): enviando email a {}", message.to());

        try {
            sendEmail(message.to(), message.subject(), message.body());
            log.info("Email enviado exitosamente a {}!",  message.to());
        } catch (Exception e) {
            log.warn("Fallo al enviar email a {}. Reintentando...", message.to());
            throw e;
        }

    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_INVITATIONS)
    public void consumeInvitationMessage(InvitationMessage message) throws Exception {
        log.info("Recibido mensaje de la cola (queue invitations): enviando invitacion a {}", message.to());

        try {
            sendEmail(message.to(), message.subject(), message.body());
            log.info("Invitación enviada exitosamente a {}!",  message.to());
        } catch (Exception e) {
            log.warn("Fallo al enviar invitación a {}. Reintentando...", message.to());
            throw e;
        }
    }



    private void sendEmail(String to, String subject, String body) throws Exception {
        // Lógica para enviar email

        log.info("Se envió correctamente el mail a {}.", to);
    }

}
