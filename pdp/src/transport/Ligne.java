package transport;

import java.util.*;

public class Ligne {
	private Set<Trajet> trajets;
	private String nom;
	private int vehicule;
	
	/*
	 * Définition des contantes pour les trajets : clarification du code.
	 * Serait-il plus pratique de créer une classe statique à part pour les transports ?
	 */
	public final static int ATTENTE = 0; 
	public final static int BUS = 1; 
	public final static int TRAM = 2;
	public final static int METRO = 3;
	public final static int BATEAU = 4;
	public final static int PIED = 5;	
	
	public Ligne(String n, int v, Set<Trajet> t) {
		this.trajets = t;
		this.nom=n;
		this.vehicule=v;
	}
	
	public Ligne(String n, int v) {
		this(n, v, new HashSet<Trajet>());
	}

	public Set<Trajet> getTrajets() {
		return trajets;
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
	
	public String getStringVehicule() {
		switch(vehicule) {
			case ATTENTE:
				return "attente";
			case BUS:
				return "bus";
			case TRAM:
				return "tram";
			case METRO :
				return "metro";
			case BATEAU :
				return "bateau";
			case PIED :
				return "pied";
		}
		return "pied";
	}
	
	@Override
	public String toString() {
		return this.getStringVehicule()+" : "+this.nom;
	}
	
	@Override
	public int hashCode() {
		return this.nom.hashCode()+this.vehicule;
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
