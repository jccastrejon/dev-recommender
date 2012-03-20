package fr.imag.recommender.googlecode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
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

import fr.imag.recommender.common.PastUsageData;
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
	 * @param login
	 * @return
	 * @throws IOException
	 */
	public static PastUsageData getPastUsageData(final String login) throws IOException {
		int startIndex;
		String inputLine;
		String repository;
		BufferedReader reader;
		String projectName;
		List<Project> projects;
		List<Future<Project>> candidateProjects;
		Collection<Callable<Project>> tasks;

		reader = null;
		tasks = new ArrayList<Callable<Project>>();
		projects = new ArrayList<Project>();
		try {

			reader = new BufferedReader(
			        new InputStreamReader(new URL("http://code.google.com/u/" + login).openStream()));

			// List of public projects
			while ((inputLine = reader.readLine()) != null) {
				startIndex = inputLine.indexOf("_go('/p/");
				if (startIndex > 0) {
					startIndex = startIndex + "_go('/p/".length();
					projectName = (inputLine.substring(startIndex, inputLine.indexOf('\'', startIndex + 1)));
					repository = GoogleCodeService.getRepository(projectName);

					if (repository != null) {
						// Get project usage data
						tasks.add(GoogleCodeService.getProjectCallable(repository, projectName));
					}
				}
			}

			candidateProjects = GoogleCodeService.executorService.invokeAll(tasks);
			for (Future<Project> project : candidateProjects) {
				projects.add(project.get());
			}
		} catch (Exception e) {
			GoogleCodeService.logger.log(Level.INFO, "An error ocurred while getting usage data found for user: "
			        + login + ", error: " + e.getMessage());
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return new PastUsageData(projects);
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
				} catch (IOException e) {
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
	 * @throws IOException
	 */
	private static void getDirectoryContents(final String url, final List<String> projectFiles,
	        final Set<String> projectImports) throws IOException {
		int startIndex;
		String inputLine;
		String content;
		BufferedReader reader;

		reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
			while ((inputLine = reader.readLine()) != null) {
				startIndex = inputLine.indexOf("<li><a href=");
				if (startIndex > 0) {
					startIndex = startIndex + "<li><a href=".length() + 1;
					content = inputLine.substring(startIndex, inputLine.indexOf("\"", startIndex));

					// No hidden files or directories
					if (!content.startsWith(".")) {
						// At this version we only support Java
						if (content.contains(".java")) {
							// Keep only the file name without path
							projectFiles.add(content.substring(content.lastIndexOf('/') + 1));
							projectImports.addAll(UtilService.getClassImports(url + content));
						}

						// Add child elements
						if (content.contains("/")) {
							GoogleCodeService.getDirectoryContents(url + content, projectFiles, projectImports);
						}
					}
				}
			}
		} catch (IOException e) {
			GoogleCodeService.logger.log(Level.INFO, "No content found for url: " + url);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * 
	 * @param project
	 * @return
	 * @throws IOException
	 */
	private static String getRepository(final String project) throws IOException {
		int startIndex;
		String inputLine;
		String returnValue;
		String repositoryType;
		BufferedReader reader;

		reader = null;
		returnValue = null;
		try {
			repositoryType = "svn";
			reader = new BufferedReader(new InputStreamReader(new URL("http://code.google.com/p/" + project
			        + "/source/checkout").openStream()));
			while ((inputLine = reader.readLine()) != null) {
				startIndex = inputLine.indexOf("checkoutcmd");
				if (startIndex > 0) {
					repositoryType = inputLine.substring(startIndex + "checkoutcmd".length() + 2,
					        inputLine.indexOf(' ', startIndex + "checkoutcmd".length()));
				}
			}

			reader.close();
			reader = new BufferedReader(new InputStreamReader(new URL("http://" + project + ".googlecode.com/"
			        + repositoryType + "/").openStream()));
			returnValue = "http://" + project + ".googlecode.com/" + repositoryType + "/";
		} catch (IOException e) {
			GoogleCodeService.logger.log(Level.INFO, "No repository found for user: " + project);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return returnValue;
	}
}
