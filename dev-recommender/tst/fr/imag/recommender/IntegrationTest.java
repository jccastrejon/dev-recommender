package fr.imag.recommender;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fr.imag.recommender.common.PastUsageData;
import fr.imag.recommender.common.UtilService;
import fr.imag.recommender.github.CurrentUsageData;
import fr.imag.recommender.github.GitHubService;
import fr.imag.recommender.googlecode.GoogleCodeService;
import fr.imag.recommender.local.LocalService;

/**
 * 
 * @author jccastrejon
 * 
 */
public class IntegrationTest {

	@Test
	public void testPastUsage() {
		CurrentUsageData currentUsageData;
		List<PastUsageData> pastUsageData;

		pastUsageData = new ArrayList<PastUsageData>();
		pastUsageData.add(GitHubService.getPastUsageData("jccastrejon"));
		pastUsageData.add(GoogleCodeService.getPastUsageData("jccastrejon"));
		pastUsageData.add(LocalService.getPastUsageData("jccastrejon", "/Users/jccastrejon/java/workspace/PetClinic"));

		currentUsageData = GitHubService.getCurrentUsageData("jccastrejon");
		UtilService.saveUsageData("jccastrejon", pastUsageData, currentUsageData);
	}
}
