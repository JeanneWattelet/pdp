package domain;

public class ArcTrajet extends org.jgrapht.graph.DefaultWeightedEdge{

	private static final long serialVersionUID = -3259071493169286685L ;
	private String transport;
	private String nom;
	private String from;
	private String to;
	
	public ArcTrajet(String transport, String from, String to, String nom){
		super();
		this.transport = transport;
		this.from = from;
		this.to = to;
		this.nom = nom;
	}
	
	public String getTransport() {
		return transport;
	}
	
	public String getFrom() {
		return from;
	}
	
	public String getTo() {
		return to;
	}

	public String getNom() {
		return nom;
	}
	
	public Object getSourceT() {
		return super.getSource();
	}
	
	public Object getTargetT() {
		return super.getTarget();
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
