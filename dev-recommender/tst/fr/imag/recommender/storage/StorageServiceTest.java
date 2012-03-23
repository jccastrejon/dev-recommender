package fr.imag.recommender.storage;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fr.imag.recommender.common.PastUsageData;
import fr.imag.recommender.github.CurrentUsageData;
import fr.imag.recommender.github.GitHubService;
import fr.imag.recommender.googlecode.GoogleCodeService;
import fr.imag.recommender.local.LocalService;

/**
 * 
 * @author jccastrejon
 * 
 */
public class StorageServiceTest {

	@Test
	public void testSaveUsageData() {
		CurrentUsageData currentUsageData;
		List<PastUsageData> pastUsageData;

		pastUsageData = new ArrayList<PastUsageData>();
		pastUsageData.add(GitHubService.getPastUsageData("jccastrejon"));
		pastUsageData.add(GoogleCodeService.getPastUsageData("jccastrejon"));
		pastUsageData.add(LocalService.getPastUsageData("jccastrejon", "/Users/jccastrejon/java/workspace/PetClinic"));

		currentUsageData = GitHubService.getCurrentUsageData("jccastrejon");
		StorageService.saveUsageData("jccastrejon", pastUsageData, currentUsageData);
	}

	@Test
	public void testSaveCurrentUsageData() {
		CurrentUsageData currentUsageData;

		currentUsageData = GitHubService.getCurrentUsageData("jccastrejon");
		StorageService.saveCurrentUsageData("jccastrejon", currentUsageData);
	}
}
