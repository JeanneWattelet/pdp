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
	
	private static double PENALITE = 2;
	
	
	
	public GrapheTrajet(List<Ligne> LignesTrajet) throws IOException {
		g = new SimpleDirectedWeightedGraph<String, ArcTrajet>(ArcTrajet.class);
		ajouterSommets(LignesTrajet);
		ajouterAretesDeTransport(LignesTrajet);
		ajouterAretesAttente(LignesTrajet);
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
	
	private void ajouterSommets(List<Ligne> LignesTrajet) {
		String sommet;
		Horaire h;
		for(Ligne l: LignesTrajet) {//pour chaque ligne
			for(Trajet t: l.getTrajets()) {//pour chaque trajet 
				for(Station s: t.getArrets().keySet()) {//et pour chaque station dans la map(staion/horaire) de t
					h= t.getArrets().get(s); //l'horaire de la station s sur le trajet t
					sommet = nommerSommet(s, h);
					//sommet=s.toString()+h.toString();//on cree un sommet au nom unique selon son lieu et son horaire
					if(!this.g.containsVertex(sommet)) {
						this.g.addVertex(sommet);
						System.out.println(sommet);
					}
				}
			}
		}
		System.out.println("Création des sommets terminée");
	}
	
	
	private void ajouterAretesDeTransport(List<Ligne> LignesTrajet){
		String sommetA, sommetB;
		Horaire hA, hB;
		Map<Station,Horaire> tmp = new HashMap<Station,Horaire>();
		List<Trajet> tr = new ArrayList<Trajet>();
		Map<Station,Horaire> mp = new HashMap<Station,Horaire>();

		for(Ligne l: LignesTrajet) {//On regarde chaque ligne successivement
			tr = l.getTrajets();
			for(Trajet t: tr) {//Pour chaque trajets de ces lignes
				tmp = t.getArrets();//////
				for(Station stationA: tmp.keySet()){//On regarde chaque station par lequel le trajet passe, qui pourraient Ãªtre une source d'un arc
					hA = tmp.get(stationA);
					mp = t.getArretsAfter(hA);/////
					for(Station stationB: mp.keySet()) {//La station B est la cible de l'arc( 
						hB = tmp.get(stationB);
						//le sens est bien le bon, car getArretAfter(hA) revoie que les arrets qui viennent apres stationA
						//sommetA = stationA.toString()+horaireA.toString();
						sommetA = nommerSommet(stationA, hA);
						//sommetB = stationB.toString()+horaireB.toString();
						sommetB = nommerSommet(stationB, hB);
						addWeightedEdge(sommetA, sommetB, hA.tempsEntre(hB), l.getVehicule(), l.getNom());//ajout des sommets		
					}
				}
			}
		}
		System.out.println("Création des arcs de trajet terminée");
	}
	
	private void ajouterAretesAttente(List<Ligne> LignesTrajet) {
		/*List<Trajet> trajets = new ArrayList<Trajet>();
		for(Ligne l: LignesTrajet) {
			trajets.addAll(l.getTrajets());
		}
		ajouterAretesAttenteTrajets(trajets);*/
		ajouterAretesAttenteTrajets();
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
								addWeightedEdge(sommetA, sommetB, horaireA.tempsEntre(horaireB), Ligne.ATTENTE, "attente");
								System.out.println(sommetA+" to "+sommetB);
							}
						}
					}
				}
			}
		}
	}
	
	private void ajouterAretesAttenteTrajets() {
		String  stationA, stationB, sommetA, sommetB ;
		Horaire horaireA, horaireB ;

		Set<String> sommets = g.vertexSet();
		Iterator<String> iterSommetA = sommets.iterator();
			
		ArcTrajet arc = new ArcTrajet("Pied", "Depart", "Arrivee", "Nom"), tmp;
		
		double best;
		
		while(iterSommetA.hasNext()) {
			sommetA = iterSommetA.next();
			stationA = denommer(sommetA);
			horaireA = trouverHoraire(sommetA);
			Iterator<String> iterSommetB = sommets.iterator();
			best = 0;
			while(iterSommetB.hasNext()) {
				sommetB = iterSommetB.next();
				stationB = denommer(sommetB);
				horaireB = trouverHoraire(sommetB);
				if(!(sommetA.contentEquals(sommetB))&&stationA.contentEquals(stationB)&&horaireA.estAvant(horaireB)) {
					if((best==0)||(best>horaireA.tempsEntre(horaireB))) {
						sommetA = nommerSommet(stationA, horaireA);
						sommetB = nommerSommet(stationB, horaireB);
						tmp = addWeightedEdge(sommetA, sommetB, horaireA.tempsEntre(horaireB), Ligne.ATTENTE, "attente");
						System.out.println(sommetA+" to "+sommetB);
						if(best!=0) {
							g.removeEdge(arc);
						}
						arc=tmp;
						best = arc.getWeightT();
					}
				}
			}
		}
		if(g.containsEdge("depart", "arrivee")) {
			g.removeEdge("depart", "arrivee");
		}
	}
	
	
	/*
	 * Gestion des noms des sommets
	 */
	
	public String denommer(String s) {
		if(s.contains("%")) {
			int i = s.indexOf('%');
			return s.substring(0, i);
		}
		return s;
	}
	
	public Horaire trouverHoraire(String s) {

		int j=0,h=0,m=0,sec=0;
		if(s.contains("%")) {
			String[] str = s.split("%");
			String[] horaire = str[1].split(":");
			j = Integer.valueOf(horaire[0]);
			h = Integer.valueOf(horaire[1]);
			m = Integer.valueOf(horaire[2]);
			sec = Integer.valueOf(horaire[3]);
		}
		return new Horaire(j, h, m, sec);
	}
	
	public String nommerSommet(Station s, Horaire h) {
		return s.toString()+"%"+h.toString();
	}
	
	public String nommerSommet(String s, Horaire h) {
		return s+"%"+h.toString();
	}
	
	/*
	 * Fonction d'ajout d'un arc valué.
	 * Utile à la fois à la création du graphe et lors de la résolution du plus court chemin
	 */
	
	private ArcTrajet addWeightedEdge(String v1, String v2, double weight, String vehicule, String nom) {
		ArcTrajet arc = new ArcTrajet(vehicule, denommer(v1), denommer(v2), nom);
		if(!v1.contains(v2)) {
			g.addEdge(v1, v2, arc);
			g.setEdgeWeight(v1, v2, weight);
		}
		return arc;
	}
	
	private void ajouterDepart(String from, Horaire h, GrapheTrajet gr) {
		Iterator<String> i = gr.g.vertexSet().iterator();
		String vertex;
		while(i.hasNext()) {
			vertex = i.next();
			if(denommer(vertex).equals(from)&&h.estAvant(trouverHoraire(vertex))) {
				addWeightedEdge("depart", vertex, 0, Ligne.ATTENTE, "attente");
			}
		}
	}
	
	private void ajouterArrivee(String to, GrapheTrajet gr) {
		Iterator<String> i = gr.g.vertexSet().iterator();
		String vertex;
		while(i.hasNext()) {
			vertex = i.next();
			if(denommer(vertex).equals(to)) {
				addWeightedEdge(vertex, "arrivee", 0, Ligne.ATTENTE, "attente");
			}
		}
	}

	private void retirerArcsDepartArrivee(GrapheTrajet gr) {
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
	
	private void ajouterDepartArriverA(String from, GrapheTrajet gr) {
		Iterator<String> i = gr.g.vertexSet().iterator();
		String vertex;
		while(i.hasNext()) {
			vertex = i.next();
			if(denommer(vertex).equals(from)) {
				addWeightedEdge("depart", vertex, 0, Ligne.ATTENTE, "attente");
			}
		}
	}
	
	private void ajouterArriveeArriverA(String to, Horaire h, GrapheTrajet gr) {
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

	private GrapheTrajet filtrerGraphe() {
		return filtrerGraphe(this);
	}
	
	private GrapheTrajet filtrerGraphe(String p) {
		return filtrerGraphe(p, this);
	}

	private GrapheTrajet filtrerGraphe(GrapheTrajet g) {
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
				h.addWeightedEdge((String)arc.getSourceT(), (String)arc.getTargetT(), (int)arc.getWeightT(), arc.getTransport(), arc.getNom());
				//System.out.println((String)arc.getSourceT()+" "+(String)arc.getTargetT()+" "+(int)arc.getWeightT()+" "+arc.getTransport()+" "+arc.getNom());
			}
		}
		return h;
	}
	
	private GrapheTrajet filtrerGraphe(String penalite, GrapheTrajet g) {
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
				if(arc.getTransport().equals(penalite))
					h.addWeightedEdge((String)arc.getSourceT(), (String)arc.getTargetT(), (int)arc.getWeightT()*PENALITE, arc.getTransport(), arc.getNom());
				else
					h.addWeightedEdge((String)arc.getSourceT(), (String)arc.getTargetT(), (int)arc.getWeightT(), arc.getTransport(), arc.getNom());
			}
		}
		return h;
	}
	
	/*
	 * Fonctionnalité supplémentaire : pénaliser un moyen de transport
	 */
	
	private List<ArcTrajet> depenaliser(GrapheTrajet gr, List<ArcTrajet> l, String p){
		ArrayList<ArcTrajet> liste = new ArrayList<ArcTrajet>();
		for(ArcTrajet arc : l) {
			if(arc.getTransport()==p) {
				gr.g.setEdgeWeight(arc, arc.getWeightT()/PENALITE);
			}
			liste.add(arc);
		}
		return liste;
	}
	
	public List<ArcTrajet> astarPenalisant(String from, String to, Horaire h, String penalite) {

		GrapheTrajet gr = filtrerGraphe(penalite);
		
		ajouterDepart(from, h, gr);
		ajouterArrivee(to, gr);
		
		AStarShortestPath<String, ArcTrajet> astar = new AStarShortestPath<String, ArcTrajet>(gr.g, new ALTAdmissibleHeuristic<String, ArcTrajet>(gr.g,gr.g.vertexSet()));
		
		GraphPath<String, ArcTrajet> itineraire = astar.getPath("depart", "arrivee");
        
        retirerArcsDepartArrivee(gr);
        
        List<ArcTrajet> liste;
        
        try {
        	liste = itineraire.getEdgeList();
        	liste = depenaliser(gr, liste, penalite);
        	System.out.println("Shortest Path : "+liste);
        	return liste;
        }catch(java.lang.NullPointerException e){
        	System.out.println("Aucun itineraire correspondant.");
        	liste = new ArrayList<ArcTrajet>();
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
        
        List<ArcTrajet> liste;
        
        try {
        	liste = itineraire.getEdgeList();
        	liste = depenaliser(gr, liste, penalite);
        	System.out.println("Shortest Path : "+liste);
        	return liste;
        }catch(java.lang.NullPointerException e){
        	System.out.println("Aucun itineraire correspondant.");
        	liste = new ArrayList<ArcTrajet>();
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
