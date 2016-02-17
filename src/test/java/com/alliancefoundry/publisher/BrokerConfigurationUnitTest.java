package com.alliancefoundry.publisher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import com.alliancefoundry.publisher.BrokersConfiguration.BrokerConfiguration;

public class BrokerConfigurationUnitTest {
	private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
	
	@Test
	public void setTestBrokerProperties() {
		EnvironmentTestUtils.addEnvironment(context, 
				"brokers.kafka.name=kafka");
		context.register(PropertyPlaceholderAutoConfiguration.class, TestConfiguration.class);
		context.refresh();
		
		BrokersConfiguration brokersConfiguration = context.getBean(BrokersConfiguration.class);
		assertNotNull(brokersConfiguration);
		assertEquals(1, brokersConfiguration.brokers.size());
		assertEquals("kafka", brokersConfiguration.getBrokers().get("kafka").getName());
	}
	
	@Test
	public void setTestMultipleFullBrokerProperties() {
		// either style of populating the broker map will work
		EnvironmentTestUtils.addEnvironment(context, 
				"brokers.kafka.name=kafka",
				"brokers.kafka.className=com.alliancefoundry.publisher.impl.kafka.KafkaPublisher",
				"brokers.kafka.url=localhost:9092",

				"brokers[amq].name=activemqpub",
				"brokers[amq].className=com.alliancefoundry.publisher.impl.amq.ActiveMQPublisher",
				"brokers[amq].url=tcp://localhost:61616"
				);
		
		context.register(PropertyPlaceholderAutoConfiguration.class, TestConfiguration.class);
		context.refresh();
		
		BrokersConfiguration brokersConfiguration = context.getBean(BrokersConfiguration.class);
		assertNotNull(brokersConfiguration);
		assertEquals(2, brokersConfiguration.brokers.size());
		
		BrokerConfiguration broker0 = brokersConfiguration.getBrokers().get("kafka");
		assertEquals("kafka", broker0.getName());
		assertEquals("com.alliancefoundry.publisher.impl.kafka.KafkaPublisher", broker0.getClassName());
		assertEquals("localhost:9092", broker0.getUrl());

		BrokerConfiguration broker1 = brokersConfiguration.getBrokers().get("amq");
		assertEquals("activemqpub", broker1.getName());
		assertEquals("com.alliancefoundry.publisher.impl.amq.ActiveMQPublisher", broker1.getClassName());
		assertEquals("tcp://localhost:61616", broker1.getUrl());

	}
	
	@Configuration
	@EnableConfigurationProperties(BrokersConfiguration.class)
	protected static class TestConfiguration {
	}
}
