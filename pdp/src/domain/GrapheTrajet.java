package domain;

import transport.*;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.ALTAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class GrapheTrajet implements java.io.Serializable{
	private static final long serialVersionUID = -7760248029679113128L;
	private SimpleDirectedWeightedGraph<String, ArcTrajet> g;
	
	private static int PENALITE = 10;
	
	public GrapheTrajet(Set<Ligne> set) {
		g = new SimpleDirectedWeightedGraph<String, ArcTrajet>(ArcTrajet.class);
		ajouterSommets(set);
		ajouterAretesDeTransport(set);
		ajouterAretesAttente(set);
		g.addVertex("depart");
		g.addVertex("arrivee");
	}
	
	public GrapheTrajet() {
		g = new SimpleDirectedWeightedGraph<String, ArcTrajet>(ArcTrajet.class);
	}
	
	/*
	 * Création d'un nouveau graphe à partir d'un ensemble d'entités du package "Transport"
	 * (Cette partie est utile avant la sérialisation du graphe)
	 * (Une fois que celui-ci est créé, ces fonctions ne sont plus utiles pour la résolution)
	 */
	
	public void ajouterSommets(Set<Ligne> lignes) {
		String sommet;
		Set<Trajet> setTrajet;
		Ligne l;
		Trajet t;
		Station s;
		Iterator<Ligne> iterLignes = lignes.iterator();
		Horaire h;
		while(iterLignes.hasNext()) {//pour chaque ligne
			l=iterLignes.next();
			setTrajet = l.getTrajets();
			Iterator<Trajet> iterTrajets = setTrajet.iterator();
			while(iterTrajets.hasNext()) {//pour chaque trajet
				t=iterTrajets.next();
				Map<Station, Horaire> arrets = t.getArrets();
				Set<Station> stations = arrets.keySet();
				Iterator<Station> iterStations = stations.iterator();
				while(iterStations.hasNext()) {//et pour chaque station
					s=iterStations.next();
					h=arrets.get(s);
					sommet = nommerSommet(s, h);//creation d'un nom unique
					if(!this.g.containsVertex(sommet)) {
						this.g.addVertex(sommet);
					}
				}
			}
		}
	}

	private void ajouterAretesDeTransport(Set<Ligne> lignes){
		String sommetA, sommetB;
		Ligne l;
		Set<Trajet> setTrajet;
		Trajet t;
		Station stationA, stationB;
		Iterator<Ligne> iterLignes = lignes.iterator();
		Horaire hA, hB;
		
		while(iterLignes.hasNext()) {//On regarde chaque ligne successivement
			l=iterLignes.next();
			setTrajet=l.getTrajets();
			Iterator<Trajet> iterTrajets = setTrajet.iterator();
			while(iterTrajets.hasNext()) {//Pour chaque trajets de ces lignes
				t=iterTrajets.next();
				Map<Station, Horaire> arrets = t.getArrets();
				Set<Station> stations = arrets.keySet();
				Iterator<Station> iterStationsA = stations.iterator();
				while(iterStationsA.hasNext()) {//On regarde chaque station par lequel le trajet passe, qui pourraient Ãªtre une source d'un arc
					stationA = iterStationsA.next();
					hA = arrets.get(stationA);
					Iterator<Station> iterStationsB = stations.iterator();//La station B est la cible de l'arc
					while(iterStationsB.hasNext()) {//
						stationB = iterStationsB.next();
						hB = arrets.get(stationB);
						if(hA.estAvant(hB)){//Si le sens est bien le bon, selon l'horaire
							//sommetA = stationA.toString()+horaireA.toString();
							sommetA = nommerSommet(stationA, hA);
							//sommetB = stationB.toString()+horaireB.toString();
							sommetB = nommerSommet(stationB, hB);
							addWeightedEdge(sommetA, sommetB, hA.tempsEntre(hB), l.getVehicule(), l.getNom());//ajout des sommets
						}
					}
				}
			}
		}
	}
	
	private void ajouterAretesAttente(Set<Ligne> lignes) {
		Set<Trajet> trajets = new HashSet<Trajet>();
		Iterator<Ligne> iterLigne = lignes.iterator();
		Ligne ligne;
		while(iterLigne.hasNext()) {
			ligne = iterLigne.next();
			trajets.addAll(ligne.getTrajets());
		}
		ajouterAretesAttenteTrajets(trajets);
	}
	
	private void ajouterAretesAttenteTrajets(Set<Trajet> trajets) {
		String sommetA, sommetB;
		Trajet trajetA, trajetB;
		Horaire horaireA, horaireB;
		Station stationA, stationB;
		Iterator<Trajet> iterTrajetA = trajets.iterator();
		while(iterTrajetA.hasNext()) {
			trajetA=iterTrajetA.next();
			Map<Station, Horaire> arretsA = trajetA.getArrets();
			Set<Station> stationsA = arretsA.keySet();
			Iterator<Station> a = stationsA.iterator();
			while(a.hasNext()) {
				stationA = a.next();
				Iterator<Trajet> iterTrajetB = trajets.iterator();
				while(iterTrajetB.hasNext()) {
					trajetB = iterTrajetB.next();
					Map<Station, Horaire> arretsB = trajetB.getArrets();
					Set<Station> stationsB = arretsB.keySet();
					Iterator<Station> iterStationB = stationsB.iterator();
					while(iterStationB.hasNext()) {
						stationB = iterStationB.next();
						if(stationA==stationB) {
							horaireA = arretsA.get(stationA);
							horaireB = arretsB.get(stationB);
							if(horaireA.estAvant(horaireB)) {
								//sommetA = stationA.toString()+horaireA.toString();
								sommetA = nommerSommet(stationA, horaireA);
								//sommetB = stationB.toString()+horaireB.toString();
								sommetB = nommerSommet(stationB, horaireB);
								addWeightedEdge(sommetA, sommetB, horaireA.tempsEntre(horaireB), Ligne.ATTENTE, "attente");
							}
						}
					}
				}
			}
		}
	}
	
	/*
	 * Gestion des noms des sommets
	 */
	
	public String denommer(String s) {
		if(s.contains("&")) {
			int i = s.indexOf('&');
			return s.substring(0, i);
		}
		return s;
	}
	
	public Horaire trouverHoraire(String s) {//fonction tres probablement ameliorable...
		String d=Horaire.FERIER;
		int h=0, m=0;
		if(s.contains("&")) {
			
			int i = s.indexOf('&')+1;
			int j = s.indexOf(' ', i)+1;
			d = s.substring(i, j-1);
			
			i = s.indexOf(':', j)+1;
			h=Integer.parseInt(s.substring(j, i-1));
			
			j = s.length();
			m=Integer.parseInt(s.substring(i, j));
			
		}
		return new Horaire(d, h, m);
	}
	
	public String nommerSommet(Station s, Horaire h) {
		return s.toString()+"&"+h.toString();
	}
	
	
	/*
	 * Fonction d'ajout d'un arc valué.
	 * Utile à la fois à la création du graphe et lors de la résolution du plus court chemin
	 */
	
	public void addWeightedEdge(String v1, String v2, int weight, String vehicule, String nom) {
		if(!v1.contains(v2)) {
			ArcTrajet arc = new ArcTrajet(vehicule, denommer(v1), denommer(v2), nom);
			g.addEdge(v1, v2, arc);
			g.setEdgeWeight(v1, v2, weight);
		}
	}
	
	public void ajouterDepart(String from, Horaire h, GrapheTrajet gr) {
		Iterator<String> i = gr.g.vertexSet().iterator();
		String vertex;
		while(i.hasNext()) {
			vertex = i.next();
			if(denommer(vertex).equals(from)&&h.estAvant(trouverHoraire(vertex))) {
				addWeightedEdge("depart", vertex, 0, Ligne.ATTENTE, "attente");
			}
		}
	}
	
	public void ajouterArrivee(String to, GrapheTrajet gr) {
		Iterator<String> i = gr.g.vertexSet().iterator();
		String vertex;
		while(i.hasNext()) {
			vertex = i.next();
			if(denommer(vertex).equals(to)) {
				addWeightedEdge(vertex, "arrivee", 0, Ligne.ATTENTE, "attente");
			}
		}
	}

	public void retirerArcsDepartArrivee(GrapheTrajet gr) {
		Set<ArcTrajet> dep = gr.g.edgesOf("depart");
		Set<ArcTrajet> arr = gr.g.edgesOf("arrivee");
		
		Iterator<ArcTrajet> i = dep.iterator();
		Iterator<ArcTrajet> j = arr.iterator();
		
		ArcTrajet arc;
		
		while(i.hasNext()) {
			arc = i.next();
			if(arc.getSourceT().equals("depart")) {
				gr.g.removeEdge(arc);
			}
		}
		
		while(j.hasNext()) {
			arc = j.next();
			if(arc.getTargetT().equals("arrivee")) {
				gr.g.removeEdge(arc);
			}
		}
	}
	
	/*
	 * Algos de résolution (A* et Dijkstra
	 */
	
	
public List<ArcTrajet> astar(String from, String to, Horaire h) {
		
		GrapheTrajet gr = filtrerGraphe();
		
		ajouterDepart(from, h, gr);
		ajouterArrivee(to, gr);
		
		AStarShortestPath<String, ArcTrajet> astar = new AStarShortestPath<String, ArcTrajet>(gr.g, new ALTAdmissibleHeuristic<String, ArcTrajet>(gr.g,gr.g.vertexSet()));
		
		GraphPath<String, ArcTrajet> itineraire = astar.getPath("depart", "arrivee");
        
        retirerArcsDepartArrivee(gr);
        try {
        	System.out.println("Shortest Path : "+itineraire.getEdgeList());
            System.out.println("Weight of this path : "+itineraire.getWeight());
            System.out.println("Number of means of transportation used : "+(itineraire.getLength()-2));
        	return itineraire.getEdgeList();
        }catch(java.lang.NullPointerException e){
        	System.out.println("Aucun itineraire correspondant.");
        	ArrayList<ArcTrajet> liste = new ArrayList<ArcTrajet>();
        	return liste;
        }
	}
	
	public List<ArcTrajet> dijkstra(String from, String to, Horaire h) {
		GrapheTrajet gr = filtrerGraphe();
		
		ajouterDepart(from, h, gr);
		ajouterArrivee(to, gr);
		
		DijkstraShortestPath<String, ArcTrajet> dijkstra = new DijkstraShortestPath<String, ArcTrajet>(gr.g);
		
		GraphPath<String, ArcTrajet> itineraire = dijkstra.getPath("depart", "arrivee");
		
        retirerArcsDepartArrivee(gr);
        try {
        	System.out.println("Shortest Path : "+itineraire.getEdgeList());
            System.out.println("Weight of this path : "+itineraire.getWeight());
            System.out.println("Number of means of transportation used : "+(itineraire.getLength()-2));
        	return itineraire.getEdgeList();
        }catch(java.lang.NullPointerException e){
        	System.out.println("Aucun itineraire correspondant.");
        	ArrayList<ArcTrajet> liste = new ArrayList<ArcTrajet>();
        	return liste;
        }
	}
	
	/*
	 * Fonctionnalitées suppélementaires : Arriver A
	 */
	
	public void ajouterDepartArriverA(String from, GrapheTrajet gr) {
		Iterator<String> i = gr.g.vertexSet().iterator();
		String vertex;
		while(i.hasNext()) {
			vertex = i.next();
			if(denommer(vertex).equals(from)) {
				addWeightedEdge("depart", vertex, 0, Ligne.ATTENTE, "attente");
			}
		}
	}
	
	public void ajouterArriveeArriverA(String to, Horaire h, GrapheTrajet gr) {
		Iterator<String> i = gr.g.vertexSet().iterator();
		String vertex;
		while(i.hasNext()) {
			vertex = i.next();
			if(denommer(vertex).equals(to)&&trouverHoraire(vertex).estAvant(h)) {
				addWeightedEdge(vertex, "arrivee", 0, Ligne.ATTENTE, "attente");

			}
		}
	}
	
	public List<ArcTrajet> dijkstraArriverA(String from, String to, Horaire h) {
		GrapheTrajet gr = filtrerGraphe();
		
		ajouterDepartArriverA(from, gr);
		ajouterArriveeArriverA(to, h, gr);
		
		DijkstraShortestPath<String, ArcTrajet> dijkstra = new DijkstraShortestPath<String, ArcTrajet>(gr.g);
		
		GraphPath<String, ArcTrajet> itineraire = dijkstra.getPath("depart", "arrivee");
		
        retirerArcsDepartArrivee(gr);
        
        try {
        	System.out.println("Shortest Path : "+itineraire.getEdgeList());
            System.out.println("Weight of this path : "+itineraire.getWeight());
            System.out.println("Number of means of transportation used : "+(itineraire.getLength()-2));
        	return itineraire.getEdgeList();
        }catch(java.lang.NullPointerException e){
        	System.out.println("Aucun itineraire correspondant.");
        	ArrayList<ArcTrajet> liste = new ArrayList<ArcTrajet>();
        	return liste;
        }
	}
	
public List<ArcTrajet> astarArriverA(String from, String to, Horaire h) {
		
		GrapheTrajet gr = filtrerGraphe();
		
		ajouterDepartArriverA(from, gr);
		ajouterArriveeArriverA(to, h, gr);
		
		AStarShortestPath<String, ArcTrajet> astar = new AStarShortestPath<String, ArcTrajet>(gr.g, new ALTAdmissibleHeuristic<String, ArcTrajet>(gr.g,gr.g.vertexSet()));
		
		GraphPath<String, ArcTrajet> itineraire = astar.getPath("depart", "arrivee");
        
        retirerArcsDepartArrivee(gr);
        try {
        	System.out.println("Shortest Path : "+itineraire.getEdgeList());
            System.out.println("Weight of this path : "+itineraire.getWeight());
            System.out.println("Number of means of transportation used : "+(itineraire.getLength()-2));
        	return itineraire.getEdgeList();
        }catch(java.lang.NullPointerException e){
        	System.out.println("Aucun itineraire correspondant.");
        	ArrayList<ArcTrajet> liste = new ArrayList<ArcTrajet>();
        	return liste;
        }
	}

	/*
	 * Fonctionnalitées suppélementaires : éviter tel trajet
	 */

	GrapheTrajet filtrerGraphe() {
		return filtrerGraphe(this);
	}
	
	GrapheTrajet filtrerGraphe(String p) {
		return filtrerGraphe(p, this);
	}

	GrapheTrajet filtrerGraphe(GrapheTrajet g) {
		GrapheTrajet h = new GrapheTrajet();
		String line;
		HashSet<String> tabouLigne = new HashSet<String>();
		HashSet<String> tabouTransport = new HashSet<String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader("src/saves/perturbations.txt"));
			while(in.ready()) {
				line = in.readLine();
				switch(line){
				case "Tram" :
					tabouTransport.add(Ligne.TRAM);
					break;
				case "Marche" :
					tabouTransport.add(Ligne.PIED);
					break;
				case "Bus" :
					tabouTransport.add(Ligne.BUS);
					break;
				case "Attente" :
					tabouTransport.add(Ligne.ATTENTE);
					break;	
				case "Metro" :
					tabouTransport.add(Ligne.METRO);
					break;
				case "Bateau" :
					tabouTransport.add(Ligne.BATEAU);
					break;
				default :
					tabouLigne.add(line);
				}
				
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(tabouLigne.isEmpty()&&tabouTransport.isEmpty()) {
			return this;
		}
		Set<String> sommets = g.g.vertexSet();
		Iterator<String> iterSommets = sommets.iterator();
		String s;
		while(iterSommets.hasNext()) {
			s = iterSommets.next();
			h.g.addVertex(s);
		}
		Iterator<ArcTrajet> iterArcs = g.g.edgeSet().iterator();
		ArcTrajet arc;
		while(iterArcs.hasNext()) {
			arc = iterArcs.next();
			if(!tabouTransport.contains(arc.getTransport())&&!tabouLigne.contains(arc.getNom())) {
				h.addWeightedEdge((String)arc.getSourceT(), (String)arc.getTargetT(), (int)arc.getWeightT(), arc.getTransport(), arc.getNom());
				//System.out.println((String)arc.getSourceT()+" "+(String)arc.getTargetT()+" "+(int)arc.getWeightT()+" "+arc.getTransport()+" "+arc.getNom());
			}
		}
		return h;
	}
	
	GrapheTrajet filtrerGraphe(String penalite, GrapheTrajet g) {
		GrapheTrajet h = new GrapheTrajet();
		String line;
		HashSet<String> tabouLigne = new HashSet<String>();
		HashSet<String> tabouTransport = new HashSet<String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader("FICHIER.txt"));
			while(in.ready()) {
				line = in.readLine();
				switch(line){
				case "Tram" :
					tabouTransport.add(Ligne.TRAM);
					break;
				case "Marche" :
					tabouTransport.add(Ligne.PIED);
					break;
				case "Bus" :
					tabouTransport.add(Ligne.BUS);
					break;
				case "Attente" :
					tabouTransport.add(Ligne.ATTENTE);
					break;	
				case "Metro" :
					tabouTransport.add(Ligne.METRO);
					break;
				case "Bateau" :
					tabouTransport.add(Ligne.BATEAU);
					break;
				default :
					tabouLigne.add(line);
				}
				
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(tabouLigne.isEmpty()&&tabouTransport.isEmpty()) {
			return this;
		}
		Set<String> sommets = g.g.vertexSet();
		Iterator<String> iterSommets = sommets.iterator();
		String s;
		while(iterSommets.hasNext()) {
			s = iterSommets.next();
			h.g.addVertex(s);
		}
		Iterator<ArcTrajet> iterArcs = g.g.edgeSet().iterator();
		ArcTrajet arc;
		while(iterArcs.hasNext()) {
			arc = iterArcs.next();
			if(!tabouTransport.contains(arc.getTransport())&&!tabouLigne.contains(arc.getNom())) {
				if(arc.getTransport().equals(penalite))
					h.addWeightedEdge((String)arc.getSourceT(), (String)arc.getTargetT(), (int)arc.getWeightT()+PENALITE, arc.getTransport(), arc.getNom());
				else
					h.addWeightedEdge((String)arc.getSourceT(), (String)arc.getTargetT(), (int)arc.getWeightT(), arc.getTransport(), arc.getNom());
			}
		}
		return h;
	}
	
	/*
	 * Fonctionnalité supplémentaire : pénaliser un moyen de transport
	 */
	
	public List<ArcTrajet> astarPenalisant(String from, String to, Horaire h, String penalite) {

		GrapheTrajet gr = filtrerGraphe(penalite);
		
		ajouterDepart(from, h, gr);
		ajouterArrivee(to, gr);
		
		AStarShortestPath<String, ArcTrajet> astar = new AStarShortestPath<String, ArcTrajet>(gr.g, new ALTAdmissibleHeuristic<String, ArcTrajet>(gr.g,gr.g.vertexSet()));
		
		GraphPath<String, ArcTrajet> itineraire = astar.getPath("depart", "arrivee");
        
        retirerArcsDepartArrivee(gr);
        try {
        	System.out.println("Shortest Path : "+itineraire.getEdgeList());
            System.out.println("Weight of this path : "+itineraire.getWeight());
            System.out.println("Number of means of transportation used : "+(itineraire.getLength()-2));
        	return itineraire.getEdgeList();
        }catch(java.lang.NullPointerException e){
        	System.out.println("Aucun itineraire correspondant.");
        	ArrayList<ArcTrajet> liste = new ArrayList<ArcTrajet>();
        	return liste;
        }
	}
	
	public List<ArcTrajet> dijkstraPenalisant(String from, String to, Horaire h, String penalite) {
		GrapheTrajet gr = filtrerGraphe(penalite);
		
		ajouterDepart(from, h, gr);
		ajouterArrivee(to, gr);
		
		DijkstraShortestPath<String, ArcTrajet> dijkstra = new DijkstraShortestPath<String, ArcTrajet>(gr.g);
		
		GraphPath<String, ArcTrajet> itineraire = dijkstra.getPath("depart", "arrivee");
		
        retirerArcsDepartArrivee(gr);
        try {
        	System.out.println("Shortest Path : "+itineraire.getEdgeList());
            System.out.println("Weight of this path : "+itineraire.getWeight());
            System.out.println("Number of means of transportation used : "+(itineraire.getLength()-2));
        	return itineraire.getEdgeList();
        }catch(java.lang.NullPointerException e){
        	System.out.println("Aucun itineraire correspondant.");
        	ArrayList<ArcTrajet> liste = new ArrayList<ArcTrajet>();
        	return liste;
        }
	}
	
	public List<ArcTrajet> dijkstraArriverAPenalisant(String from, String to, Horaire h, String p) {
		GrapheTrajet gr = filtrerGraphe(p);
		
		ajouterDepartArriverA(from, gr);
		ajouterArriveeArriverA(to, h, gr);
		
		DijkstraShortestPath<String, ArcTrajet> dijkstra = new DijkstraShortestPath<String, ArcTrajet>(gr.g);
		
		GraphPath<String, ArcTrajet> itineraire = dijkstra.getPath("depart", "arrivee");
		
        retirerArcsDepartArrivee(gr);
        
        try {
        	System.out.println("Shortest Path : "+itineraire.getEdgeList());
            System.out.println("Weight of this path : "+itineraire.getWeight());
            System.out.println("Number of means of transportation used : "+(itineraire.getLength()-2));
        	return itineraire.getEdgeList();
        }catch(java.lang.NullPointerException e){
        	System.out.println("Aucun itineraire correspondant.");
        	ArrayList<ArcTrajet> liste = new ArrayList<ArcTrajet>();
        	return liste;
        }
	}
	
public List<ArcTrajet> astarArriverAPenalisant(String from, String to, Horaire h, String p) {
		
		GrapheTrajet gr = filtrerGraphe(p);
		
		ajouterDepartArriverA(from, gr);
		ajouterArriveeArriverA(to, h, gr);
		
		AStarShortestPath<String, ArcTrajet> astar = new AStarShortestPath<String, ArcTrajet>(gr.g, new ALTAdmissibleHeuristic<String, ArcTrajet>(gr.g,gr.g.vertexSet()));
		
		GraphPath<String, ArcTrajet> itineraire = astar.getPath("depart", "arrivee");
        
        retirerArcsDepartArrivee(gr);
        try {
        	System.out.println("Shortest Path : "+itineraire.getEdgeList());
            System.out.println("Weight of this path : "+itineraire.getWeight());
            System.out.println("Number of means of transportation used : "+(itineraire.getLength()-2));
        	return itineraire.getEdgeList();
        }catch(java.lang.NullPointerException e){
        	System.out.println("Aucun itineraire correspondant.");
        	ArrayList<ArcTrajet> liste = new ArrayList<ArcTrajet>();
        	return liste;
        }
	}
	/*
	 * Fonctions de base d'une classe
	 */
	
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
