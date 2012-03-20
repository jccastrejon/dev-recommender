package fr.imag.recommender.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author jccastrejon
 * 
 */
public class UtilService {

	/**
	 * 
	 */
	private static final Logger logger = Logger.getLogger(UtilService.class.getName());

	/**
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static Set<String> getClassImports(final String url) throws IOException {
		int startIndex;
		String inputLine;
		BufferedReader reader;
		Set<String> returnValue;

		returnValue = new HashSet<String>();
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
			UtilService.logger.log(Level.INFO, "No imports found for class: " + url);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return returnValue;
	}
}
