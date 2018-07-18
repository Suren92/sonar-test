package com.viae.maven.sonar.config;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertTrue;

/**
 * Created by Maarten on 01/06/2016.
 */
public class TestSonarPropertyNames {

	@Test
	public void testConstructorIsPrivate() throws Throwable {
		final Constructor<SonarStrings> constructor = SonarStrings.class.getDeclaredConstructor();
		assertTrue( Modifier.isPrivate( constructor.getModifiers() ) );
		constructor.setAccessible( true );
		constructor.newInstance();
	}
}