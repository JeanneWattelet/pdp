package transport;

public class Horaire {
	private int jour;
	private int heure;
	private int minute;
	
	public Horaire(int j, int h, int m) {
		jour=j;
		heure = h;
		minute = m;
	}
	
	public int getJour() {
		return jour;
	}
	
	public int getHeure() {
		return heure;
	}
	
	public int getMinute() {
		return minute;
	}
	
	public boolean estAvant(Horaire h) {
		if(h.jour!=this.jour)
			return false;
		if(this.heure>h.heure)
			return false;
		if(this.heure<h.heure)
			return true;
		if(this.minute>h.minute)
			return false;
		return true;
	}
	
	public String getStringJour() {
		switch(jour) {
			case 1 :
				return "Lundi";
			case 2 :
				return "Mardi";
			case 3 :
				return "Mercredi";
			case 4 :
				return "Jeudi";
			case 5 :
				return "Vendredi";
			case 6 :
				return "Samedi";
			case 7 :
				return "Dimanche";
			default :
		}
		return "Jour ferier";
	}
	
	@Override
	public String toString() {
		return this.getStringJour()+" "+this.heure+":"+this.minute;
	}
	
	@Override
	public int hashCode() {
		return this.jour*10000+this.heure*100+this.minute;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==this)
			return true;
		if(obj==null)
			return false;
		if(!(obj instanceof Horaire))
			return false;
		Horaire o = (Horaire) obj;
		if(o.jour!=this.jour)
			return false;
		if(o.heure!=this.heure)
			return false;
		if(o.minute!=this.minute)
			return false;
		return true;
	}
}
