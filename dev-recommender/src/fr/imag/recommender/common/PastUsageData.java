package fr.imag.recommender.common;

import java.util.List;
import java.util.Set;

/**
 * 
 * @author jccastrejon
 * 
 */
public class PastUsageData {

	/**
	 * 
	 */
	private String source;

	/**
	 * 
	 */
	private List<Project> projects;

	/**
	 * 
	 */
	private Set<String> artifacts;

	public PastUsageData(String source, List<Project> projects, Set<String> artifacts) {
		super();
		this.source = source;
		this.projects = projects;
		this.artifacts = artifacts;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public Set<String> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(Set<String> artifacts) {
		this.artifacts = artifacts;
	}
}
