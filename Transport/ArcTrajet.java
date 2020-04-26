package Transport;

public class ArcTrajet extends org.jgrapht.graph.DefaultWeightedEdge{

	private static final long serialVersionUID = -3259071493169286685L ;
	private int transport;
	private String from;
	private String to;
	
	public ArcTrajet(int transport, String from, String to){
		super();
		this.transport = transport;
		this.from = from;
		this.to = to;
	}
	
	public int getTransport() {
		return transport;
	}
	
	public String getFrom() {
		return from;
	}
	
	public String getTo() {
		return to;
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
			t="Tramway";
			break;
		case 1:
			t="metro";
			break;
		case 2:
			t="train";
			break;
		case 3 :
			t="bus";
			break;
		case 4 :
			t="bateau";
		case 5 :
			t="Attente";
			break;
		}
		return super.toString()+"& vehicule : "+t;
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
