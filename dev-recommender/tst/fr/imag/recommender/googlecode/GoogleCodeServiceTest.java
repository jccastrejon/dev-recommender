package fr.imag.recommender.googlecode;

import java.io.IOException;

import org.junit.Test;

import fr.imag.recommender.common.PastUsageData;
import fr.imag.recommender.googlecode.GoogleCodeService;

public class GoogleCodeServiceTest {

	@Test
	public void testGetUsageData() throws IOException {
		PastUsageData usageData;

		// Existing user
		usageData = GoogleCodeService.getPastUsageData("jccastrejon");

		// Non-existing user
		usageData = GoogleCodeService.getPastUsageData("jccastrejon2");
	}
}
