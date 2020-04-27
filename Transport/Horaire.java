package Transport;
public class Horaire {
	
	private int jour;
	private int heure;
	private int minute;
	private int seconde;

	public Horaire(int j, int h, int m, int seconde) {
		jour=j;
		heure = h;
		minute = m;
		this.seconde = seconde ;
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

	public int getSeconde() {
		return seconde;
	}

	public boolean estAvant(Horaire h) {
		if(this.jour != h.getJour() )
			return false;
		if(this.heure*3600+this.minute*60+this.seconde < h.getHeure()*3600+h.getMinute()*60+h.getSeconde())
			return true;
		return false;
	}

	public int tempsEntre(Horaire horaire) {
		int res=0; // on convertit tout en seconde et on renvoie la différence
		if(this.estAvant(horaire)) { //
			res += ( 3600*horaire.getHeure()+60*horaire.getMinute()+horaire.getSeconde() )
					- ( 3600*this.heure+60*this.minute+this.seconde) ;

		}else if(horaire.estAvant(this)){
			res += ( 3600*this.heure+60*horaire.minute+horaire.seconde )
					- ( 3600*this.heure+60*this.minute+this.seconde) ;
		}else if(this.equals(horaire) ){
			res += 0; // s'ils sont eguaux (pas possble dans notre cas)
		}else {
			res += Integer.MAX_VALUE; //si c'est pas le meme jour on renvoie une valeur enorme;
		}
		return res;
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
		return this.jour+":"+this.heure+":"+this.minute+":"+this.seconde;
	}

	@Override
	public int hashCode() {
		return this.jour*10000+this.heure*100+this.minute*10+this.seconde;
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
		if(o.seconde!=this.seconde)
			return false;
		return true;
	}
}
