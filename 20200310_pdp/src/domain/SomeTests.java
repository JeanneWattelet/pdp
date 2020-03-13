package domain;

import java.util.HashMap;
import java.util.HashSet;

import transport.*;

//import java.util.HashSet;
//import java.util.Set;

//import infra.Donnees;
//import infra.DonneesAgency;
//import infra.DonneesCalendar;

public class SomeTests {

	public static void main(String[] args) {
		
		long debut = System.currentTimeMillis();
		/*Graphe g = new Graphe();
		System.out.println(g);
		g.astar("Gare St-Jean", "Peixotto");*/
		
		/*System.out.println("---------");
		
		HashMap<String, HashMap<String, Double>> map = new HashMap<String, HashMap<String, Double>>();
		HashMap<String, Double> mapA = new HashMap<String, Double>();
		HashMap<String, Double> mapB = new HashMap<String, Double>();
		mapB.put("A", 6.0);
		mapA.put("B", 8.0);
		map.put("B", mapB);
		map.put("A", mapA);
		Graphe g2 = new Graphe(map);
		System.out.println(g2);
		g2.astar("A", "B");
		//System.out.println(g.astar());*/
		
		Carte v = new Carte("Ville");
		
		//les stations
		Station s1 = new Station("Porte Nord", 6, 14);
		Station s2 = new Station("Grande Place", 7, 11);
		Station s3 = new Station("Porte Est", 19, 15);
		Station s4 = new Station("Quai du Fleuve", 15, 13);
		Station s5 = new Station("Centre-Ville" , 11, 11);
		Station s6 = new Station("Quai de la Rivière", 7, 9);
		Station s7 = new Station("Porte Ouest", 0, 8);
		Station s8 = new Station("Hôpital", 11, 6);
		Station s9 = new Station("Parc Botanique", 12, 4);
		Station s10 = new Station("Hôtel de Ville", 15, 6);
		Station s11 = new Station("Porte Sud", 16, 0);
		
		//les trajets
		
		int j = 0;
		
		Trajet trajetstramA[] = new Trajet[200];
		Ligne tramA = new Ligne("A", 2);
		for(int i = 0 ; i<200 ; i++) {
			trajetstramA[i] = new Trajet("A"+i);
			trajetstramA[i].addArret(s7, new Horaire(1, 5+j, (4*i)%60));
			trajetstramA[i].addArret(s6, new Horaire(1, 5+j, (7+4*i)%60));
			trajetstramA[i].addArret(s5, new Horaire(1, 5+j, (9+4*i)%60));
			trajetstramA[i].addArret(s4, new Horaire(1, 5+j, (11+4*i)%60));
			trajetstramA[i].addArret(s3, new Horaire(1, 5+j, (14+4*i)%60));
			if(i%15==0) {
				j++;
			}
			tramA.addTrajet(trajetstramA[i]);
		}
		
		j=0;
		
		Trajet trajetstramB[] = new Trajet[200];
		Ligne tramB = new Ligne("A", 2);
		for(int i = 0 ; i<200 ; i++) {
			trajetstramB[i] = new Trajet("B"+i);
			trajetstramB[i].addArret(s1, new Horaire(1, 5+j, (3+7*i)%60));
			trajetstramB[i].addArret(s2, new Horaire(1, 5+j, (7+7*i)%60));
			trajetstramB[i].addArret(s5, new Horaire(1, 5+j, (10+7*i)%60));
			trajetstramB[i].addArret(s8, new Horaire(1, 5+j, (12+7*i)%60));
			if(i%8==0) {
				j++;
			}
			tramB.addTrajet(trajetstramB[i]);
		}
		
		
		
		/*Trajet tramA1 = new Trajet("A-1");
		tramA1.addArret(s7, new Horaire(1, 8, 0));
		tramA1.addArret(s6, new Horaire(1, 8, 7));
		tramA1.addArret(s5, new Horaire(1, 8, 9));
		tramA1.addArret(s4, new Horaire(1, 8, 11));
		tramA1.addArret(s3, new Horaire(1, 8, 14));
		
		Trajet tramA2 = new Trajet("A-2");
		tramA2.addArret(s7, new Horaire(1, 8, 10));
		tramA2.addArret(s6, new Horaire(1, 8, 17));
		tramA2.addArret(s5, new Horaire(1, 8, 19));
		tramA2.addArret(s4, new Horaire(1, 8, 21));
		tramA2.addArret(s3, new Horaire(1, 8, 24));
		
		Trajet tramA3 = new Trajet("A-3");
		tramA3.addArret(s7, new Horaire(1, 8, 20));
		tramA3.addArret(s6, new Horaire(1, 8, 27));
		tramA3.addArret(s5, new Horaire(1, 8, 29));
		tramA3.addArret(s4, new Horaire(1, 8, 31));
		tramA3.addArret(s3, new Horaire(1, 8, 34));
		
		Trajet tramA4 = new Trajet("A-4");
		tramA4.addArret(s7, new Horaire(1, 8, 14));
		tramA4.addArret(s6, new Horaire(1, 8, 7));
		tramA4.addArret(s5, new Horaire(1, 8, 5));
		tramA4.addArret(s4, new Horaire(1, 8, 3));
		tramA4.addArret(s3, new Horaire(1, 8, 0));
		
		Trajet tramA5 = new Trajet("A-5");
		tramA5.addArret(s7, new Horaire(1, 8, 24));
		tramA5.addArret(s6, new Horaire(1, 8, 17));
		tramA5.addArret(s5, new Horaire(1, 8, 15));
		tramA5.addArret(s4, new Horaire(1, 8, 13));
		tramA5.addArret(s3, new Horaire(1, 8, 10));
		
		Trajet tramA6 = new Trajet("A-6");
		tramA6.addArret(s7, new Horaire(1, 8, 34));
		tramA6.addArret(s6, new Horaire(1, 8, 27));
		tramA6.addArret(s5, new Horaire(1, 8, 25));
		tramA6.addArret(s4, new Horaire(1, 8, 23));
		tramA6.addArret(s3, new Horaire(1, 8, 20));
		*/
		
		
		
		/*
		tramA.addTrajet(tramA2);
		tramA.addTrajet(tramA3);
		tramA.addTrajet(tramA4);
		tramA.addTrajet(tramA5);
		tramA.addTrajet(tramA6);
		*/
		
		
		/*
		Trajet tramB1 = new Trajet("B-1");
		tramB1.addArret(s1, new Horaire(1, 8, 3));
		tramB1.addArret(s2, new Horaire(1, 8, 7));
		tramB1.addArret(s5, new Horaire(1, 8, 10));
		tramB1.addArret(s8, new Horaire(1, 8, 12));
		
		Trajet tramB2 = new Trajet("B-2");
		tramB2.addArret(s1, new Horaire(1, 8, 13));
		tramB2.addArret(s2, new Horaire(1, 8, 17));
		tramB2.addArret(s5, new Horaire(1, 8, 20));
		tramB2.addArret(s8, new Horaire(1, 8, 22));
		
		Trajet tramB3 = new Trajet("B-3");
		tramB3.addArret(s1, new Horaire(1, 8, 23));
		tramB3.addArret(s2, new Horaire(1, 8, 27));
		tramB3.addArret(s5, new Horaire(1, 8, 30));
		tramB3.addArret(s8, new Horaire(1, 8, 32));
		
		Trajet tramB4 = new Trajet("B-4");
		tramB4.addArret(s1, new Horaire(1, 8, 12));
		tramB4.addArret(s2, new Horaire(1, 8, 8));
		tramB4.addArret(s5, new Horaire(1, 8, 5));
		tramB4.addArret(s8, new Horaire(1, 8, 3));
		
		Trajet tramB5 = new Trajet("B-5");
		tramB5.addArret(s1, new Horaire(1, 8, 22));
		tramB5.addArret(s2, new Horaire(1, 8, 18));
		tramB5.addArret(s5, new Horaire(1, 8, 15));
		tramB5.addArret(s8, new Horaire(1, 8, 13));
		
		Trajet tramB6 = new Trajet("B-6");
		tramB6.addArret(s1, new Horaire(1, 8, 32));
		tramB6.addArret(s2, new Horaire(1, 8, 28));
		tramB6.addArret(s5, new Horaire(1, 8, 25));
		tramB6.addArret(s8, new Horaire(1, 8, 23));
		
		Ligne tramB = new Ligne("B", 2);
		tramB.addTrajet(tramB1);
		tramB.addTrajet(tramB2);
		tramB.addTrajet(tramB3);
		tramB.addTrajet(tramB4);
		tramB.addTrajet(tramB5);
		tramB.addTrajet(tramB6);
		*/
		
		Trajet bus11 = new Trajet("1-1");
		bus11.addArret(s4, new Horaire(1, 8, 7));
		bus11.addArret(s10, new Horaire(1, 8, 13));
		bus11.addArret(s11, new Horaire(1, 8, 19));
		
		Trajet bus12 = new Trajet("1-2");
		bus12.addArret(s4, new Horaire(1, 8, 21));
		bus12.addArret(s10, new Horaire(1, 8, 31));
		bus12.addArret(s11, new Horaire(1, 8, 38));
		
		Trajet bus13 = new Trajet("1-3");
		bus13.addArret(s4, new Horaire(1, 8, 27));
		bus13.addArret(s10, new Horaire(1, 8, 7));
		bus13.addArret(s11, new Horaire(1, 8, 3));
		
		Trajet bus14 = new Trajet("1-4");
		bus14.addArret(s4, new Horaire(1, 8, 32));
		bus14.addArret(s10, new Horaire(1, 8, 28));
		bus14.addArret(s11, new Horaire(1, 8, 18));
		
		Ligne bus1 = new Ligne("1", 1);
		bus1.addTrajet(bus11);
		bus1.addTrajet(bus12);
		bus1.addTrajet(bus13);
		bus1.addTrajet(bus14);
		
		
		Trajet bus21 = new Trajet("2-1");
		bus21.addArret(s2, new Horaire(1, 8, 0));
		bus21.addArret(s6, new Horaire(1, 8, 4));
		bus21.addArret(s9, new Horaire(1, 8, 9));
		bus21.addArret(s11, new Horaire(1, 8, 14));
		
		Trajet bus22 = new Trajet("2-2");
		bus22.addArret(s2, new Horaire(1, 8, 25));
		bus22.addArret(s6, new Horaire(1, 8, 27));
		bus22.addArret(s9, new Horaire(1, 8, 30));
		bus22.addArret(s11, new Horaire(1, 8, 34));
		
		Trajet bus23 = new Trajet("2-3");
		bus23.addArret(s2, new Horaire(1, 8, 22));
		bus23.addArret(s6, new Horaire(1, 8, 18));
		bus23.addArret(s9, new Horaire(1, 8, 16));
		bus23.addArret(s11, new Horaire(1, 8, 10));
		
		Trajet bus24 = new Trajet("2-4");
		bus24.addArret(s2, new Horaire(1, 8, 27));
		bus24.addArret(s6, new Horaire(1, 8, 24));
		bus24.addArret(s9, new Horaire(1, 8, 20));
		bus24.addArret(s11, new Horaire(1, 8, 14));
		
		Ligne bus2 = new Ligne("2", 1);
		bus2.addTrajet(bus21);
		bus2.addTrajet(bus22);
		bus2.addTrajet(bus23);
		bus2.addTrajet(bus24);
		
		v.addLigne(tramA);
		v.addLigne(tramB);
		v.addLigne(bus1);
		v.addLigne(bus2);
		
		HashSet<Integer> set = new HashSet<Integer>();
		set.add(1);
		set.add(2);
		
		Horaire h = new Horaire(1, 8, 0);
		
		GrapheTrajet g = new GrapheTrajet(v.getTrajets(h, set));
		
		

		g.astar(s11, s2, h, v.getTrajets(h, set));
	
		
		//g.dijkstra(s11, s2, h, v.getTrajets(h, set));
		System.out.println(System.currentTimeMillis()-debut+" millisecondes");
	}

}
