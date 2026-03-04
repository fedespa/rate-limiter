package com.app.rate_limiter.common.rabbit;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "app.notifications.exchange";
    public static final String QUEUE_EMAIL = "q.notifications.email";
    public static final String ROUTING_KEY_EMAIL = "notifications.email";

    public static final String DLQ_EXCHANGE = "app.notifications.dlx";
    public static final String DLQ_EMAIL = "q.notifications.email.dlq";
    public static final String DLQ_ROUTING_KEY = "notifications.email.dlq";

    public static final String QUEUE_INVITATIONS = "q.invitations.email";
    public static final String ROUTING_KEY_INVITATIONS = "invitations.email";

    public static final String DLQ_INVITATIONS = "q.invitations.email.dlq";
    public static final String DLQ_ROUTING_KEY_INVITATIONS = "invitations.email.dlq";

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    // Configuracion DLQ Invitations
    @Bean
    public Queue deadLetterQueueInvitations() {
        return QueueBuilder.durable(DLQ_INVITATIONS).build();
    }

    @Bean
    public Binding dlqBindingInvitations() {
        return BindingBuilder.bind(deadLetterQueueInvitations())
                .to(deadLetterExchange())
                .with(DLQ_ROUTING_KEY_INVITATIONS);
    }

    // Configuracion cola de invitations
    @Bean
    public Queue invitationQueue(){
        return QueueBuilder.durable(QUEUE_INVITATIONS)
                .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY_INVITATIONS)
                .build();
    }

    @Bean
    public Binding invitationBinding() {
        return BindingBuilder
                .bind(invitationQueue())
                .to(exchange())
                .with(ROUTING_KEY_INVITATIONS);
    }

    // Configuracion de DLQ
    @Bean
    public TopicExchange deadLetterExchange(){
        return new TopicExchange(DLQ_EXCHANGE);
    }

    @Bean
    public Queue deadLetterQueue(){
        return QueueBuilder.durable(DLQ_EMAIL).build();
    }

    @Bean
    public Binding dlqBinding(){
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(DLQ_ROUTING_KEY);
    }

    // Configuracion de Email
    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue emailQueue(){
        return QueueBuilder.durable(QUEUE_EMAIL)
                .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding emailBinding(){
        return BindingBuilder
                .bind(emailQueue())
                .to(exchange())
                .with(ROUTING_KEY_EMAIL);
    }

}
