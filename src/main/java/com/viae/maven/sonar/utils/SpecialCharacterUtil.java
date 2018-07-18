package com.viae.maven.sonar.utils;

import java.util.Optional;

/**
 * @author by Maarten on 25/09/2016.
 */
public class SpecialCharacterUtil {

	public static String makeStringFreeOfSpecialCharacters( final String value ) {
		return Optional.ofNullable( value ).orElse( "" ).replace( "/", "-" );
	}
}
