package Transport;
import java.util.*;

public class Ligne implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String nom;
	private int vehicule;
	private List<Trajet> trajets;

	
	public Ligne(String id, String n, int v, List<Trajet> t) {
		this.id = id ;
		this.trajets = t;
		this.nom=n;
		this.vehicule=v;
	}

	public Ligne(String id, String n, int v) {
		this(id, n, v, new ArrayList<Trajet>());
	}

	public List<Trajet> getTrajets() {
		return trajets;
	}

	public String getId() {
		return this.id;
	}

	public String getNom() {
		return nom;
	}

	public int getVehicule() {
		return vehicule;
	}

	public void addTrajet(Trajet t) {
		trajets.add(t);
	}

	public List<Trajet> getTrajetsAfter(Horaire h) {
		List<Trajet> rep = new ArrayList<Trajet>();
		for(Trajet t: trajets)
			rep.add(new Trajet( t.getId(), t.getArretsAfter(h), t.getDirection(), t.getCalendrier())); 
		return rep;
	}

	public String getStringVehicule() {
		switch(vehicule) {
		case 0:
			return "Tramway";
		case 1:
			return "Metro";
		case 2:
			return "Train";
		case 3 :
			return "Bus";
		case 4 :
			return "Bateau";
		case 5 :
			return "Attente";
		}
		return "Pied";
	}

	@Override
	public String toString() {
		return this.getStringVehicule()+" : "+this.nom;
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==this)
			return true;
		if(obj==null)
			return false;
		if(!(obj instanceof Ligne))
			return false;
		Ligne o = (Ligne) obj;
		if(this.id.equals(o.id))
			return true;
		return false;
	}


}
