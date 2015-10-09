package com.alliancefoundry.tests;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

import junit.framework.Assert;

public class SecurityTests {


	@Test
	public void testAccessRestServiceWithoutPassword() throws ClientProtocolException, IOException {
		
		// access get event reest service
//		Request.Get("http://localhost:8080/event-service/event/34578")
//	    .execute().returnContent();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet("http://localhost:8080/event-service/event/34578");
		CloseableHttpResponse response1 = httpclient.execute(httpGet);
		System.out.println(response1.getStatusLine().getStatusCode());

		// Status code 401 - Unauthorized 401 
		Assert.assertTrue((response1.getStatusLine().getStatusCode() == 401));
		

		
	}

	@Test
	public void testAccessRestServiceWithPassword() throws ClientProtocolException, IOException {
		
		// access get event reest service

		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet("http://ratnesh:ratnesh@localhost:8080/event-service/event/34578");
		CloseableHttpResponse response1 = httpclient.execute(httpGet);
		
		System.out.println(response1.getStatusLine().getStatusCode());
		
		// if we get this far test passes
//		Assert.assertTrue(true);
	}

}
