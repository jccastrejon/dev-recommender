package fr.imag.recommender.github;

import java.util.List;

import org.eclipse.egit.github.core.CommitFile;
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
	private List<CommitFile> commitFiles;

	/**
	 * 
	 * @param issues
	 * @param commitFiles
	 */
	public CurrentUsageData(final List<Issue> issues, final List<CommitFile> commitFiles) {
		this.issues = issues;
		this.commitFiles = commitFiles;
	}

	public List<Issue> getIssues() {
		return issues;
	}

	public void setIssues(List<Issue> issues) {
		this.issues = issues;
	}

	public List<CommitFile> getCommitFiles() {
		return commitFiles;
	}

	public void setCommitFiles(List<CommitFile> commitFiles) {
		this.commitFiles = commitFiles;
	}
}
