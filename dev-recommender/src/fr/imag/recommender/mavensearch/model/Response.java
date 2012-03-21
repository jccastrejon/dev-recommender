package fr.imag.recommender.mavensearch.model;

import java.util.Collection;

/**
 * 
 * @author jccastrejon
 * 
 */
public class Response {
	private int numFound;
	private int start;
	private Collection<Document> docs;

	public int getNumFound() {
		return numFound;
	}

	public void setNumFound(int numFound) {
		this.numFound = numFound;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public Collection<Document> getDocs() {
		return docs;
	}

	public void setDocs(Collection<Document> docs) {
		this.docs = docs;
	}

}
