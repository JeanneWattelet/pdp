package domain;

public class ArcTrajet extends org.jgrapht.graph.DefaultWeightedEdge{

	private static final long serialVersionUID = -3259071493169286685L ;
	private int transport;
	
	public ArcTrajet(int transport){
		super();
		this.transport = transport;
	}
	
	public int getTransport() {
		return transport;
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
		String t = "pied";
		switch(transport) {
		case 0:
			t="attente";
			break;
		case 1:
			t="bus";
			break;
		case 2:
			t="tram";
			break;
		case 3 :
			t="metro";
			break;
		case 4 :
			t="bateau";
			break;
		}
		return super.toString()+"& véhicule : "+t;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode()+transport;
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