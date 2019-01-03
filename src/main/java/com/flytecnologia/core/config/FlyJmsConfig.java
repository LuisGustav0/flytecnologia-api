package com.flytecnologia.core.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.jms.ConnectionFactory;


@Configuration
@EnableJms
public class FlyJmsConfig {
    private final static String BROKER_URL = "vm://localhost?broker.persistent=false";

    @Bean
    public ActiveMQConnectionFactory activeMqConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(BROKER_URL);

        return connectionFactory;
    }

    @Bean
    public JmsListenerContainerFactory<?> flyJmsFactory(ConnectionFactory connectionFactory,
                                                        DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        // This provides all boot's default to this factory, including the message converter
        factory.setTransactionManager(jmsTransactionManager());
        factory.setCacheLevelName("CACHE_CONNECTION"); //<-- the line fixed the problem
        configurer.configure(factory, connectionFactory);

        // You could still override some of Boot's default if necessary.
        return factory;
    }

    @Bean // Serialize message content to json using TextMessage
    public MessageConverter jacksonJmsMessageConverter() {
        return new SimpleMessageConverter();
    }

    @Bean(name = "jmsTransactionManager")
    @DependsOn(value = {"activeMqConnectionFactory"})
    public PlatformTransactionManager jmsTransactionManager() {
        JmsTransactionManager transactionManager = new JmsTransactionManager();
        transactionManager.setConnectionFactory(activeMqConnectionFactory());
        return transactionManager;
    }
}
