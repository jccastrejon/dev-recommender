package fr.imag.recommender.google;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author jccastrejon
 * 
 */
public class GoogleService {

	private static Logger logger = Logger.getLogger(GoogleService.class.getName());

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
		List<String> filesUrls;
		List<String> filesNames;
		List<String> projectImports;

		reader = null;
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
						filesUrls = GoogleService.getProjectFiles(repository);
						projectImports = GoogleService.getProjectImports(projectName, filesUrls);

						// Keep only file names without package information
						filesNames = new ArrayList<String>(filesUrls.size());
						for (String fileUrl : filesUrls) {
							filesNames.add(fileUrl.substring(fileUrl.lastIndexOf('/') + 1));
						}

						projects.add(new Project(projectName, filesNames, projectImports));
					}
				}
			}
		} catch (IOException e) {
			GoogleService.logger.log(Level.INFO, "No usage data found for user: " + login);
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
	 * @return
	 * @throws IOException
	 */
	private static List<String> getProjectFiles(final String repository) throws IOException {
		List<String> returnValue;

		returnValue = new ArrayList<String>();
		returnValue = GoogleService.getDirectoryContents(repository, returnValue);

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
	private static List<String> getDirectoryContents(final String url, final List<String> returnValue)
	        throws IOException {
		int startIndex;
		String inputLine;
		BufferedReader reader;

		reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
			while ((inputLine = reader.readLine()) != null) {
				startIndex = inputLine.indexOf("<li><a href=");
				if (startIndex > 0) {
					startIndex = startIndex + "<li><a href=".length() + 1;

					// At this version we only support Java
					if (inputLine.contains(".java")) {
						returnValue.add(url + inputLine.substring(startIndex, inputLine.indexOf("\"", startIndex)));
					}

					// Add child elements
					if (inputLine.contains("/")) {
						GoogleService
						        .getDirectoryContents(
						                url + inputLine.substring(startIndex, inputLine.indexOf("\"", startIndex)),
						                returnValue);
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

		return returnValue;
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
