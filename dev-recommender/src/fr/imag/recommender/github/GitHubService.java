package fr.imag.recommender.github;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.egit.github.core.CommitFile;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;

/**
 * 
 * @author jccastrejon
 * 
 */
public class GitHubService {
	private final static UserService userService = new UserService();
	private final static IssueService issueService = new IssueService();
	private final static CommitService commitService = new CommitService();
	private final static RepositoryService repositoryService = new RepositoryService();

	/**
	 * 
	 * @param login
	 * @return
	 * @throws IOException
	 */
	public static UsageData getUserData(final String login) throws IOException {
		User user;
		List<Issue> issues;
		UsageData returnValue;
		List<CommitFile> commitFiles;

		returnValue = null;
		user = GitHubService.userService.getUser(login);

		if (user != null) {
			issues = new ArrayList<Issue>();
			commitFiles = new ArrayList<CommitFile>();

			for (Repository repository : GitHubService.repositoryService.getRepositories(login)) {
				// Current work
				for (RepositoryCommit commit : GitHubService.commitService.getCommits(repository)) {
					if (user.getLogin().equals(commit.getCommitter().getLogin())) {
						commitFiles.addAll(GitHubService.commitService.getCommit(repository, commit.getSha())
						        .getFiles());
					}
				}

				// Pending work
				for (Issue issue : GitHubService.issueService.getIssues(login, repository.getName(), null)) {
					if (user.getLogin().equals(issue.getAssignee().getLogin())) {
						if (issue.getState().equals("open")) {
							issues.add(GitHubService.issueService.getIssue(repository, issue.getNumber()));
						}
					}
				}
			}

			// Gather work data
			returnValue = new UsageData(issues, commitFiles);
		}

		return returnValue;
	}
}
