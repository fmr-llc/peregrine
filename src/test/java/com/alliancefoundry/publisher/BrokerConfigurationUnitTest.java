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
				"brokers[0].name=kafka");
		context.register(PropertyPlaceholderAutoConfiguration.class, TestConfiguration.class);
		context.refresh();
		
		BrokersConfiguration brokersConfiguration = context.getBean(BrokersConfiguration.class);
		assertNotNull(brokersConfiguration);
		assertEquals(1, brokersConfiguration.brokers.size());
		assertEquals("kafka", brokersConfiguration.getBrokers().get(0).getName());
	}
	
	@Test
	public void setTestMultipleFullBrokerProperties() {
		EnvironmentTestUtils.addEnvironment(context, 
				"brokers[0].name=kafka",
				"brokers[0].className=com.alliancefoundry.publisher.impl.kafka.KafkaPublisher",
				"brokers[0].url=localhost:9092",

				"brokers[1].name=activemqpub",
				"brokers[1].className=com.alliancefoundry.publisher.impl.amq.ActiveMQPublisher",
				"brokers[1].url=tcp://localhost:61616"
				);
		
		context.register(PropertyPlaceholderAutoConfiguration.class, TestConfiguration.class);
		context.refresh();
		
		BrokersConfiguration brokersConfiguration = context.getBean(BrokersConfiguration.class);
		assertNotNull(brokersConfiguration);
		assertEquals(2, brokersConfiguration.brokers.size());
		
		BrokerConfiguration broker0 = brokersConfiguration.getBrokers().get(0);
		assertEquals("kafka", broker0.getName());
		assertEquals("com.alliancefoundry.publisher.impl.kafka.KafkaPublisher", broker0.getClassName());
		assertEquals("localhost:9092", broker0.getUrl());

		BrokerConfiguration broker1 = brokersConfiguration.getBrokers().get(1);
		assertEquals("activemqpub", broker1.getName());
		assertEquals("com.alliancefoundry.publisher.impl.amq.ActiveMQPublisher", broker1.getClassName());
		assertEquals("tcp://localhost:61616", broker1.getUrl());

	}
	
	@Configuration
	@EnableConfigurationProperties(BrokersConfiguration.class)
	protected static class TestConfiguration {
	}
}
