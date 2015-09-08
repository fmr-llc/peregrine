package com.alliancefoundry.tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class KafkaSubscriber {
	
	private final ConsumerConnector consumer;
	 private final String topic;
	 private final String zookeeper;
	 private final String groupId;
	 
	 public KafkaSubscriber() {
		 
		 this.topic = "test2";
		 this.zookeeper="127.0.0.1:2181";
		 this.groupId= "testgroup";
		 
		 consumer = kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig(zookeeper, groupId));
		   
	 }
	 
	 public static ConsumerConfig createConsumerConfig(String zookeeper, String groupId) {
		    Properties props = new Properties();
		    props.put("zookeeper.connect", zookeeper);
		    props.put("group.id", groupId);
		    props.put("zookeeper.session.timeout.ms", "500");
		    props.put("zookeeper.sync.time.ms", "250");
		    props.put("auto.commit.interval.ms", "1000");

		    return new ConsumerConfig(props);
	}
	 
	 public String consumeEvent() {

		    Map<String, Integer> topicMap = new HashMap<String, Integer>();
		    String result = null;

		    // Define single thread for topic
		    topicMap.put(topic, new Integer(1));

		    Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreamsMap = consumer.createMessageStreams(topicMap);

		    List<KafkaStream<byte[], byte[]>> streamList = consumerStreamsMap.get(topic);

		    for (final KafkaStream<byte[], byte[]> stream : streamList) {
		    	
		      ConsumerIterator<byte[], byte[]> consumerIte = stream.iterator();
		      
		      if (consumerIte.hasNext()){
		    	  result = new String(consumerIte.next().message());
		    	  //System.out.println(result);
		      }

		    }
		    
		    if (consumer != null){
		      consumer.shutdown();
		    }
		    
		    return result;
	  }
	
}


/*
 * 
 	 public void consumeEvent() {

		    Map<String, Integer> topicMap = new HashMap<String, Integer>();

		    // Define single thread for topic
		    topicMap.put(topic, new Integer(1));

		    Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreamsMap = 
		        consumer.createMessageStreams(topicMap);

		    List<KafkaStream<byte[], byte[]>> streamList = consumerStreamsMap
		        .get(topic);

		    for (final KafkaStream<byte[], byte[]> stream : streamList) {
		      ConsumerIterator<byte[], byte[]> consumerIte = stream.iterator();
		      while (consumerIte.hasNext())
		        System.out.println("Event from Single Topic :: "
		          + new String(consumerIte.next().message()));
		    }
		    if (consumer != null)
		      consumer.shutdown();
	  }
 
 * */
 