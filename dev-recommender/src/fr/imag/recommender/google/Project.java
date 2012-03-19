package fr.imag.recommender.google;

import java.util.List;

/**
 * 
 * @author jccastrejon
 * 
 */
public class Project {
	private String name;
	private List<String> files;
	private List<String> imports;

	public Project(String name, List<String> files, List<String> imports) {
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

	public List<String> getImports() {
		return imports;
	}

	public void setImports(List<String> imports) {
		this.imports = imports;
	}

}
