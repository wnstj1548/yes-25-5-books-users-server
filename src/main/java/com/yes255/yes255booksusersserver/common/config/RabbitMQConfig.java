package com.yes255.yes255booksusersserver.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue birthdayCouponQueue() {
        return new Queue("birthdayQueue", true);
    }

    @Bean
    public Queue welcomeCouponQueue() {
        return new Queue("welcomeQueue", true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("couponExchange");
    }

    @Bean
    public Binding birthdayCouponBinding(Queue birthdayCouponQueue, DirectExchange exchange) {
        return BindingBuilder.bind(birthdayCouponQueue).to(exchange).with("birthdayCoupon");
    }

    @Bean
    public Binding welcomeCouponBinding(Queue welcomeCouponQueue, DirectExchange exchange) {
        return BindingBuilder.bind(welcomeCouponQueue).to(exchange).with("welcomeCoupon");
    }
}