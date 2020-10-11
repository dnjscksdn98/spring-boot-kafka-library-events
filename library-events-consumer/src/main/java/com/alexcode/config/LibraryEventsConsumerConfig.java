package com.alexcode.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties.AckMode;

@Configuration
@EnableKafka
public class LibraryEventsConsumerConfig {

	@Autowired
	private KafkaProperties properties;

	/**
	 * Description:
	 * 		- factory.getContainerProperties().setAckMode(AckMode.MANUAL);
	 * 	    - sets the option for committing offsets to MANUAL mode
	 * 	  - factory.setConcurrency(3);
	 * 	    - configures multiple kafka listeners from the same application itself
	 *
	 */
	@Bean
	ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
					ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
					ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory) {

		ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
		configurer.configure(factory, kafkaConsumerFactory
						.getIfAvailable(() -> new DefaultKafkaConsumerFactory<>(this.properties.buildConsumerProperties())));
//		factory.getContainerProperties().setAckMode(AckMode.MANUAL);
		factory.setConcurrency(3);
		return factory;
	}
}
