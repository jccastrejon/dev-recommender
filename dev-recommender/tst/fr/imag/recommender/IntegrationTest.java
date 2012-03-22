package fr.imag.recommender;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fr.imag.recommender.common.PastUsageData;
import fr.imag.recommender.common.UtilService;
import fr.imag.recommender.github.GitHubService;
import fr.imag.recommender.googlecode.GoogleCodeService;

/**
 * 
 * @author jccastrejon
 * 
 */
public class IntegrationTest {

	@Test
	public void testPastUsage() {
		List<PastUsageData> usageData;

		usageData = new ArrayList<PastUsageData>();
		usageData.add(GitHubService.getPastUsageData("jccastrejon"));
		usageData.add(GoogleCodeService.getPastUsageData("jccastrejon"));

		UtilService.savePastUsageData("jccastrejon", usageData);
	}
}
