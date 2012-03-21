package fr.imag.recommender.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.imag.recommender.googlecode.Project;
import fr.imag.recommender.mavensearch.MavenSearchService;

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

	/**
	 * 
	 * @param projects
	 */
	public static Set<String> assignArtifacts(final List<Project> projects) {
		String artifact;
		Set<String> packages;
		Set<String> returnValue;
		Set<String> allImports;

		returnValue = new HashSet<String>();
		allImports = new HashSet<String>();
		for (Project project : projects) {
			allImports.addAll(project.getImports());
		}

		packages = getImportsPackages(allImports);
		for (String projectPackage : packages) {
			artifact = MavenSearchService.getAssociatedProject(projectPackage);
			if (artifact != null) {
				returnValue.add(artifact);
			}
		}

		return returnValue;
	}

	/**
	 * 
	 * @param projectImports
	 * @return
	 */
	private static Set<String> getImportsPackages(final Set<String> projectImports) {
		int index;
		Set<String> returnValue;

		returnValue = new HashSet<String>();
		for (String projectImport : projectImports) {
			// Ignore standard apis and static imports, and consider classes
			// with at least three upper packages
			if (!projectImport.startsWith("java.") && !projectImport.startsWith("static ")) {
				index = projectImport.indexOf('.');
				if (index > 0) {
					index = projectImport.indexOf('.', index + 1);
					if (index > 0) {
						index = projectImport.indexOf('.', index + 1);
						if (index > 0) {
							returnValue.add(projectImport.substring(0, index));
						}
					}
				}
			}
		}

		return returnValue;
	}
}
