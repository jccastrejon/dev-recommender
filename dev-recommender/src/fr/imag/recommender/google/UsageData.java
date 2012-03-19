package fr.imag.recommender.google;

import java.util.List;

/**
 * 
 * @author jccastrejon
 * 
 */
public class UsageData {

	/**
	 * 
	 */
	private List<Project> projects;

	public UsageData(List<Project> projects) {
		this.projects = projects;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}
}
