package domain;

import java.io.Serializable;


public class MemeEndroit implements Serializable {
	
	
	private static final long serialVersionUID = -6937018394297085373L;
	private String locS; 
	private String locT;
	private int ecart;
	private String numeroS;
	private String numeroT;
	
	public MemeEndroit(String s, String t, int c, String e, String p) {
		this.locS = s;
		this.locT = t;
		this.ecart = c;
		this.numeroS = e;
		this.numeroT = p;
	}

	public MemeEndroit() {
		locS = "";
		locT = "";
		ecart = 0;
		numeroS = "";
		numeroT = "";
	}

	public String getLocS() {
		return locS;
	}
	public int getEcart() {
		return ecart;
	}
	
	public String getLocT() {
		return locT;
	}
	
	public String getNumeroS() {
		return numeroS;
	}
	public String getNumeroT() {
		return numeroT;
	}
	
	
	public boolean equals(MemeEndroit e) {
		if(e.getLocS() == this.locS && e.getLocT() == this.locT && e.getNumeroS() == numeroS && e.getNumeroT() == numeroT && e.getEcart() == ecart) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		return b.append(locS).append(" ").append(locT).append(" ").append(numeroS).append(" ").append(numeroT).append(" ").append(ecart).toString();	
	}
	
	public String str() {
		return locS + " " + locT + " " + " " + ecart + " " + numeroS + " " + numeroT;
	}
	
	
	
}
