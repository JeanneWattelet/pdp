package transport;

public class Horaire {
	private int jour;
	private int heure;
	private int minute;
	private int seconde;
	
	/*
	 * Définition des contantes pour les trajets : clarification du code.
	 * Serait-il plus pratique de créer une classe statique à part pour les jours ?
	 */
	public final static int LUNDI = 1; 
	public final static int MARDI = 2; 
	public final static int MERCREDI = 3;
	public final static int JEUDI = 4;
	public final static int VENDREDI = 5;
	public final static int SAMEDI = 6;
	public final static int DIMANCHE = 7;
	public final static int FERIER = 8;
	
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
		if(this.jour != h.getJour() ) {
			return false;
		}
			
		if(this.heure*10000+this.minute*100+this.seconde < h.getHeure()*10000+h.getMinute()*100+h.getSeconde()) {
			return true;
		}
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
		
		return res>0?res:0-res;
	}
	
	public static String getStringJour(int j) {
		switch(j) {
			case LUNDI :
				return "Lundi";
			case MARDI :
				return "Mardi";
			case MERCREDI :
				return "Mercredi";
			case JEUDI :
				return "Jeudi";
			case VENDREDI :
				return "Vendredi";
			case SAMEDI :
				return "Samedi";
			case DIMANCHE :
				return "Dimanche";
			case FERIER :
				return "Ferier";
		}
		return "Jour ferier";
	}
	
	public static int stringToIntDate(String j) {
		switch(j) {
		case "Lundi" :
			return LUNDI;
		case "Mardi":
			return MARDI;
		case "Mercredi" :
			return MERCREDI;
		case "Jeudi" :
			return JEUDI;
		case "Vendredi" :
			return VENDREDI;
		case "Samedi" :
			return SAMEDI;
		case "Dimanche" :
			return DIMANCHE;
		case "Ferier" :
			return FERIER;
	}
	return FERIER;
}
	
	@Override
	public String toString() {
		return this.jour+":"+this.heure+":"+this.minute+":"+this.seconde;
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
