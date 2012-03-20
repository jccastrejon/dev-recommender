package fr.imag.recommender.github;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.egit.github.core.CommitFile;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;

import fr.imag.recommender.common.PastUsageData;

/**
 * 
 * @author jccastrejon
 * 
 */
public class GitHubService {
	private final static IssueService issueService = new IssueService();
	private final static CommitService commitService = new CommitService();
	private final static RepositoryService repositoryService = new RepositoryService();

	private static Logger logger = Logger.getLogger(GitHubService.class.getName());

	/**
	 * 
	 * @param login
	 * @return
	 */
	public static PastUsageData getPastUsageData(final String login) {
		PastUsageData returnValue;

		try {
			for (Repository repository : GitHubService.repositoryService.getRepositories(login)) {
				System.out.println(repository.getHtmlUrl());
			}
		} catch (IOException exception) {
			GitHubService.logger.log(Level.INFO, "No usage data found for user: " + login);
		}

		returnValue = null;
		return returnValue;
	}

	/**
	 * 
	 * @param login
	 * @return
	 * @throws IOException
	 */
	public static CurrentUsageData getCurrentUsageData(final String login) {
		List<Issue> issues;
		List<CommitFile> commitFiles;

		issues = new ArrayList<Issue>();
		commitFiles = new ArrayList<CommitFile>();

		try {
			// Gather work data
			for (Repository repository : GitHubService.repositoryService.getRepositories(login)) {
				// Current work
				for (RepositoryCommit commit : GitHubService.commitService.getCommits(repository)) {
					if (login.equals(commit.getCommitter().getLogin())) {
						commitFiles.addAll(GitHubService.commitService.getCommit(repository, commit.getSha())
						        .getFiles());
					}
				}

				// Pending work
				for (Issue issue : GitHubService.issueService.getIssues(login, repository.getName(), null)) {
					if (login.equals(issue.getAssignee().getLogin())) {
						if (issue.getState().equals("open")) {
							issues.add(GitHubService.issueService.getIssue(repository, issue.getNumber()));
						}
					}
				}
			}
		} catch (IOException exception) {
			GitHubService.logger.log(Level.INFO, "No usage data found for user: " + login);
		}

		return new CurrentUsageData(issues, commitFiles);
	}
}
