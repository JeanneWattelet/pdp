package transport;

public class Horaire {
	private String jour;
	private int heure;
	private int minute;
	
	/*
	 * Définition des contantes pour les trajets : clarification du code.
	 * Serait-il plus pratique de créer une classe statique à part pour les jours ?
	 */
	public final static String LUNDI = "Lundi"; 
	public final static String MARDI = "Mardi"; 
	public final static String MERCREDI = "Mercredi";
	public final static String JEUDI = "Jeudi";
	public final static String VENDREDI = "Vendredi";
	public final static String SAMEDI = "Samedi";
	public final static String DIMANCHE = "Dimanche";
	public final static String FERIER = "Ferier";
	
	public Horaire(String j, int h, int m) {
		jour=j;
		heure = h;
		minute = m;
	}
	
	public String getJour() {
		return jour;
	}
	
	public int getHeure() {
		return heure;
	}
	
	public int getMinute() {
		return minute;
	}
	
	public boolean estAvant(Horaire h) {
		if(this.heure>h.heure)
			return false;
		if((this.heure>4)&&(h.heure<4)) {
			//changement theorique de jour entre this et h
		}else {
			if(this.heure>h.getHeure()) {
				return false;
			}
		}
		if(this.minute>h.minute)
			return false;
		return true;
	}
	
	public int tempsEntre(Horaire horaire) {
		int res=0;
		if(this.heure>horaire.getHeure()) {//S'il se passe un jour entre maintenant et l'horaire d'arrivee. Nous supposons que les autres fonctions ont deja verifie que l'utilisateur ne met pas plus de deux jours a traverser une ville
			res+=60*(23-this.heure)+horaire.getHeure();//heure jusqu'a minuit puis heure depuis minuit
			res+=60-this.minute+horaire.getMinute();//idem minutes
		}else {
			res+=60*(horaire.getHeure()-this.heure);//On compte l'heure deja commencee comme complete
			res+=horaire.getMinute()-this.minute;//on soustrait les minutes deja passees si besoin
		}
		return res;
	}
	
	public String getStringJour() {
		switch(jour) {
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
	
	@Override
	public String toString() {
		return this.getStringJour()+" "+this.heure+":"+this.minute;
	}
	
	@Override
	public int hashCode() {
		return this.jour.hashCode()+this.heure*100+this.minute;
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
