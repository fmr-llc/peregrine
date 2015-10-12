package com.alliancefoundry.tests.PublisherTests.KafkaTests;

public class RestConsumerTest {

	public static void main(String[] args) {
		
		String topicName = "testIntegration3";
		KafkaSubscriber kafkaSubscriber = new KafkaSubscriber(topicName);
		System.out.println(kafkaSubscriber.consumeEvent());

	}

}
