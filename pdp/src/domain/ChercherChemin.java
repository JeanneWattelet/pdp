package domain;

import java.util.Date;
import java.util.List;

import transport.Horaire;
import transport.Ligne;

public class ChercherChemin {
	private GrapheTrajet g;
	
	public ChercherChemin() {
		Date d = new Date();
		//g = SerializeGrapheTrajet.deserialiserGrapheTrajet(trouverJour(d.getDay()-1));
		g = SerializeGrapheTrajet.deserialiserGrapheTrajet(trouverJour(7));
		/*
		 * PENSER A CHANGER LE JOUR PAS DEFAUT !!
		 */
	}
	
	private int trouverJour(int j) {
		if(j<4) {
			return 3;
		}
		if(j<6) {
			return 5;
		}
		if(j==6)
			return j;
		return 7;
	}
	
	public Horaire creerHoraire(String horaire) {
		int h=0,m=0,sec=0;//j=0,
		String[] rep = horaire.split(":");
		h = Integer.valueOf(rep[0]);
		m = Integer.valueOf(rep[1]);
		sec = Integer.valueOf(rep[2]);
		return new Horaire(g.getJour(), h, m, sec); //j,
	}
	
	public List<ArcTrajet> chercherChemin(String from, String to, String h2, int jour, int objectif, boolean partirA){
		int j = trouverJour(jour);
		if(j!=g.getJour()) {
			g = SerializeGrapheTrajet.deserialiserGrapheTrajet(j);
		}
		
		Horaire h = creerHoraire(h2);
		
		if(partirA) {
			switch(objectif) {
				case 1:
					return g.dijkstraPenalisant(from, to, h, Ligne.PIED);
				case 2:
					return g.dijkstraPenalisant(from, to, h, Ligne.ATTENTE);
				case 3:
					return g.dijkstraPenalisant(from, to, h, Ligne.ATTENTE);
				default:
					return g.dijkstra(from, to, h);
			}	
		}
		switch(objectif) {
			case 1:
				return g.dijkstraArriverAPenalisant(from, to, h, Ligne.PIED);
			case 2:
				return g.dijkstraArriverAPenalisant(from, to, h, Ligne.ATTENTE);
			case 3:
				return g.dijkstraArriverAPenalisant(from, to, h, Ligne.ATTENTE);
			default:
				return g.dijkstraArriverA(from, to, h);
		}
	}
}
