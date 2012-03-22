package fr.imag.recommender.local;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.imag.recommender.common.PastUsageData;

/**
 * 
 * @author jccastrejon
 * 
 */
public class LocalServiceTest {

	@Test
	public void testGetPastUsageData() {
		PastUsageData usageData;

		// Existing user
		usageData = LocalService.getPastUsageData("jccastrejon", "/Users/jccastrejon/java/workspace/PetClinic");
		assertTrue(usageData.getProjects().size() > 0);

		// Non-existing user
		usageData = LocalService.getPastUsageData("jccastrejon", "/Users/jccastrejon/java/workspace/PetClinic-invalid");
		assertTrue(usageData.getProjects().isEmpty());
	}

}
