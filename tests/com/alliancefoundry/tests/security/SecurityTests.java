package com.alliancefoundry.tests.security;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import junit.framework.Assert;

/**
 * 
 * @author Curtis Robinson, Robert Coords
 *
 */
public class SecurityTests {


	@Test
	public void testAccessRestServiceWithoutPassword() throws ClientProtocolException, IOException {
		
		// access get event reest service
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet("http://user:password@localhost:8080/event-service/event/34578");
		CloseableHttpResponse response1 = httpclient.execute(httpGet);

		// Status code 401 - Unauthorized 401 
		Assert.assertTrue((response1.getStatusLine().getStatusCode() == 401));
	
	}

	@Test
	public void testAccessRestServiceWithPassword() throws ClientProtocolException, IOException {
		
		// access get event reest service

		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet("http://user:user@localhost:8080/event-service/event/34578");
		CloseableHttpResponse response1 = httpclient.execute(httpGet);
				
		// Status code 401 - Unauthorized 401 
		Assert.assertTrue((response1.getStatusLine().getStatusCode() == 200) || ((response1.getStatusLine().getStatusCode() == 201)));
	}

}