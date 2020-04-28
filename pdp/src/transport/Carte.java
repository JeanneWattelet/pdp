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
	
	public List<Ligne> getTrajets(Horaire h, Set<String> v) {
		List<Ligne> rep = new ArrayList<Ligne>();
		for(String vehicule: v) {
			for(Ligne l: lignes) {
				List<Trajet> traj = new ArrayList<Trajet>();
				if(l.getVehicule() == vehicule)
					traj.addAll(l.getTrajetsAfter(h));
				rep.add(new Ligne(l.getId(), l.getNom(), l.getVehicule(),traj));
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
