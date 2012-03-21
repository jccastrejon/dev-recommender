package fr.imag.recommender.mavensearch.model;

/**
 * 
 * @author jccastrejon
 * 
 */
public class ResponseHeader {
	private int status;
	private int qTime;
	private Params params;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getqTime() {
		return qTime;
	}

	public void setqTime(int qTime) {
		this.qTime = qTime;
	}

	public Params getParams() {
		return params;
	}

	public void setParams(Params params) {
		this.params = params;
	}

}
