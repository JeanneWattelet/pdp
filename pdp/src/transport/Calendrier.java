package transport;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Calendrier implements Serializable{

	private static final long serialVersionUID = 930378180174152122L;

	private String service_id;
	private List<Integer> semaine;

	public Calendrier(String service_id) {
		this.service_id = service_id ;
	}

	public void setSemaine(List<Integer> sem) {
		this.semaine = new ArrayList<Integer>();
		for(Integer i: sem)
			this.semaine.add(i);
	}

	public String getService_id() {
		return service_id;
	}

	public List<Integer> getSemaine() {
		return semaine;
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
