package fr.imag.recommender.google;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author jccastrejon
 * 
 */
public class GoogleService {

	/**
	 * 
	 */
	private static final ExecutorService executorService = Executors.newFixedThreadPool(4);

	/**
	 * 
	 */
	private static final Logger logger = Logger.getLogger(GoogleService.class.getName());

	/**
	 * 
	 * @param login
	 * @return
	 * @throws IOException
	 */
	public static UsageData getUsageData(final String login) throws IOException {
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
					repository = GoogleService.getRepository(projectName);

					if (repository != null) {
						// Get project usage data
						tasks.add(GoogleService.getProjectCallable(repository, projectName));
					}
				}
			}

			candidateProjects = GoogleService.executorService.invokeAll(tasks);
			for (Future<Project> project : candidateProjects) {
				projects.add(project.get());
			}
		} catch (Exception e) {
			GoogleService.logger.log(Level.INFO, "An error ocurred while getting usage data found for user: " + login
			        + ", error: " + e.getMessage());
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return new UsageData(projects);
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
				List<String> filesUrls;
				List<String> filesNames;
				List<String> projectImports;

				try {
					filesUrls = GoogleService.getProjectFiles(repository);
					projectImports = GoogleService.getProjectImports(projectName, filesUrls);

					// Keep only file names without package information
					filesNames = new ArrayList<String>(filesUrls.size());
					for (String fileUrl : filesUrls) {
						filesNames.add(fileUrl.substring(fileUrl.lastIndexOf('/') + 1));
					}

					returnValue = new Project(projectName, filesNames, projectImports);
				} catch (IOException e) {
					returnValue = null;
					GoogleService.logger
					        .log(Level.INFO, "Error while getting usage data for repository: " + repository);
				}

				return returnValue;
			}
		};
	}

	/**
	 * 
	 * @param repository
	 * @return
	 * @throws IOException
	 */
	private static List<String> getProjectFiles(final String repository) throws IOException {
		List<String> returnValue;

		returnValue = new ArrayList<String>();
		GoogleService.getDirectoryContents(repository, returnValue);

		return returnValue;
	}

	/**
	 * 
	 * @param repository
	 * @return
	 * @throws IOException
	 */
	private static List<String> getProjectImports(final String repository, final List<String> files) throws IOException {
		List<String> returnValue;

		returnValue = new ArrayList<String>();
		for (String file : files) {
			returnValue.addAll(GoogleService.getClassImports(file));
		}

		return returnValue;
	}

	/**
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private static void getDirectoryContents(final String url, final List<String> returnValue) throws IOException {
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
							returnValue.add(url + content);
						}

						// Add child elements
						if (content.contains("/")) {
							GoogleService.getDirectoryContents(url + content, returnValue);
						}
					}
				}
			}
		} catch (IOException e) {
			GoogleService.logger.log(Level.INFO, "No content found for url: " + url);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private static List<String> getClassImports(final String url) throws IOException {
		int startIndex;
		String inputLine;
		BufferedReader reader;
		List<String> returnValue;

		returnValue = new ArrayList<String>();
		reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
			while ((inputLine = reader.readLine()) != null) {
				// Class content has started, import section is over
				if (inputLine.contains("{")) {
					break;
				}

				startIndex = inputLine.indexOf("import");
				if (startIndex >= 0) {
					startIndex = startIndex + "import".length() + 1;
					returnValue.add(inputLine.substring(startIndex, inputLine.indexOf(";", startIndex)));
				}
			}
		} catch (IOException e) {
			GoogleService.logger.log(Level.INFO, "No imports found for class: " + url);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return returnValue;
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
			GoogleService.logger.log(Level.INFO, "No repository found for user: " + project);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return returnValue;
	}
}
