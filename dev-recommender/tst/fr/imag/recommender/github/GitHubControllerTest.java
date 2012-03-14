package fr.imag.recommender.github;

import static org.junit.Assert.*;
import java.io.IOException;

import org.junit.Test;

public class GitHubControllerTest {

	@Test
	public void test() throws IOException {
		UsageData usageData;

		usageData = GitHubController.getUserData("jccastrejon");
		assertNotNull(usageData);
	}
}
