package domain;

import transport.Ligne;

public class ArcTrajet extends org.jgrapht.graph.DefaultWeightedEdge{

	private static final long serialVersionUID = -3259071493169286685L ;
	private String transport;
	private String nom;
	
	public ArcTrajet(String transport, String from, String to, String nom){
		super();
		this.transport = transport;
		this.nom = nom;
	}
	
	public ArcTrajet(int transport, String from, String to, String nom){
		super();
		this.transport = Ligne.intToStringVehicule(transport);
		this.nom = nom;
	}
	
	public ArcTrajet(int tra) {
		super();
		transport = Ligne.intToStringVehicule(tra);
	}
	
	public ArcTrajet() {
		super();
		transport = Ligne.ATTENTE;
		nom = "";
	}
	
	public int getTransport() {
		return Ligne.stringToIntVehicule(transport);
	}
	
	public String getTransportString() {
		return transport;
	}
	
	public String getFrom() {
		return GrapheTrajet.denommer((String)super.getSource());
	}
	
	public String getTo() {
		return GrapheTrajet.denommer((String)super.getTarget());
	}

	public String getNom() {
		return nom;
	}
	
	public String getSourceT() {
		return (String) super.getSource();
	}
	
	public String getTargetT() {
		return (String)super.getTarget();
	}
	
	public double getWeightT() {
		return super.getWeight();
	}
	
	@Override
	public String toString() {
		return transport+" ("+nom+") : "+super.toString();
	}
	
	@Override
	public int hashCode() {
		return super.hashCode()+transport.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(super.equals(obj)) {
			if(obj.getClass()==this.getClass()) {
				ArcTrajet o = (ArcTrajet) obj;
				if(this.getTransport() == o.getTransport()) {
					return true;
				}
			}
		}
		return false;
	}
}
