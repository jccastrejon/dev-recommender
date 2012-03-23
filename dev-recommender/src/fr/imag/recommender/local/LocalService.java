package fr.imag.recommender.local;

import java.io.File;
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

import fr.imag.recommender.common.PastUsageData;
import fr.imag.recommender.common.Project;
import fr.imag.recommender.common.UtilService;

/**
 * 
 * @author jccastrejon
 * 
 */
public class LocalService {
	/**
	 * 
	 */
	private static final ExecutorService executorService = Executors.newFixedThreadPool(4);

	/**
	 * 
	 */
	private static Logger logger = Logger.getLogger(LocalService.class.getName());

	/**
	 * 
	 * @param login
	 * @param rootPath
	 * @return
	 */
	public static PastUsageData getPastUsageData(final String login, final String... paths) {
		List<Project> projects;
		List<Future<Project>> candidateProjects;
		Collection<Callable<Project>> tasks;

		tasks = new ArrayList<Callable<Project>>();
		projects = new ArrayList<Project>();

		if (paths != null) {
			try {
				for (String path : paths) {
					tasks.add(LocalService.getProjectCallable(new File(path)));
				}

				candidateProjects = LocalService.executorService.invokeAll(tasks);
				for (Future<Project> project : candidateProjects) {
					if (project.get() != null) {
						projects.add(project.get());
					}
				}
			} catch (Exception exception) {
				LocalService.logger.log(Level.INFO, "Error while getting usage data for user: " + login);
			}
		}

		return new PastUsageData("local", projects, UtilService.assignArtifacts(projects));
	}

	/**
	 * 
	 * @param path
	 * @return
	 */
	private static Callable<Project> getProjectCallable(final File path) {
		return new Callable<Project>() {
			@Override
			public Project call() throws Exception {
				Project returnValue;
				Set<String> projectFiles;
				Set<String> projectImports;

				returnValue = null;
				if (path.exists()) {
					projectFiles = new HashSet<String>();
					projectImports = new HashSet<String>();
					LocalService.getDirectoryContents(path, projectFiles, projectImports);
					returnValue = new Project(path.getName(), projectFiles, projectImports);
				}

				return returnValue;
			}
		};
	}

	/**
	 * 
	 * @param path
	 * @param projectFiles
	 * @param projectImports
	 */
	private static void getDirectoryContents(final File path, final Set<String> projectFiles,
	        final Set<String> projectImports) {
		if (path.exists()) {
			for (File child : path.listFiles()) {
				// At this version we only support Java
				if (UtilService.isSupportedFile(child.getName())) {
					try {
						projectFiles.add(child.getName());
						projectImports.addAll(UtilService.getClassImports(child.toURI().toURL().toString()));
					} catch (IOException e) {
						LocalService.logger.log(Level.INFO,
						        "Error while getting data for file: " + child.getAbsolutePath());
					}
				}

				// Add child elements
				if (child.isDirectory()) {
					LocalService.getDirectoryContents(child, projectFiles, projectImports);
				}
			}
		}
	}
}
