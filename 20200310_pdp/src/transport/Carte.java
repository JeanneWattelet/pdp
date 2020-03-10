package transport;

import java.util.*;

public class Carte {
	private String ville;
	private Set<Ligne> lignes;
	
	public Carte(String v) {
		ville =v;
		lignes = new HashSet<Ligne>();
	}

	public String getVille() {
		return ville;
	}

	public Set<Ligne> getLignes() {
		return lignes;
	}
	
	public void addLigne(Ligne l) {
		lignes.add(l);
	}
	
	public Set<Trajet> getTrajets(Horaire h, Set<Integer> v) {
		Set<Trajet> rep = new HashSet<Trajet>();
		Iterator<Integer> i = v.iterator();
		
		Ligne l;
		int mdt;
		while(i.hasNext()) {
			mdt=i.next();
			Iterator<Ligne> j = lignes.iterator();
			while(j.hasNext()) {
				l=j.next();
				if(l.getVehicule()==mdt)
					rep.addAll(l.getTrajetsAfter(h));
			}
		}
		return rep;
	}
	
	@Override
	public String toString() {
		return this.ville;
	}
	
	@Override
	public int hashCode() {
		return this.ville.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==this)
			return true;
		if(obj==null)
			return false;
		if(!(obj instanceof Carte))
			return false;
		Carte o = (Carte) obj;
		if(o.ville!=this.ville)
			return false;
		if(o.lignes!=this.lignes)
			return false;
		return true;
	}
}
