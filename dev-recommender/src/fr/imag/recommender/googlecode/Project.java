package fr.imag.recommender.googlecode;

import java.util.List;
import java.util.Set;

/**
 * 
 * @author jccastrejon
 * 
 */
public class Project {
	private String name;
	private List<String> files;
	private Set<String> imports;
	private Set<String> artifacts;

	public Project(String name, List<String> files, Set<String> imports) {
		super();
		this.name = name;
		this.files = files;
		this.imports = imports;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

	public Set<String> getImports() {
		return imports;
	}

	public void setImports(Set<String> imports) {
		this.imports = imports;
	}

	public Set<String> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(Set<String> artifacts) {
		this.artifacts = artifacts;
	}
}
