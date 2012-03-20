package fr.imag.recommender.github;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.imag.recommender.common.PastUsageData;

public class GitHubServiceTest {

	@Test
	public void testGetCurrentUsageData() {
		CurrentUsageData usageData;

		// Existing user
		usageData = GitHubService.getCurrentUsageData("jccastrejon");
		assertNotNull(usageData.getCommitFiles());

		// Non-existing user
		usageData = GitHubService.getCurrentUsageData("jccastrejon-non-existent");
		assertTrue(usageData.getCommitFiles().size() == 0);
	}

	@Test
	public void testGetPastUsageData() {
		PastUsageData usageData;

		// Existing user
		usageData = GitHubService.getPastUsageData("jccastrejon");

		// Non-existing user
		usageData = GitHubService.getPastUsageData("jccastrejon-non-existent");
	}
}