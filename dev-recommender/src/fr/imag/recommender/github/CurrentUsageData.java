package fr.imag.recommender.github;

import java.util.List;
import java.util.Set;

import org.eclipse.egit.github.core.Issue;

/**
 * 
 * @author jccastrejon
 * 
 */
public class CurrentUsageData {

	/**
	 * 
	 */
	private List<Issue> issues;

	/**
	 * 
	 */
	private Set<String> commitFiles;

	/**
	 * 
	 */
	private Set<String> commitImports;

	/**
	 * 
	 */
	private Set<String> artifacts;

	public CurrentUsageData(List<Issue> issues, Set<String> commitFiles, Set<String> commitImports,
	        Set<String> artifacts) {
		super();
		this.issues = issues;
		this.commitFiles = commitFiles;
		this.commitImports = commitImports;
		this.artifacts = artifacts;
	}

	public List<Issue> getIssues() {
		return issues;
	}

	public void setIssues(List<Issue> issues) {
		this.issues = issues;
	}

	public Set<String> getCommitFiles() {
		return commitFiles;
	}

	public void setCommitFiles(Set<String> commitFiles) {
		this.commitFiles = commitFiles;
	}

	public Set<String> getCommitImports() {
		return commitImports;
	}

	public void setCommitImports(Set<String> commitImports) {
		this.commitImports = commitImports;
	}

	public Set<String> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(Set<String> artifacts) {
		this.artifacts = artifacts;
	}
}
