package com.alliancefoundry.controller;

import java.io.File;
import java.nio.file.Paths;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Temporary required until changes are made to the way we configure Event Service
 * via properties. Must be used as a test runner for all integration tests.
 * 
 * @author Peter
 */
public class EventServiceTestRunner extends SpringJUnit4ClassRunner {

	public EventServiceTestRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
		System.setProperty("propsroot", Paths.get("target", "classes").toAbsolutePath().toString()+"/");			
	}
}