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
		BufferedReader reader;
		List<String> projects;

		reader = null;
		projects = new ArrayList<String>();
		try {
			reader = new BufferedReader(
			        new InputStreamReader(new URL("http://code.google.com/u/" + login).openStream()));

			// List of public projects
			while ((inputLine = reader.readLine()) != null) {
				startIndex = inputLine.indexOf("_go('/p/");
				if (startIndex > 0) {
					projects.add(inputLine.substring(startIndex + 8, inputLine.indexOf('\'', startIndex + 9)));
				}
			}

			// Get latest state
			for (String project : projects) {
				reader = GoogleService.getRepositoryReader(project);

				if (reader != null) {
					System.out.println("project: " + project + ":" + reader);
					reader.close();
				}
			}
		} catch (IOException e) {
			GoogleService.logger.log(Level.INFO, "No usage data found for user: " + login);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return new UsageData();
	}

	/**
	 * 
	 * @param project
	 * @return
	 */
	private static BufferedReader getRepositoryReader(final String project) {
		int startIndex;
		String inputLine;
		String repositoryType;
		BufferedReader returnValue;

		try {
			repositoryType = "svn";
			returnValue = new BufferedReader(new InputStreamReader(new URL("http://code.google.com/p/" + project
			        + "/source/checkout").openStream()));
			while ((inputLine = returnValue.readLine()) != null) {
				startIndex = inputLine.indexOf("checkoutcmd");
				if (startIndex > 0) {
					repositoryType = inputLine.substring(startIndex + "checkoutcmd".length() + 2,
					        inputLine.indexOf(' ', startIndex + "checkoutcmd".length()));
				}
			}

			returnValue.close();
			returnValue = new BufferedReader(new InputStreamReader(new URL("http://" + project + ".googlecode.com/"
			        + repositoryType + "/").openStream()));
		} catch (IOException e) {
			returnValue = null;
		}

		return returnValue;
	}
}
