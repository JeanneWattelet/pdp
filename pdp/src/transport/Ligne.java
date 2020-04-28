package transport;

import java.util.*;

public class Ligne {
	private String id;
	private List<Trajet> trajets;
	private String nom;
	private String vehicule;
	
	/*
	 * Définition des contantes pour les trajets : clarification du code.
	 * Serait-il plus pratique de créer une classe statique à part pour les transports ?
	 */
	public final static String ATTENTE = "Attente"; 
	public final static String BUS = "Bus"; 
	public final static String TRAM = "Tram";
	public final static String METRO = "Metro";
	public final static String BATEAU = "Bateau";
	public final static String PIED = "Marche";	
	public final static String TRAIN = "Train";	
	
	public Ligne(String i, String n, String v, List<Trajet> t) {
		this.id = i;
		this.trajets = t;
		this.nom=n;
		this.vehicule=v;
	}
	
	public Ligne(String i, String n, String v) {
		this(i, n, v, new ArrayList<Trajet>());
	}

	public List<Trajet> getTrajets() {
		return trajets;
	}

	public String getId() {
		return id;
	}
	
	public String getNom() {
		return nom;
	}

	public String getVehicule() {
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
	
	public static String intToStringVehicule(int i) {
		switch(i) {
		case 0:
			return TRAM;
		case 1:
			return METRO;
		case 2:
			return TRAIN;
		case 3 :
			return BUS;
		case 4 :
			return BATEAU;
		case 5 :
			return ATTENTE;
		}
		return PIED;
	}
	
	@Override
	public String toString() {
		return this.getVehicule()+" : "+this.nom;
	}
	
	@Override
	public int hashCode() {
		return this.nom.hashCode()+this.vehicule.hashCode();
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
		if(o.nom!=this.nom)
			return false;
		if(o.vehicule!=this.vehicule)
			return false;
		if(o.trajets!=this.trajets)
			return false;
		return true;
	}

	
}
