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

public class MessageTypeConfigurationUnitTest {
	private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
	
	@Test
	public void setTestMessageProperties() {
		EnvironmentTestUtils.addEnvironment(context, 
				"message.type.test-message=activemqpub|queue://test1.queue");
		context.register(PropertyPlaceholderAutoConfiguration.class, TestConfiguration.class);
		context.refresh();
		
		MessageTypeConfiguration messageTypeConfiguration = context.getBean(MessageTypeConfiguration.class);
		assertNotNull(messageTypeConfiguration);
		assertEquals(1, messageTypeConfiguration.type.size());
		assertEquals(1, messageTypeConfiguration.typedMessageDestinations.size());
		assertNotNull(messageTypeConfiguration.typedMessageDestinations.get("test-message"));
		assertEquals("activemqpub", messageTypeConfiguration.typedMessageDestinations.get("test-message").publisher);
		assertEquals("queue://test1.queue", messageTypeConfiguration.typedMessageDestinations.get("test-message").destination);
	}
	
	@Configuration
	@EnableConfigurationProperties(MessageTypeConfiguration.class)
	protected static class TestConfiguration {
	}
}
