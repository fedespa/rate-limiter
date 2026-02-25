package com.app.rate_limiter.communication.email.producer;

import com.app.rate_limiter.common.rabbit.RabbitMQConfig;
import com.app.rate_limiter.communication.email.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmailProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.url-base}")
    private String baseUrl;

    public void sendVerificationEmail(String to, String token){
        String url = this.baseUrl + "/v1/auth/verify?token=" + token;

        EmailMessage message = new EmailMessage(
                to,
                "Verificá tu cuenta",
                "Haz clic aquí para activar tu cuenta de Rate Limiter: " + url
        );

        log.info("Enviando mensaje de email a la cola para: {}", to);

        this.rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_EMAIL,
                message
        );

    }

}
