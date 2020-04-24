package transport;

import java.util.*;

public class Ligne {
	private Set<Trajet> trajets;
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
	
	public Ligne(String n, String v, Set<Trajet> t) {
		this.trajets = t;
		this.nom=n;
		this.vehicule=v;
	}
	
	public Ligne(String n, String v) {
		this(n, v, new HashSet<Trajet>());
	}

	public Set<Trajet> getTrajets() {
		return trajets;
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
	
	public Set<Trajet> getTrajetsAfter(Horaire h) {
		Set<Trajet> rep = new HashSet<Trajet>();
		Iterator<Trajet> i = trajets.iterator();
		Trajet t;
		int cpt=0;
		while(i.hasNext()) {
			cpt++;
			t=i.next();
			rep.add(new Trajet(this.toString()+h.toString()+cpt, t.getArretsAfter(h)));
		}
		return rep;
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
