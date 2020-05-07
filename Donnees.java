package Transport;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Donnees {
	public Donnees() {
	}
	private List<Station> ChargerStations(String chemin) throws IOException{
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
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private List<Ligne> ChargerLignes(String chemin, int day) throws IOException {

		Transforme R = new Transforme();
		Map<String, List<String>> Routes = R.TabaleauRoutes(chemin);

		String name;
		int type_vehicule;	

		List<Ligne> lignes = new ArrayList<Ligne>(); 

		for(String id : Routes.keySet()) {
			name = Routes.get(id).get(0);
			type_vehicule = Integer.valueOf(Routes.get(id).get(1));
			lignes.add(new Ligne(id, name, type_vehicule ));
		}
		Routes.clear();

		////////leurs trajets:///////////////////////////////////////////////////////////////////////////////////////////////

		List<List<String>> Trips;
		Trips = R.TabaleauTrips(chemin);
		String Trajet_id;
		String Service_id;
		String Direction;


		for(int i=0; i<Trips.size(); i++) {
			for(Ligne l: lignes) {
				if( l.getId().equals(Trips.get(i).get(0))) {
					Trajet_id = Trips.get(i).get(2);
					Service_id= Trips.get(i).get(1);
					Direction = Trips.get(i).get(3);
					l.addTrajet(new Trajet(Trajet_id,Direction,new Calendrier(Service_id)));
				}
			}
		}

		System.out.println("Chargement des trajets terminé");

		////////Remplir les calendriers des trajets///////////////////////////////////////////////////////////////////////////////
		Map<String, List<String>> Calendrier ;
		Calendrier = R.TabaleauCalendrier(chemin);

		Service_id = null ;
		List<String> temp = new ArrayList<String>(); // pour recuperer la semaine de chaque service (dans Map)
		List<Integer> semaine = new ArrayList<Integer>() ; // utile pour la recup

		// On remplie les services de tout les trajets de toute les lignes:
		for(Ligne l: lignes) {
			for(Trajet t: l.getTrajets()) {

				Service_id = t.getCalendrier().getService_id() ;
				temp = Calendrier.get(Service_id);

				// on recupere les jours de la semaine:
				for(int i=0; i<temp.size(); i++)
					semaine.add(Integer.valueOf( temp.get(i)) );

				// On injecte la semaine dans le trajet:
				t.getCalendrier().setSemaine(semaine);
				semaine.clear(); // vider notre semaine pour en recuperer une autre
			}
		}

		System.out.println("Chargement des calendriers terminé");

		/////////récuperer les stations (et les creer en meme temps) et leurs horaires pour chaque trajet////////////////////////

		List<List<String>> StopTimes ;
		StopTimes = R.TabaleauStopTimes(chemin);


		String station_id;   // pour recuperer les stations une par une 
		Integer  h, min , sec;
		String[] horaire = null;

		int i=0 ;
		while(i<StopTimes.size()) {
			// on recupre le "Id" du trajet et on le cherche dans tt les trajts qu'on a dans StopTimes pour lui rajouter ses arrets:(station,horaire)
			for(Ligne l: lignes) {
				for(Trajet t: l.getTrajets()) {
					while( t.getId().equals(StopTimes.get(i).get(0)) && i<StopTimes.size()) {
						station_id = StopTimes.get(i).get(2) ;
						horaire = StopTimes.get(i).get(1).split(":"); //transformer le string(h:m:s) en h et m et s et en(int)
						h = Integer.valueOf(horaire[0]);
						min = Integer.valueOf(horaire[1]);
						sec = Integer.valueOf(horaire[2]);
						if(t.getCalendrier().getSemaine().get(day-1) == 1) {
							// on ajoute l'arret (Station/Horaire) au trajet "t"
							t.addArret(new Station(station_id), new Horaire(day, h, min, sec)); 
						}
						i++;
						//System.out.println(i);
						if(i == StopTimes.size())
							break;
					}
					if(i == StopTimes.size())
						break;
				}
				if(i == StopTimes.size())
					break;
			}
			if(i == StopTimes.size())
				break;
		}

		System.out.println("Chargement des arrets terminé");

		
		List<Ligne> LignesTrajet = new ArrayList<Ligne>();
		LignesTrajet = lignes;

		return LignesTrajet ;
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void RemplirStations(List<Ligne> lignes, List<Station> stations){

		for(Ligne l :lignes) { //remplir les stations a partir du tableau Stations (qu'on deja rempli a partir du Path p1)
			for(Trajet t: l.getTrajets()) {
				for(Station k: t.getArrets().keySet()) {
					for(Station a: stations) {
						if( ( a.getId().equals(k.getId()) ) && (k.getNom() == null) ) {// si c == et si on l'a pas deja rempli avant(car dans chaque trajet il peut y avoir des satations qui se repetent car ils n'ont pas le meme jour et donc des qu'on le rempli la premiere fois c bon)
							k.setNom(a.getNom());
							k.setPosition(a.getPosition());
							//System.out.println(l.getNom()+"   "+k.getId()+"  "+k.getNom()+"    "+t.getArrets().get(k)+"    "+k.getPosition());
						}
					}
				}
			}
		}
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public List<Ligne> ChargerDonnees(String chemin, int day) throws IOException {
		if(day < 1 || day > 7) //si le jour demandé n'est pas entre 1 et 7 alors on le considere comme jour ferier donc horaire de Dimanche 
			day = 7; 

		List<Station> stations = ChargerStations(chemin); // Recuperer les stations dans stations 
		System.out.println("Chargement des stations términée");

		List<Ligne> lignes = ChargerLignes(chemin,day); // Recuperer les lignes dans lignes
		System.out.println("Chargement des lignes terminé");

		for(Ligne l : lignes) {
			System.out.println(l.getNom());
		}

		// retourner que les trajets du jour demandé 'day' 
		List<Ligne> LignesFinale = new ArrayList<Ligne>();
		List<Trajet> trajetDay ;

		for(Ligne l : lignes) {
			trajetDay = new ArrayList<Trajet>();
			for(Trajet t: l.getTrajets()) {
				if(t.getCalendrier().getSemaine().get(day-1) == 1) 
					trajetDay.add(t);
			}
			LignesFinale.add(new Ligne(l.getId(), l.getNom(), l.getVehicule() , trajetDay)) ;
		}


		RemplirStations(LignesFinale,stations);

		// FIN DE LA RECUPEARATION,   toutes les données sont dans "LignesFinale"

		System.out.println("Chargement de toutes les lignes terminé");
		System.out.println("Chargement effectué avec succes");
		return LignesFinale;
	}

}



