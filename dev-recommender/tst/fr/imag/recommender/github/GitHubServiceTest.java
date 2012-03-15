package fr.imag.recommender.github;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

public class GitHubServiceTest {

	@Test
	public void testGetUsageData() throws IOException {
		UsageData usageData;

		// Existing user
		usageData = GitHubService.getUsageData("jccastrejon");
		assertNotNull(usageData.getCommitFiles());

		// Non-existing user
		usageData = GitHubService.getUsageData("jccastrejon-non-existent");
		assertTrue(usageData.getCommitFiles().size() == 0);
	}
}