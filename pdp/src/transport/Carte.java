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
	
	public Set<Ligne> getTrajets(Horaire h, Set<String> v) {
		Set<Ligne> rep = new HashSet<Ligne>();
		Iterator<String> i = v.iterator();
		Ligne l;
		String mdt;
		while(i.hasNext()) {
			mdt=i.next();
			Iterator<Ligne> j = lignes.iterator();
			while(j.hasNext()) {
				Set<Trajet> traj = new HashSet<Trajet>();
				l=j.next();
				if(l.getVehicule()==mdt)
					traj.addAll(l.getTrajetsAfter(h));
				rep.add(new Ligne(l.getNom(), l.getVehicule(),traj));
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
