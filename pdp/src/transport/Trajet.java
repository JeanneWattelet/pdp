package transport;
import java.util.*;

public class Trajet {
	private Map<Station, Horaire> arrets;
	private String id;
	
	public Trajet(String id, Map<Station, Horaire> arrets) {
		this.id = id;
		this.arrets= arrets;
	}
	
	public Trajet(String id) {
		this(id, new HashMap<Station, Horaire>());
	}

	public Map<Station, Horaire> getArrets() {
		Map<Station, Horaire> rep = new HashMap<Station,Horaire>();
		Set<Station> stations = arrets.keySet();
		Station s;
		Horaire h;
		Iterator<Station> i = stations.iterator();
		while(i.hasNext()){
			s=i.next();
			h = arrets.get(s);
			rep.put(s, h);
		}
		return rep;
	}

	public Map<Station, Horaire> getArretsAfter(Horaire h) {
		Map<Station, Horaire> rep = new HashMap<Station, Horaire>();
		Set<Station> stations = arrets.keySet();
		Station s;
		Horaire t;
		Iterator<Station> i = stations.iterator();
		while(i.hasNext()){
			s=i.next();
			t = arrets.get(s);
			if(h.estAvant(t)) {
				rep.put(s, t);
			}
		}
		return rep;
	}
	
	public String getId() {
		return id;
	}
	
	public void addArret(Station s, Horaire h) {
		arrets.put(s, h);
	}
	
	@Override
	public String toString() {
		String trajet = "";
		Set<Station> stations = this.arrets.keySet();
		Iterator<Station> i = stations.iterator();
		Station s;
		while(i.hasNext()) {
			s=i.next();
			trajet=trajet+" "+s+" : "+arrets.get(s)+" ; ";
		}
		return trajet;
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
		if(!(obj instanceof Trajet))
			return false;
		Trajet o = (Trajet) obj;
		if(o.id!=this.id)
			return false;
		if(o.arrets!=this.arrets)
			return false;
		return true;
	}
}
