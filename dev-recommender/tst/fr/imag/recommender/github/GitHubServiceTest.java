package fr.imag.recommender.github;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

public class GitHubServiceTest {

	@Test
	public void test() throws IOException {
		UsageData usageData;

		usageData = GitHubService.getUserData("jccastrejon");
		assertNotNull(usageData);
	}
}
