package fr.imag.recommender.google;

import java.io.IOException;

import org.junit.Test;

public class GoogleServiceTest {

	@Test
	public void testGetUsageData() throws IOException {
		UsageData usageData;

		// Existing user
		usageData = GoogleService.getUsageData("jccastrejon");

		// Non-existing user
		usageData = GoogleService.getUsageData("jccastrejon2");
	}
}
