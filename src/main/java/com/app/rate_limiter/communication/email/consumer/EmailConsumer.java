package com.app.rate_limiter.communication.email.consumer;

import com.app.rate_limiter.common.rabbit.RabbitMQConfig;
import com.app.rate_limiter.communication.email.dto.EmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailConsumer {

    // private final JavaMailSender mailSender;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_EMAIL)
    public void consumeEmailMessage(EmailMessage message) throws Exception {
        log.info("Recibido mensaje de la cola: enviando email a {}", message.to());

        try {
            sendEmail(message);
            log.info("Email enviado exitosamente a {}!",  message.to());
        } catch (Exception e) {
            log.warn("Fallo al enviar email a {}. Reintentando...", message.to());
            throw e;
        }

    }

    private void sendEmail(EmailMessage message) throws Exception {
        // Lógica de SMTP aquí
        // Si hay un error de conexión, lanzará Exception
    }

}
