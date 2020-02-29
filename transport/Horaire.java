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
}
