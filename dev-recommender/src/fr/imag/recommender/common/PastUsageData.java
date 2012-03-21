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
	private List<Project> projects;

	/**
	 * 
	 */
	private Set<String> artifacts;

	public PastUsageData(List<Project> projects, Set<String> artifacts) {
		super();
		this.projects = projects;
		this.artifacts = artifacts;
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
