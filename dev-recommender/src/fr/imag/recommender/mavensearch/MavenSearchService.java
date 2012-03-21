package fr.imag.recommender.mavensearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import fr.imag.recommender.mavensearch.model.Document;
import fr.imag.recommender.mavensearch.model.SearchResponse;

/**
 * 
 * @author jccastrejon
 * 
 */
public class MavenSearchService {

	private static Logger logger = Logger.getLogger(MavenSearchService.class.getName());

	/**
	 * 
	 * @return
	 */
	public static String getAssociatedProject(final String className) {
		String returnValue;
		SearchResponse searchResponse;

		returnValue = null;
		try {
			searchResponse = new Gson().fromJson(
			        new BufferedReader(new InputStreamReader(new URL(
			                "http://search.maven.org/solrsearch/select?q=fc:\"" + className + "\"&rows=20&wt=json")
			                .openStream())), SearchResponse.class);

			if (!searchResponse.getResponse().getDocs().isEmpty()) {
				// In this version, we'll only keep the first artifactId
				for (Document document : searchResponse.getResponse().getDocs()) {
					returnValue = document.getA();
					break;
				}
			}
		} catch (IOException e) {
			MavenSearchService.logger.log(Level.INFO, "No associated project for class: " + className);
		}

		return returnValue;
	}
}
