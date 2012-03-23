package fr.imag.recommender.github;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.egit.github.core.CommitFile;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import fr.imag.recommender.common.PastUsageData;
import fr.imag.recommender.common.Project;
import fr.imag.recommender.common.UtilService;

/**
 * 
 * @author jccastrejon
 * 
 */
public class GitHubService {

	/**
	 * 
	 */
	private final static String GITHUB_URL = "https://github.com";

	/**
	 * 
	 */
	private final static String GITHUB_RAW_URL = "https://raw.github.com";

	/**
	 * 
	 */
	private final static IssueService issueService = new IssueService();

	/**
	 * 
	 */
	private final static CommitService commitService = new CommitService();

	/**
	 * 
	 */
	private final static RepositoryService repositoryService = new RepositoryService();

	/**
	 * 
	 */
	private static final ExecutorService executorService = Executors.newFixedThreadPool(4);

	/**
	 * 
	 */
	private static Logger logger = Logger.getLogger(GitHubService.class.getName());

	/**
	 * 
	 * @param login
	 * @return
	 */
	public static PastUsageData getPastUsageData(final String login) {
		List<Project> projects;
		List<Future<Project>> candidateProjects;
		Collection<Callable<Project>> tasks;

		tasks = new ArrayList<Callable<Project>>();
		projects = new ArrayList<Project>();
		try {
			for (Repository repository : GitHubService.repositoryService.getRepositories(login)) {
				tasks.add(GitHubService.getProjectCallable(login, repository.getName(), repository.getHtmlUrl()));
			}

			candidateProjects = GitHubService.executorService.invokeAll(tasks);
			for (Future<Project> project : candidateProjects) {
				if (project.get() != null) {
					projects.add(project.get());
				}
			}
		} catch (Exception exception) {
			GitHubService.logger.log(Level.INFO, "No usage data found for user: " + login);
		}

		return new PastUsageData(GitHubService.GITHUB_URL, projects, UtilService.assignArtifacts(projects));
	}

	/**
	 * 
	 * @param login
	 * @param projectName
	 * @param url
	 * @param projectFiles
	 */
	private static void getDirectoryContents(final String login, final String projectName, final String url,
	        final Set<String> projectFiles, final Set<String> projectImports) {
		String href;
		Document document;
		Elements contents;

		try {
			document = Jsoup.connect(url).get();
			contents = document.select("td.content");

			// Analyze tree contents
			for (Element content : contents) {
				for (Node childNode : content.childNodes()) {
					// Get tree content reference
					if ("a".equals(childNode.nodeName())) {
						href = childNode.attr("href");

						// At this version we only support Java
						if ((href.startsWith("/" + login + "/" + projectName + "/blob/") && (UtilService
						        .isSupportedFile(href)))) {
							// Keep only the file name without path
							projectFiles.add(href.substring(href.lastIndexOf('/') + 1));
							projectImports.addAll(UtilService.getClassImports(GitHubService.getRawFile(login,
							        projectName, href)));
						}

						// Add child elements
						else if (href.startsWith("/" + login + "/" + projectName + "/tree/")) {
							GitHubService.getDirectoryContents(login, projectName, GitHubService.GITHUB_URL + href,
							        projectFiles, projectImports);
						}

						break;
					}
				}
			}
		} catch (IOException exception) {
			GitHubService.logger.log(Level.INFO, "No content found for url: " + url);
		}
	}

	/**
	 * 
	 * @param login
	 * @param projectName
	 * @param repositoryUrl
	 * @return
	 */
	private static Callable<Project> getProjectCallable(final String login, final String projectName,
	        final String repositoryUrl) {
		return new Callable<Project>() {
			@Override
			public Project call() throws Exception {
				Project returnValue;
				Set<String> projectFiles;
				Set<String> projectImports;

				try {
					projectImports = new HashSet<String>();
					projectFiles = new HashSet<String>();
					GitHubService.getDirectoryContents(login, projectName, repositoryUrl, projectFiles, projectImports);

					returnValue = new Project(projectName, projectFiles, projectImports);
				} catch (Exception e) {
					returnValue = null;
					GitHubService.logger.log(Level.INFO, "Error while getting usage data for repository: "
					        + projectName);
				}

				return returnValue;
			}
		};
	}

	/**
	 * 
	 * @param login
	 * @param repositoryName
	 * @param projectFile
	 * @return
	 */
	private static String getRawFile(final String login, final String projectName, final String projectFile) {
		return GitHubService.GITHUB_RAW_URL
		        + projectFile.replace((login + "/" + projectName + "/blob"), (login + "/" + projectName));
	}

	/**
	 * 
	 * @param blobFile
	 * @return
	 */
	private static String getRawBlobFile(final String blobFile) {
		return blobFile.replace(GitHubService.GITHUB_URL, GitHubService.GITHUB_RAW_URL).replace("/blob", "");
	}

	/**
	 * 
	 * @param login
	 * @return
	 * @throws IOException
	 */
	public static CurrentUsageData getCurrentUsageData(final String login) {
		Issue currentIssue;
		List<Issue> issues;
		Set<String> commitFiles;
		Set<String> commitImports;

		issues = new ArrayList<Issue>();
		commitFiles = new HashSet<String>();
		commitImports = new HashSet<String>();

		try {
			// Gather work data
			for (Repository repository : GitHubService.repositoryService.getRepositories(login)) {
				// Current work
				for (RepositoryCommit commit : GitHubService.commitService.getCommits(repository)) {
					if (login.equals(commit.getCommitter().getLogin())) {
						for (CommitFile commitFile : GitHubService.commitService.getCommit(repository, commit.getSha())
						        .getFiles()) {
							if (UtilService.isSupportedFile(commitFile.getFilename())) {
								commitFiles.add(commitFile.getFilename().substring(
								        commitFile.getFilename().lastIndexOf('/') + 1));
								commitImports.addAll(UtilService.getClassImports(GitHubService
								        .getRawBlobFile(commitFile.getBlobUrl())));
							}
						}
					}
				}

				// Pending work
				for (Issue issue : GitHubService.issueService.getIssues(login, repository.getName(), null)) {
					if (login.equals(issue.getAssignee().getLogin())) {
						if (issue.getState().equals("open")) {
							currentIssue = GitHubService.issueService.getIssue(repository, issue.getNumber());
							issues.add(currentIssue);
						}
					}
				}
			}
		} catch (IOException exception) {
			GitHubService.logger.log(Level.INFO, "No usage data found for user: " + login);
		}

		return new CurrentUsageData(issues, commitFiles, commitImports, UtilService.assignArtifacts(commitImports));
	}
}