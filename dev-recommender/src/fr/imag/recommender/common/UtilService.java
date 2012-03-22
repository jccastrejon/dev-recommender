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

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.rest.graphdb.RestGraphDatabase;

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
	 */
	private static final GraphDatabaseService databaseService = new RestGraphDatabase("http://localhost:7474/db/data");

	/**
	 * 
	 * @author jccastrejon
	 * 
	 */
	private static enum UsageTypes implements RelationshipType {
		DEVELOPED, IN_REPOSITORY, HAS_FILE, HAS_IMPORT, CONTAINS, RELATED_TO
	};

	/**
	 * 
	 * @param login
	 * @param usageData
	 */
	public static void savePastUsageData(final String login, final List<PastUsageData> usageData) {
		Node userNode;
		Node dataNode;
		Node fileNode;
		Node filesNodes;
		Node dataNodes;
		Node importNode;
		Node importsNodes;
		Node projectNode;
		Node artifactNode;
		Node projectsNode;
		Node artifactsNode;

		if (usageData != null) {
			userNode = databaseService.createNode();
			dataNodes = databaseService.createNode();

			dataNodes.setProperty("name", "Background projects");
			userNode.createRelationshipTo(dataNodes, UtilService.UsageTypes.DEVELOPED);
			for (PastUsageData pastUsageData : usageData) {
				dataNode = databaseService.createNode();
				projectsNode = databaseService.createNode();
				artifactsNode = databaseService.createNode();

				userNode.setProperty("login", login);
				projectsNode.setProperty("name", "projects");
				artifactsNode.setProperty("name", "artifacts");
				dataNode.setProperty("source", pastUsageData.getSource());
				dataNode.setProperty("numberProjects", pastUsageData.getProjects().size());
				dataNode.setProperty("numberArtifacts", pastUsageData.getArtifacts().size());

				dataNodes.createRelationshipTo(dataNode, UtilService.UsageTypes.IN_REPOSITORY);
				dataNode.createRelationshipTo(projectsNode, UtilService.UsageTypes.CONTAINS);
				dataNode.createRelationshipTo(artifactsNode, UtilService.UsageTypes.CONTAINS);

				for (Project project : pastUsageData.getProjects()) {
					projectNode = databaseService.createNode();
					importsNodes = databaseService.createNode();
					filesNodes = databaseService.createNode();

					projectNode.setProperty("name", project.getName());
					importsNodes.setProperty("name", "imports");
					filesNodes.setProperty("name", "files");

					importsNodes.setProperty("numImports", project.getImports().size());
					filesNodes.setProperty("numFiles", project.getFiles().size());

					projectsNode.createRelationshipTo(projectNode, UtilService.UsageTypes.CONTAINS);
					projectNode.createRelationshipTo(importsNodes, UtilService.UsageTypes.CONTAINS);
					projectNode.createRelationshipTo(filesNodes, UtilService.UsageTypes.CONTAINS);

					for (String projectFile : project.getFiles()) {
						fileNode = databaseService.createNode();
						fileNode.setProperty("name", projectFile);
						filesNodes.createRelationshipTo(fileNode, UtilService.UsageTypes.HAS_FILE);
					}

					for (String projectImport : project.getImports()) {
						importNode = databaseService.createNode();
						importNode.setProperty("name", projectImport);
						importsNodes.createRelationshipTo(importNode, UtilService.UsageTypes.HAS_IMPORT);
					}
				}

				for (String artifact : pastUsageData.getArtifacts()) {
					artifactNode = databaseService.createNode();
					artifactNode.setProperty("name", artifact);
					artifactsNode.createRelationshipTo(artifactNode, UtilService.UsageTypes.RELATED_TO);
				}
			}
		}
	}

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
