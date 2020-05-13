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
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj==this)
			return true;
		if(obj==null)
			return false;
		if(!(obj instanceof MemeEndroit))
			return false;
		MemeEndroit o = (MemeEndroit) obj;
		if(!locS.contentEquals(o.locS))
			return false;
		if(!locT.contentEquals(o.locT))
			return false;
		if(o.ecart!=this.ecart)
			return false;
		if(!numeroS.contentEquals(o.numeroS))
			return false;
		if(!numeroT.contentEquals(o.numeroT))
			return false;
		return true;
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
