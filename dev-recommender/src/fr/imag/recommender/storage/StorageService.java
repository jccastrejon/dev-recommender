package fr.imag.recommender.storage;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.rest.graphdb.RestGraphDatabase;

import fr.imag.recommender.common.PastUsageData;
import fr.imag.recommender.common.Project;
import fr.imag.recommender.github.CurrentUsageData;

/**
 * 
 * @author jccastrejon
 * 
 */
public class StorageService {

	/**
	 * 
	 */
	private static final Logger logger = Logger.getLogger(StorageService.class.getName());

	/**
	 * 
	 */
	private static final GraphDatabaseService databaseService;

	/**
	 * 
	 */
	private static final Index<Node> userIndex;

	static {
		databaseService = new RestGraphDatabase("http://localhost:7474/db/data");
		userIndex = databaseService.index().forNodes("users");
	}

	/**
	 * 
	 * @author jccastrejon
	 * 
	 */
	private static enum UsageTypes implements RelationshipType {
		IS_DEVELOPING, HAS_DEVELOPED, IN_REPOSITORY, HAS_FILE, HAS_IMPORT, CONTAINS, RELATED_TO
	};

	/**
	 * 
	 * @param login
	 * @param pastUsageData
	 * @param currentUsageData
	 */
	public static void saveUsageData(final String login, final List<PastUsageData> pastUsageData,
	        final CurrentUsageData currentUsageData) {
		try {
			StorageService.savePastUsageData(login, pastUsageData);
			StorageService.saveCurrentUsageData(login, currentUsageData);
		} catch (Exception e) {
			StorageService.logger.log(Level.INFO, "An error ocurred while saving data for user: " + login);
		}
	}

	/**
	 * 
	 * @param login
	 * @param usageData
	 */
	public static void saveCurrentUsageData(final String login, final CurrentUsageData usageData) {
		Node dataNode;
		Node userNode;
		Node filesNode;
		Node commitNode;
		Node importNode;
		Node importsNode;
		Node artifactNode;
		Node artifactsNode;

		if (usageData != null) {
			userNode = StorageService.getUserNode(login);
			dataNode = databaseService.createNode();
			filesNode = databaseService.createNode();
			artifactsNode = databaseService.createNode();
			importsNode = databaseService.createNode();

			dataNode.setProperty("name", "Current work");
			dataNode.setProperty("numberFiles", usageData.getCommitFiles().size());
			dataNode.setProperty("numberArtifacts", usageData.getArtifacts().size());
			dataNode.setProperty("numberImports", usageData.getCommitImports().size());

			userNode.createRelationshipTo(dataNode, StorageService.UsageTypes.IS_DEVELOPING);
			dataNode.createRelationshipTo(filesNode, StorageService.UsageTypes.CONTAINS);
			dataNode.createRelationshipTo(artifactsNode, StorageService.UsageTypes.CONTAINS);
			dataNode.createRelationshipTo(importsNode, StorageService.UsageTypes.CONTAINS);

			filesNode.setProperty("name", "files");
			artifactsNode.setProperty("name", "artifacts");
			importsNode.setProperty("name", "imports");

			for (String commitFile : usageData.getCommitFiles()) {
				commitNode = databaseService.createNode();
				commitNode.setProperty("name", commitFile);
				filesNode.createRelationshipTo(commitNode, StorageService.UsageTypes.HAS_FILE);
			}

			for (String projectImport : usageData.getCommitImports()) {
				importNode = databaseService.createNode();
				importNode.setProperty("name", projectImport);
				importsNode.createRelationshipTo(importNode, StorageService.UsageTypes.HAS_IMPORT);
			}

			for (String artifact : usageData.getArtifacts()) {
				artifactNode = databaseService.createNode();
				artifactNode.setProperty("name", artifact);
				artifactsNode.createRelationshipTo(artifactNode, StorageService.UsageTypes.RELATED_TO);
			}
		}
	}

	/**
	 * 
	 * @param login
	 * @param usageData
	 */
	public static void savePastUsageData(final String login, final List<PastUsageData> usageData) {
		Node userNode;
		Node dataNode;
		Node fileNode;
		Node dataNodes;
		Node importNode;
		Node filesNodes;
		Node projectNode;
		Node importsNode;
		Node artifactNode;
		Node projectsNode;
		Node artifactsNode;

		if (usageData != null) {
			userNode = StorageService.getUserNode(login);
			dataNodes = databaseService.createNode();

			dataNodes.setProperty("name", "Background work");
			userNode.createRelationshipTo(dataNodes, StorageService.UsageTypes.HAS_DEVELOPED);
			for (PastUsageData pastUsageData : usageData) {
				dataNode = databaseService.createNode();
				projectsNode = databaseService.createNode();
				artifactsNode = databaseService.createNode();

				projectsNode.setProperty("name", "projects");
				artifactsNode.setProperty("name", "artifacts");
				dataNode.setProperty("source", pastUsageData.getSource());
				dataNode.setProperty("numberProjects", pastUsageData.getProjects().size());
				dataNode.setProperty("numberArtifacts", pastUsageData.getArtifacts().size());

				dataNodes.createRelationshipTo(dataNode, StorageService.UsageTypes.IN_REPOSITORY);
				dataNode.createRelationshipTo(projectsNode, StorageService.UsageTypes.CONTAINS);
				dataNode.createRelationshipTo(artifactsNode, StorageService.UsageTypes.CONTAINS);

				for (Project project : pastUsageData.getProjects()) {
					projectNode = databaseService.createNode();
					importsNode = databaseService.createNode();
					filesNodes = databaseService.createNode();

					projectNode.setProperty("name", project.getName());
					importsNode.setProperty("name", "imports");
					filesNodes.setProperty("name", "files");

					importsNode.setProperty("numImports", project.getImports().size());
					filesNodes.setProperty("numFiles", project.getFiles().size());

					projectsNode.createRelationshipTo(projectNode, StorageService.UsageTypes.CONTAINS);
					projectNode.createRelationshipTo(importsNode, StorageService.UsageTypes.CONTAINS);
					projectNode.createRelationshipTo(filesNodes, StorageService.UsageTypes.CONTAINS);

					for (String projectFile : project.getFiles()) {
						fileNode = databaseService.createNode();
						fileNode.setProperty("name", projectFile);
						filesNodes.createRelationshipTo(fileNode, StorageService.UsageTypes.HAS_FILE);
					}

					for (String projectImport : project.getImports()) {
						importNode = databaseService.createNode();
						importNode.setProperty("name", projectImport);
						importsNode.createRelationshipTo(importNode, StorageService.UsageTypes.HAS_IMPORT);
					}
				}

				for (String artifact : pastUsageData.getArtifacts()) {
					artifactNode = databaseService.createNode();
					artifactNode.setProperty("name", artifact);
					artifactsNode.createRelationshipTo(artifactNode, StorageService.UsageTypes.RELATED_TO);
				}
			}
		}
	}

	/**
	 * 
	 * @param login
	 * @return
	 */
	private static Node getUserNode(final String login) {
		Node returnValue;
		IndexHits<Node> hits;

		hits = StorageService.userIndex.get("login", login);
		returnValue = hits.getSingle();

		if (returnValue == null) {
			returnValue = StorageService.databaseService.createNode();
			returnValue.setProperty("login", login);
			userIndex.add(returnValue, "login", login);
		}

		return returnValue;
	}
}
