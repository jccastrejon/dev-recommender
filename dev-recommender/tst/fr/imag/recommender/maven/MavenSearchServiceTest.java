package fr.imag.recommender.maven;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import fr.imag.recommender.mavensearch.MavenSearchService;

/**
 * 
 * @author jccastrejon
 * 
 */
public class MavenSearchServiceTest {

	@Test
	public void testGetAssociatedProject() {
		String project;

		// Existing project
		project = MavenSearchService.getAssociatedProject("org.eclipse.egit.github.core.service.IssueService");
		assertNotNull(project);

		// Non-existing project
		project = MavenSearchService.getAssociatedProject(MavenSearchService.class.getCanonicalName());
		assertNull(project);
	}
}
