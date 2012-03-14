package fr.imag.recommender;

import java.io.IOException;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.junit.Test;

public class GithubAPITest {

	@Test
	public void test() throws IOException {
		//GitHubClient client = new GitHubClient();
		//client.setCredentials("user", "password");

		RepositoryService service = new RepositoryService();
		for (Repository repo : service.getRepositories("defunkt"))
			System.out.println(repo.getName() + " Watchers: " + repo.getWatchers());
	}
}
