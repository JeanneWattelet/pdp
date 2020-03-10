package transport;

public class Station {
	private String nom;
	private Coordonnees position;
	
	public Station(String n, Coordonnees c) {
		this.nom = n;
		this.position = c;
	}
	
	public Station(String n, float x, float y) {
		this(n, new Coordonnees(x, y));
	}

	public String getNom() {
		return nom;
	}

	public Coordonnees getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return this.nom;
	}
	
	@Override
	public int hashCode() {
		return this.nom.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==this)
			return true;
		if(obj==null)
			return false;
		if(!(obj instanceof Station))
			return false;
		Station o = (Station) obj;
		if(o.nom!=this.nom)
			return false;
		if(o.position!=this.position)
			return false;
		return true;
	}
	
}
