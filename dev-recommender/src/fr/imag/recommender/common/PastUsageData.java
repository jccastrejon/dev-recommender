package fr.imag.recommender.common;

import java.util.List;

import fr.imag.recommender.googlecode.Project;

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

	public PastUsageData(List<Project> projects) {
		this.projects = projects;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}
}
