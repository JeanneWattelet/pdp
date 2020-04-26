package Transport;


import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.ALTAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.*;


public class GrapheTrajet implements java.io.Serializable{
	private static final long serialVersionUID = -7760248029679113128L;
	private SimpleDirectedWeightedGraph<String, ArcTrajet> g;
	private List<Ligne> LignesTrajet;

	public GrapheTrajet(String chemin) throws IOException {
		g = new SimpleDirectedWeightedGraph<String, ArcTrajet>(ArcTrajet.class);
		chargerDonnees(chemin);
		ajouterSommets();
		ajouterAretesDeTransport();
		ajouterAretesAttente();
		g.addVertex("depart");
		g.addVertex("arrivee");
	}

	private String nommerSommet(Station s, Horaire h) {
		return s.toString()+"&"+h.toString();
	}

	private String denommer(String s) {
		if(s.contains("&")) {
			int i = s.indexOf('&');
			return s.substring(0, i);
		}
		return s;
	}

	private Horaire trouverHoraire(String s) {

		int j=0,h=0,m=0,sec=0;
		if(s.contains("&")) {
			String[] str = s.split("&");
			String[] horaire = str[1].split(":");
			j = Integer.valueOf(horaire[0]);
			h = Integer.valueOf(horaire[1]);
			m = Integer.valueOf(horaire[2]);
			sec = Integer.valueOf(horaire[3]);
		}
		return new Horaire(j, h, m, sec);
	}


	private void chargerDonnees(String chemin) throws IOException {
		////////Recuperer les stations dans un list<station>://////////

		Path p1 = Paths.get(chemin+"\\stop_Test.txt");
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

		System.out.println("Chargement des stations términée");

		/*for(Station s : stations) {
			System.out.println(s.getId()+"   "+s.getNom()+"  "+s.getPosition());
		}*/

		//////////////Recuperer les Lignes://///////////////////////////////////////////////////////////////////

		int type_vehicule;
		read = null ;
		Path p2 = Paths.get(chemin+"/ligne_test.txt");
		read = Files.newBufferedReader(p2);
		List<Ligne> lignes = new ArrayList<Ligne>();

		ligne = read.readLine();
		while( ( ligne = read.readLine() ) != null) {
			str = ligne.split(",") ;
			id = str[0];
			name = str[2];
			type_vehicule = Integer.valueOf(str[3]);
			lignes.add(new Ligne(id, name, type_vehicule ));
		}

		System.out.println("Chargement des routes terminé");

		/*for(Ligne l : lignes) {
			System.out.println(l.getNom());
		}*/

		////////leurs trajets:///////////////////////////////////////////////////////////////////////////////////////////////


		Path p3 = Paths.get(chemin+"/trip_test.txt");
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

		Path p5 = Paths.get(chemin+"/stp_h_test.txt");
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

		/////////remplir les stations a partir du tableau stations (qu'on deja rempli a partir du Path p1)///////////////////////

		for(Ligne l :lignes) {
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
		// FIN DE LA RECUPEARATION,   toutes les données sont dans 'lignes'
		this.LignesTrajet = lignes;

		System.out.println("Chargement de toutes les lignes terminé");
		System.out.println("Chargement effectué avec succes");
	}

	/////////////////////////////////////////////////////////// FIN //////////////////////////////////////////////////////////
	private void ajouterSommets() {
		String sommet;
		Horaire h;
		for(Ligne l: this.LignesTrajet) {//pour chaque ligne
			for(Trajet t: l.getTrajets()) {//pour chaque trajet 
				for(Station s: t.getArrets().keySet()) {//et pour chaque station dans la map(staion/horaire) de t
					h= t.getArrets().get(s); //l'horaire de la station s sur le trajet t
					sommet = nommerSommet(s, h);
					//sommet=s.toString()+h.toString();//on cree un sommet au nom unique selon son lieu et son horaire
					if(!this.g.containsVertex(sommet)) {
						this.g.addVertex(sommet);
					}
				}
			}
		}
		System.out.println("Création des sommets terminée");
	}

	private void ajouterAretesDeTransport(){
		String sommetA, sommetB;
		Horaire hA, hB;

		for(Ligne l: this.LignesTrajet) {//On regarde chaque ligne successivement
			for(Trajet t: l.getTrajets()) {//Pour chaque trajets de ces lignes
				for(Station stationA: t.getArrets().keySet()){//On regarde chaque station par lequel le trajet passe, qui pourraient Ãªtre une source d'un arc
					hA = t.getArrets().get(stationA);
					for(Station stationB: t.getArretsAfter(hA).keySet()) {//La station B est la cible de l'arc( 
						hB = t.getArrets().get(stationB);
						//le sens est bien le bon, car getArretAfter(hA) revoie que les arrets qui viennent apres stationA
						//sommetA = stationA.toString()+horaireA.toString();
						sommetA = nommerSommet(stationA, hA);
						//sommetB = stationB.toString()+horaireB.toString();
						sommetB = nommerSommet(stationB, hB);
						addWeightedEdge(sommetA, sommetB, hA.tempsEntre(hB), l.getVehicule());//ajout des sommets		
					}
				}
			}
		}
		System.out.println("Création des arcs de trajet terminée");
	}

	private void ajouterAretesAttente() {
		List<Trajet> trajets = new ArrayList<Trajet>();
		for(Ligne l: this.LignesTrajet) {
			trajets.addAll(l.getTrajets());
		}
		ajouterAretesAttenteTrajets(trajets);
		System.out.println("Création des arcs d'attente terminée");
	}

	private void ajouterAretesAttenteTrajets(List<Trajet> trajets) {
		String  sommetA, sommetB ;
		Horaire horaireA, horaireB ;

		for(Trajet trajetA: trajets){
			for(Station stationA: trajetA.getArrets().keySet()) {//les stations du trajetA
				for(Trajet trajetB: trajets) {
					for(Station stationB: trajetB.getArrets().keySet()) {
						if(stationA.equals(stationB)) { // == par leurs 'id'
							horaireA = trajetA.getArrets().get(stationA);
							horaireB = trajetB.getArrets().get(stationB);
							if(horaireA.estAvant(horaireB)) { //  horaireA.estAvant(horaireB) renvoie 'false' (donc il n y aura pas d'arc d'attente d'une station vers elle meme) 
								//sommetA = stationA.toString()+horaireA.toString();
								sommetA = nommerSommet(stationA, horaireA);
								//sommetB = stationB.toString()+horaireB.toString();
								sommetB = nommerSommet(stationB, horaireB);
								addWeightedEdge(sommetA, sommetB, horaireA.tempsEntre(horaireB), 5);
							}
						}
					}
				}
			}
		}
	}

	private void addWeightedEdge(String v1, String v2, int weight, int vehicule) {
		if(!v1.contains(v2)) {
			ArcTrajet arc = new ArcTrajet(vehicule, denommer(v1), denommer(v2));
			g.addEdge(v1, v2, arc);
			g.setEdgeWeight(v1, v2, weight);
		}
	}

	private void ajouterDepart(String from, Horaire h) {
		for(String vertex: g.vertexSet()) {
			if(denommer(vertex).equals(from)&&h.estAvant(trouverHoraire(vertex))) {
				addWeightedEdge("depart", vertex, 0, 0);
				System.out.println("Depart ajouté");
			}
		}
	}

	private void ajouterArrivee(String to) {
		for(String vertex: g.vertexSet()) {
			if(denommer(vertex).equals(to)) {
				addWeightedEdge(vertex, "arrivee", 0, 0);
				System.out.println("arrivée ajoutée");
			}
		}
	}

	private void retirerArcsDepartArrivee() {
		Set<ArcTrajet> dep = g.edgesOf("depart");
		Set<ArcTrajet> arr = g.edgesOf("arrivee");

		for(ArcTrajet arc: dep) {
			if(arc.getSourceT().equals("depart")) {
				g.removeEdge(arc); // supprimer tt les arcs qui ont "depart" comme Sommet source 
			}
		}

		for(ArcTrajet arc: arr) {
			if(arc.getTargetT().equals("arrivee")) {
				g.removeEdge(arc); // supprimer tt les arcs qui ont "arrivee" comme Sommet cible(target) 
			}
		}
	}

	public List<ArcTrajet> astar(String from, String to, Horaire h) {

		AStarShortestPath<String, ArcTrajet> astar = new AStarShortestPath<String, ArcTrajet>(this.g, new ALTAdmissibleHeuristic<String, ArcTrajet>(g,g.vertexSet()));

		ajouterDepart(from, h);
		ajouterArrivee(to);

		System.out.println(g.toString());

		GraphPath<String, ArcTrajet> itineraire = astar.getPath("depart", "arrivee");

		System.out.println("/////////////////////// A* ////////////////////////////");
		System.out.println("Shortest Path : "+itineraire.getEdgeList());
		System.out.println("Weight of this path : "+AfficherTempsDeTrajet(itineraire.getWeight()));
		System.out.println("Number of means of transportation used : "+(itineraire.getLength()-2));
		System.out.println("////////////////////////////A* FIN///////////////////////////");

		retirerArcsDepartArrivee();

		return itineraire.getEdgeList();
	}

	public List<ArcTrajet> dijkstra(String from, String to, Horaire h) {
		DijkstraShortestPath<String, ArcTrajet> dijkstra = new DijkstraShortestPath<String, ArcTrajet>(this.g);

		ajouterDepart(from, h);
		ajouterArrivee(to);

		GraphPath<String, ArcTrajet> itineraire = dijkstra.getPath("depart", "arrivee");

		System.out.println("/////////////////////// Dikjstra ////////////////////////////");

		System.out.println("Shortest Path : "+itineraire.getEdgeList());
		System.out.println("Weight of this path : "+AfficherTempsDeTrajet(itineraire.getWeight()));
		System.out.println("Number of means of transportation used : "+(itineraire.getLength()-2));

		System.out.println("/////////////////////// DIKJSTRA FIN////////////////////////////");

		retirerArcsDepartArrivee();

		return itineraire.getEdgeList();
	}

	private String AfficherTempsDeTrajet(double duree) { // afficher la durée du trajet en mode "normal" 
		if( (int)(duree/3600) == 0 ) {
			if( (int) (duree/60) == 0 ) {
				return duree+"(seconde)";
			}else {
				return (int)(duree/60)+"(minute)"+(duree%60)+"seconde";
			}
		}else {
			return (int)(duree/3600)+"(heure)"+AfficherTempsDeTrajet(duree%3600);
		}
	}

	@Override
	public String toString() {
		return g.toString();
	}

	@Override
	public int hashCode() {
		return g.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==this)
			return true;
		if(obj.getClass()!=this.getClass())
			return false;
		GrapheTrajet o = (GrapheTrajet) obj;
		if(o.g!=this.g)
			return false;
		return true;
	}

}
