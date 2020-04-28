package transport;
//import java.util.ArrayList;

public class Station {
	
	private String id;
	private String nom;
	private Coordonnees position;
	//private ArrayList<Horaire> horaires;

	public Station(String id, String n, Coordonnees c) {
		this.id = id;
		this.nom = n;
		this.position = c;
		//horaires = new ArrayList<Horaire>();
	}

	public Station(String id, String n, float x, float y) {
		this(id, n, new Coordonnees(x, y));
	}
	
	public Station(String id) {
		this.id = id ;
	}
	
	public void setNom(String nom) {
		this.nom = nom;
	}

	public void setPosition(Coordonnees position) {
		this.position = position;
	}
	public void setPosition(Float x, Float y) {
		Coordonnees c = new Coordonnees(x,y);
		this.setPosition(c);
	}

	public String getId() {
		return id;
	}

	public String getNom() {
		return nom;
	}

	public Coordonnees getPosition() {
		return position;
	}

	/*public double DistanceA(Station to) { la distance entre 2 stations 
		System.out.println(Math.cos(Math.toRadians(this.position.getX())));
		return (6366*Math.acos( Math.cos(Math.toRadians(this.position.getX()))
				*Math.cos(Math.toRadians(to.getPosition().getX()))*Math.cos(Math.toRadians(to.getPosition().getY()))
				-Math.toRadians(this.getPosition().getY())+Math.sin(Math.toRadians(this.position.getX()))
				*Math.sin(Math.toRadians(to.position.getX())) ) );
	}*/
	
	
	// Le temps de Marche (en moyenne) entre 2 stations 
	/*public double TempsMarcheVers(Station to) { // la vitesse moyenne de l'homme en france est estimée a 4.7 km/h
		return  (this.DistanceA(to))/4700 ;
	}*/

	@Override
	public String toString() {
		return this.getNom();
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
		if(!(obj instanceof Station))
			return false;
		Station o = (Station) obj;
		if(o.id == this.id)
			return true;
		return false;
	}

}

