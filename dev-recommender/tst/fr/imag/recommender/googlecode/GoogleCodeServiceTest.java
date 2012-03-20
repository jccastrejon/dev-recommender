package fr.imag.recommender.googlecode;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import fr.imag.recommender.common.PastUsageData;
import fr.imag.recommender.googlecode.GoogleCodeService;

public class GoogleCodeServiceTest {

	@Test
	public void testGetPastUsageData() throws IOException {
		PastUsageData usageData;

		// Existing user
		usageData = GoogleCodeService.getPastUsageData("jccastrejon");
		assertTrue(usageData.getProjects().size() > 0);

		// Non-existing user
		usageData = GoogleCodeService.getPastUsageData("jccastrejon-non-existent");
		assertTrue(usageData.getProjects().isEmpty());
	}
}
