package fr.imag.recommender.googlecode;

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
public class GoogleCodeService {

	/**
	 * 
	 */
	private static final ExecutorService executorService = Executors.newFixedThreadPool(4);

	/**
	 * 
	 */
	private static final Logger logger = Logger.getLogger(GoogleCodeService.class.getName());

	/**
	 * 
	 */
	private static final String GOOGLE_CODE_URL = "http://code.google.com";

	/**
	 * 
	 * @param login
	 * @return
	 */
	public static PastUsageData getPastUsageData(final String login) {
		String href;
		Document document;
		Elements contents;
		String projectName;
		List<Project> projects;
		Collection<Callable<Project>> tasks;
		List<Future<Project>> candidateProjects;

		tasks = new ArrayList<Callable<Project>>();
		projects = new ArrayList<Project>();
		try {
			document = Jsoup.connect(GoogleCodeService.GOOGLE_CODE_URL + "/u/" + login).get();
			contents = document.select("td.id");

			// Analyze tree contents
			for (Element content : contents) {
				for (Node childNode : content.childNodes()) {
					// Get tree content reference
					if ("a".equals(childNode.nodeName())) {
						href = childNode.attr("href");
						projectName = href.replace("/p/", "");
						projectName = projectName.substring(0, projectName.lastIndexOf('/'));
						tasks.add(GoogleCodeService.getProjectCallable(GoogleCodeService.getRepository(projectName),
						        projectName));
						break;
					}
				}
			}

			candidateProjects = GoogleCodeService.executorService.invokeAll(tasks);
			for (Future<Project> project : candidateProjects) {
				projects.add(project.get());
			}
		} catch (Exception exception) {
			GoogleCodeService.logger.log(Level.INFO, "No usage data found for user: " + login);
		}

		UtilService.assignArtifacts(projects);
		return new PastUsageData(projects, UtilService.assignArtifacts(projects));
	}

	/**
	 * 
	 * @param repository
	 * @param projectName
	 * @param projects
	 * @return
	 */
	private static Callable<Project> getProjectCallable(final String repository, final String projectName) {
		return new Callable<Project>() {
			@Override
			public Project call() throws Exception {
				Project returnValue;
				List<String> projectFiles;
				Set<String> projectImports;

				try {
					projectFiles = new ArrayList<String>();
					projectImports = new HashSet<String>();
					GoogleCodeService.getDirectoryContents(repository, projectFiles, projectImports);
					returnValue = new Project(projectName, projectFiles, projectImports);
				} catch (Exception e) {
					returnValue = null;
					GoogleCodeService.logger.log(Level.INFO, "Error while getting usage data for repository: "
					        + repository);
				}

				return returnValue;
			}
		};
	}

	/**
	 * 
	 * @param url
	 * @param projectFiles
	 * @param projectImports
	 */
	private static void getDirectoryContents(final String url, final List<String> projectFiles,
	        final Set<String> projectImports) {
		String href;
		Document document;
		Elements contents;

		try {
			document = Jsoup.connect(url).get();
			contents = document.select("li");

			// Analyze tree contents
			for (Element content : contents) {
				for (Node childNode : content.childNodes()) {
					// Get tree content reference
					if ("a".equals(childNode.nodeName())) {
						href = childNode.attr("href");

						// Ignore hidden files
						if (!href.startsWith(".")) {
							// At this version we only support Java
							if (href.endsWith(".java")) {
								// Keep only the file name without path
								projectFiles.add(href.substring(href.lastIndexOf('/') + 1, href.indexOf(".java")));
								projectImports.addAll(UtilService.getClassImports(url + "/" + href));
							}

							// Add child elements
							else if (href.endsWith("/")) {
								GoogleCodeService.getDirectoryContents(url + href, projectFiles, projectImports);
							}

							break;
						}
					}
				}
			}
		} catch (IOException exception) {
			GoogleCodeService.logger.log(Level.INFO, "No content found for url: " + url);
		}
	}

	/**
	 * 
	 * @param project
	 * @return
	 * @throws IOException
	 */
	private static String getRepository(final String project) {
		Document document;
		Element element;
		String returnValue;
		String repositoryType;

		returnValue = null;
		try {
			// Default repository type is svn
			repositoryType = "svn";
			document = Jsoup.connect(GoogleCodeService.GOOGLE_CODE_URL + "/p/" + project + "/source/checkout").get();
			element = document.select("#checkoutcmd").first();

			if (element != null) {
				repositoryType = element.text().substring(0, element.text().indexOf(' '));
			}

			// Verify if the repository is available or not
			returnValue = "http://" + project + ".googlecode.com/" + repositoryType + "/";
			Jsoup.connect(returnValue);
		} catch (IOException e) {
			returnValue = null;
			GoogleCodeService.logger.log(Level.INFO, "No repository found for repository: " + project);
		}

		return returnValue;
	}
}