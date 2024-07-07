package com.yes255.yes255booksusersserver.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue birthdayCouponQueue() {
        return QueueBuilder.durable("yes255BirthdayQueue")
                .withArgument("x-dead-letter-exchange", "yes255DlxExchange")
                .withArgument("x-dead-letter-routing-key", "yes255.dead.birthdayCoupon")
                .withArgument("x-message-ttl", 60000) // 60초 뒤에 만료 -> Dead Letter 교환기로 이동
                .withArgument("x-max-length", 1000) // 최대 메시지 길이
                .build();
    }

    @Bean
    public Queue welcomeCouponQueue() {
        return QueueBuilder.durable("yes255WelcomeQueue")
                .withArgument("x-dead-letter-exchange", "yes255DlxExchange")
                .withArgument("x-dead-letter-routing-key", "yes255.dead.welcomeCoupon")
                .withArgument("x-message-ttl", 60000)
                .withArgument("x-max-length", 1000)
                .build();
    }

    @Bean
    public Queue dlqBirthdayCouponQueue() {
        return new Queue("yes255.dead.birthdayCoupon");
    }

    @Bean
    public Queue dlqWelcomeCouponQueue() {
        return new Queue("yes255.dead.welcomeCoupon");
    }

    @Bean
    public DirectExchange couponExchange() {
        return new DirectExchange("yes255CouponExchange");
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("yes255DlxExchange");
    }

    @Bean
    public Binding birthdayCouponBinding(Queue birthdayCouponQueue, DirectExchange couponExchange) {
        return BindingBuilder.bind(birthdayCouponQueue)
                .to(couponExchange)
                .with("yes255BirthdayCouponRoutingKey");
    }

    @Bean
    public Binding welcomeCouponBinding(Queue welcomeCouponQueue, DirectExchange couponExchange) {
        return BindingBuilder.bind(welcomeCouponQueue)
                .to(couponExchange)
                .with("yes255WelcomeCouponRoutingKey");
    }

    @Bean
    public Binding dlxBirthdayCouponBinding(Queue dlqBirthdayCouponQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(dlqBirthdayCouponQueue)
                .to(deadLetterExchange)
                .with("yes255.dead.birthdayCoupon");
    }

    @Bean
    public Binding dlxWelcomeCouponBinding(Queue dlqWelcomeCouponQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(dlqWelcomeCouponQueue)
                .to(deadLetterExchange)
                .with("yes255.dead.welcomeCoupon");
    }
}