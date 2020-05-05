package domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import transport.*;

public class Donnees {

	public Donnees() {

	}

	private static List<Station> ChargerStations(String chemin) throws IOException{
		Path p1 = Paths.get(chemin+"\\stops.txt");
		BufferedReader read = Files.newBufferedReader(p1);

		String ligne;
		String[] str = null;
		String id; 
		float x,y ;
		String name;
		List<Station> stations = new ArrayList<Station>();

		ligne = read.readLine(); // lire la premiere ligne qui nous sert a rien 

		while( ( ligne = read.readLine() ) != null ) {
			//System.out.println(ligne) ;
			str = ligne.split(",") ;
			id = str[0];
			name = str[1];
			x = Float.valueOf(str[2]);
			y = Float.valueOf(str[3]);
			stations.add(new Station(id,name,x,y));
		}
		return stations;
	}

	private static List<Ligne> ChargerLignes(String chemin) throws IOException {
		String ligne;
		String[] str = null;
		String id;
		String name;
		int type_vehicule;

		Path p2 = Paths.get(chemin+"/routes.txt");
		BufferedReader read = Files.newBufferedReader(p2);
		List<Ligne> lignes = new ArrayList<Ligne>();

		ligne = read.readLine();
		while( ( ligne = read.readLine() ) != null) {
			str = ligne.split(",") ;
			id = str[0];
			name = str[2];
			type_vehicule = Integer.valueOf(str[3]);
			lignes.add(new Ligne(id, name, Ligne.intToStringVehicule(type_vehicule)));
		}
		////////leurs trajets:///////////////////////////////////////////////////////////////////////////////////////////////

		Path p3 = Paths.get(chemin+"/trips.txt");
		read = Files.newBufferedReader(p3);
		ligne = read.readLine();
		while( ( ligne = read.readLine() ) != null ) {
			str = ligne.split(",");
			for(Ligne l: lignes) {
				if( l.getId().equals(str[0]) ) {
					l.addTrajet(new Trajet(str[2],str[3],new Calendrier(str[1])));
				}
			}
		}

		System.out.println("Chargement des trajets terminé");

		/*for(Ligne l : lignes) {
				for(Trajet t: l.getTrajets()) 
					System.out.println(l.getId()+"||||"+t.getId()+"||||"+t.getDirection()+"||||"+t.getCalendrier().getService_id());
			}*/

		////////Remplir les calendriers des trajets///////////////////////////////////////////////////////////////////////////////

		Path p4 = Paths.get(chemin+"/calendar.txt");
		read = Files.newBufferedReader(p4);

		int date_debut = 5 ;
		int date_fin;
		int i; // pour la boucle pas important
		List<Integer> semaine = new ArrayList<Integer>() ;

		ligne = read.readLine();

		while( ( ligne = read.readLine() ) != null ) {
			str = ligne.split(",");
			// on recupere la date du debut et fin du service et les jours de la semaine 
			date_debut = Integer.valueOf(str[8]);
			date_fin = Integer.valueOf(str[9]);
			for(i = 1; i<8; i++) 
				semaine.add(Integer.valueOf(str[i]));
			// On remplie les services de chaque trajet 
			for(Ligne l: lignes) {
				for(Trajet t: l.getTrajets()) {
					if( t.getCalendrier().getService_id().equals(str[0]) ) {
						t.getCalendrier().setDate_debut(date_debut);
						t.getCalendrier().setDate_fin(date_fin);
						t.getCalendrier().setSemaine(semaine);
					}
				}
			}
			semaine.clear(); // vider notre semaine pour en recuperer une autre 
		}

		System.out.println("Chargement des calendriers terminé");

		/*for(Ligne l : lignes) {
				for(Trajet t: l.getTrajets()) {
					System.out.println(t.getCalendrier().getService_id()+"####"
							+t.getCalendrier().getSemaine()+" "+t.getCalendrier().getDate_debut()+" "+t.getCalendrier().getDate_fin());

				}
			}*/


		/////////récuperer les stations (et les creer en meme temps) et leurs horaires pour chaque trajet////////////////////////

		Path p5 = Paths.get(chemin+"/stop_times.txt");
		read = Files.newBufferedReader(p5);


		String station_id;   // pour recuperer les stations une par une 
		Integer jour, h, min , sec;
		//Station s;
		//Horaire hr;
		String[] sttr = null;
		int j=0; // juste pour la boucle for, pour ne pas en creer a chaque itération 

		ligne = read.readLine();

		while( (ligne = read.readLine() ) != null) {
			str = ligne.split(",");
			// on recupre le id du trajet et on le cherche dans ttt les trajts qu'on a pour lui rajouter des arrets:(station,horaire)
			for(Ligne l: lignes) {
				for(Trajet t: l.getTrajets()) {
					if( t.getId().equals(str[0]) ) {
						station_id = str[3];
						//on recupere la semaine du trajet pour savoir quels jours il est dispo
						for(j=0; j<t.getCalendrier().getSemaine().size(); j++) {
							if(t.getCalendrier().getSemaine().get(j) == 1) {//si le trajet est en service ce jour "j", alors ajoute l'arret au trajet
								jour = j+1; // lundi = 1, mardi= 2 .... et le tableau commence a 0, donc j+1;
								sttr = str[1].split(":"); //transformer le string(h:m:s) en h et m et s et en(int)
								h = Integer.valueOf(sttr[0]);
								min = Integer.valueOf(sttr[1]);
								sec = Integer.valueOf(sttr[2]);
								//Station s = new Station(station_id);//on cree la station pour chaque jour (sinon on pourra pas la "add" dans map car elle prend pas de doublon)
								//hr = new Horaire(jour, h, min, sec);
								t.addArret(new Station(station_id), new Horaire(jour, h, min, sec)); 
							}
						}	
					}
				}
			}
		}

		System.out.println("Chargement des arrets terminé");

		/*for(Ligne l :lignes) {
				for(Trajet t: l.getTrajets()) {
					for(Station k: t.getArrets().keySet()) {
						System.out.println(k.getPosition());
					}
				}
			}*/

		List<Ligne> LignesTrajet = new ArrayList<Ligne>();
		LignesTrajet = lignes;

		return LignesTrajet ;
	}

	private static void RemplirStations(List<Ligne> lignes, List<Station> stations){

		for(Ligne l :lignes) { //remplir les stations a partir du tableau stations (qu'on deja rempli a partir du Path p1)
			for(Trajet t: l.getTrajets()) {
				for(Station k: t.getArrets().keySet()) {
					for(Station a: stations) {
						if( ( a.getId().equals(k.getId()) ) && (k.getNom() == null) ) {// si c == et si on l'a pas deja rempli avant(car dans chaque trajet il peut y avoir des satations qui se repetent car ils n'ont pas le meme jour et donc des qu'on le rempli la premiere fois c bon)
							k.setNom(a.getNom());
							k.setPosition(a.getPosition());
							System.out.println(l.getNom()+"   "+k.getId()+"  "+k.getNom()+"    "+t.getArrets().get(k)+"    "+k.getPosition());
						}
					}
				}
			}
		}
	}
	public static List<Ligne> ChargerDonnees(String chemin) throws IOException {

		List<Station> stations = ChargerStations(chemin); // Recuperer les stations dans stations 
		System.out.println("Chargement des stations términée");

		for(Station s : stations) {
			System.out.println(s.getId()+"   "+s.getNom()+"  "+s.getPosition());
		}

		List<Ligne> lignes = ChargerLignes(chemin); // Recuperer les lignes dans lignes
		System.out.println("Chargement des lignes terminé");

		for(Ligne l : lignes) {
			System.out.println(l.getNom());
		}

		RemplirStations(lignes,stations);

		// FIN DE LA RECUPEARATION,   toutes les données sont dans 'lignes'

		System.out.println("Chargement de toutes les lignes terminé");
		System.out.println("Chargement effectué avec succes");
		return lignes;
	}

}