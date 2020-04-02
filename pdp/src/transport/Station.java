package transport;

//import java.util.ArrayList;

public class Station {
	private String nom;
	private Coordonnees position;
	//private ArrayList<Horaire> horaires;
	
	public Station(String n, Coordonnees c) {
		this.nom = n;
		this.position = c;
		//horaires = new ArrayList<Horaire>();
	}
	
	public Station(String n, float x, float y) {
		this(n, new Coordonnees(x, y));
	}

	public String getNom() {
		return nom;
	}

	public Coordonnees getPosition() {
		return position;
	}
	
	/*public ArrayList<Horaire> getHoraires(){
		return horaires;
	}*/

	@Override
	public String toString() {
		return this.nom;
	}
	
	@Override
	public int hashCode() {
		return this.nom.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==this)
			return true;
		if(obj==null)
			return false;
		if(!(obj instanceof Station))
			return false;
		Station o = (Station) obj;
		//System.out.println(o.nom+" / "+this.nom);
		if(o.nom!=this.nom)
			return false;
		if(o.position!=this.position)
			return false;
		return true;
	}
	
}
