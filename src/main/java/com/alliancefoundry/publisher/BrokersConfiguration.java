package com.alliancefoundry.publisher;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Used to capture broker configuration information. Bound directly to application*.properties via
 * Spring Boot.
 * 
 * @author Peter
 */
@ConfigurationProperties
@Component
public class BrokersConfiguration {
	List<BrokerConfiguration> brokers = new ArrayList<>();

	public List<BrokerConfiguration> getBrokers() {
		return brokers;
	}

	public void setBrokers(List<BrokerConfiguration> brokers) {
		this.brokers = brokers;
	}

	/**
	 * Class representing configuration of a single broker.
	 * 
	 * @author Peter
	 */
	public static class BrokerConfiguration {
		@NotNull
		private String name;
		@NotNull
		private String className;
		@NotNull
		private String url;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
	}
}
