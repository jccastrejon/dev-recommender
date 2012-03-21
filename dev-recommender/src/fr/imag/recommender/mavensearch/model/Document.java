package fr.imag.recommender.mavensearch.model;

import java.util.Collection;

/**
 * 
 * @author jccastrejon
 * 
 */
public class Document {
	private String id;
	private String g;
	private String a;
	private String latestVersion;
	private String repositoryId;
	private String p;
	private String timestamp;
	private String versionCount;
	private Collection<String> text;
	private Collection<String> ec;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getG() {
		return g;
	}

	public void setG(String g) {
		this.g = g;
	}

	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}

	public String getLatestVersion() {
		return latestVersion;
	}

	public void setLatestVersion(String latestVersion) {
		this.latestVersion = latestVersion;
	}

	public String getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
	}

	public String getP() {
		return p;
	}

	public void setP(String p) {
		this.p = p;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getVersionCount() {
		return versionCount;
	}

	public void setVersionCount(String versionCount) {
		this.versionCount = versionCount;
	}

	public Collection<String> getText() {
		return text;
	}

	public void setText(Collection<String> text) {
		this.text = text;
	}

	public Collection<String> getEc() {
		return ec;
	}

	public void setEc(Collection<String> ec) {
		this.ec = ec;
	}

}
