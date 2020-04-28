package transport;
import java.util.*;

public class Trajet {
	
	private Map<Station, Horaire> arrets;
	private String id;
	private String direction ;
	private Calendrier service; 

	public Trajet(String id, Map<Station, Horaire> arrets, String direction, Calendrier service) {
		this.id = id;
		this.arrets= arrets;
		this.direction = direction ;
		this.service = service;
	}

	public Trajet(String id, String direction, Calendrier service) {
		this(id, new HashMap<Station, Horaire>(), direction, service);
	}

	public Map<Station, Horaire> getArrets() {
		Map<Station, Horaire> rep = new HashMap<Station, Horaire>();
		Horaire t;
		for( Station s: arrets.keySet() ) {
			t = arrets.get(s);
			rep.put(s, t);
		}
		return rep;
	}


	public Map<Station, Horaire> getArretsAfter(Horaire h) {
		Map<Station, Horaire> rep = new HashMap<Station, Horaire>();
		Horaire t;
		for( Station s: arrets.keySet() ) {
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

	public String getDirection() {
		return direction;
	}

	public Calendrier getCalendrier() {
		return service;
	}

	public void addArret(Station s, Horaire h) {
		arrets.put(s, h);
	}

	@Override
	public String toString() {
		String trajet = "";
		for(Station s: this.arrets.keySet()) {
			trajet=trajet+" "+s+" : "+arrets.get(s)+" ; ";
		}
		trajet=trajet+"en direction de: "+this.direction;
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
		return true;
	}
}
