package transport;
import java.util.ArrayList;
import java.util.List;

public class Calendrier {

	private String service_id;
	private List<Integer> semaine;
	private int date_debut, date_fin;

	public Calendrier(String service_id) {
		this.service_id = service_id ;
	}

	public void setSemaine(List<Integer> sem) {
		this.semaine = new ArrayList<Integer>();
		for(Integer i: sem)
			this.semaine.add(i);
	}

	public void setDate_debut(Integer date_debut) {
		this.date_debut = date_debut;
	}

	public void setDate_fin(int date_fin) {
		this.date_fin = date_fin;
	}

	public String getService_id() {
		return service_id;
	}

	public List<Integer> getSemaine() {
		return semaine;
	}

	public int getDate_debut() {
		return date_debut;
	}

	public int getDate_fin() {
		return date_fin;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==this)
			return true;
		if(obj==null)
			return false;
		if(!(obj instanceof Calendrier))
			return false;
		Calendrier o = (Calendrier) obj;
		if(!this.service_id.equals( o.service_id ))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		return this.service_id.hashCode();
	}
	
}
