package domain;

import transport.*;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.ALTAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.*;
import java.util.*;


public class GrapheTrajet implements java.io.Serializable{
	private static final long serialVersionUID = -7760248029679113128L;
	private SimpleDirectedWeightedGraph<String, ArcTrajet> g;
	private transient AStarAdmissibleHeuristic<String> h;
	
	public GrapheTrajet(Set<Ligne> set) {
		g = new SimpleDirectedWeightedGraph<String, ArcTrajet>(ArcTrajet.class);
		Set<String> heuristique=ajouterSommets(set);
		ajouterAretesDeTransport(set);
		ajouterAretesAttente(set);
		g.addVertex("depart");
		g.addVertex("arrivee");
		heuristique.add("depart");
		heuristique.add("arrivee");
		creatHeuristicForAStar(heuristique);
	}
	
	public String nommerSommet(Station s, Horaire h) {
		return s.toString()+"&"+h.toString();
	}
	
	public Set<String> ajouterSommets(Set<Ligne> lignes) {
		HashSet<String> set = new HashSet<String>();
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
					sommet = nommerSommet(s, h);
					//sommet=s.toString()+h.toString();//on cree un sommet au nom unique selon son lieu et son horaire
					if(!this.g.containsVertex(sommet)) {
						this.g.addVertex(sommet);
						set.add(sommet);
					}
				}
			}
		}
		return set;
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
				while(iterStationsA.hasNext()) {//On regarde chaque station par lequel le trajet passe, qui pourraient être une source d'un arc
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
							addWeightedEdge(sommetA, sommetB, hA.tempsEntre(hB), l.getVehicule());//ajout des sommets
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
								addWeightedEdge(sommetA, sommetB, horaireA.tempsEntre(horaireB), 0);
							}
						}
					}
				}
			}
		}
	}
	
	public void addWeightedEdge(String v1, String v2, int weight, int vehicule) {
		if(!v1.contains(v2)) {
			ArcTrajet arc = new ArcTrajet(vehicule);
			g.addEdge(v1, v2, arc);
			g.setEdgeWeight(v1, v2, weight);
		}
	}
	
	public void creatHeuristicForAStar(Set<String> set) {
		h = new ALTAdmissibleHeuristic<String, ArcTrajet>(g,set);
	}
	
	public void ajouterDepart(Station from, Set<Ligne> set) {
		String sommet;
		Iterator<Ligne> iterLigne = set.iterator();
		Ligne ligne;
		Trajet t;
		while(iterLigne.hasNext()) {
			ligne=iterLigne.next();
			Set<Trajet> setTrajet = ligne.getTrajets();
			Iterator<Trajet> iterTrajet = setTrajet.iterator();
			while(iterTrajet.hasNext()) {
				t=iterTrajet.next();
				Map<Station, Horaire> map = t.getArrets();
				Set<Station> stations = map.keySet();
				Iterator<Station> j = stations.iterator();
				while(j.hasNext()) {
					Station s = j.next();
					if(s.equals(from)) {
						sommet = nommerSommet(s, map.get(s));
						//sommet = s.toString()+map.get(s).toString();
						addWeightedEdge("depart", sommet, 0, 0);
					}
				}	
			}
		}
		
	}
	
	public void ajouterArrivee(Station to, Set<Ligne> set) {
		String sommet;
		Iterator<Ligne> iterLigne = set.iterator();
		Ligne ligne;
		Trajet t;
		while(iterLigne.hasNext()) {
			ligne=iterLigne.next();
			Set<Trajet> setTrajet = ligne.getTrajets();
			Iterator<Trajet> iterTrajet = setTrajet.iterator();
			while(iterTrajet.hasNext()) {
				t=iterTrajet.next();
				Map<Station, Horaire> map = t.getArrets();
				Set<Station> stations = map.keySet();
				Iterator<Station> j = stations.iterator();
				while(j.hasNext()) {
					Station s = j.next();
					if(s.equals(to)) {
						sommet = nommerSommet(s, map.get(s));
						//sommet = s.toString()+map.get(s).toString();
						addWeightedEdge(sommet, "arrivee", 0, 0);
					}
				}	
			}
		}	
	}

	public void retirerArcsDepartArrivee() {
		Set<ArcTrajet> dep = g.edgesOf("depart");
		Set<ArcTrajet> arr = g.edgesOf("arrivee");
		
		Iterator<ArcTrajet> i = dep.iterator();
		Iterator<ArcTrajet> j = arr.iterator();
		
		ArcTrajet arc;
		
		while(i.hasNext()) {
			arc = i.next();
			if(arc.getSourceT().equals("depart")) {
				g.removeEdge(arc);
			}
		}
		
		while(j.hasNext()) {
			arc = j.next();
			if(arc.getTargetT().equals("arrivee")) {
				g.removeEdge(arc);
			}
		}
	}
	
	public List<ArcTrajet> astar(Station from, Station to, Horaire h, Set<Ligne> set) {
		AStarShortestPath<String, ArcTrajet> astar = new AStarShortestPath<String, ArcTrajet>(this.g, this.h);
		
		ajouterDepart(from, set);
		ajouterArrivee(to, set);
		
		GraphPath<String, ArcTrajet> itineraire = astar.getPath("depart", "arrivee");
		
		System.out.println("Shortest Path : "+itineraire.getEdgeList());
        System.out.println("Weight of this path : "+itineraire.getWeight());
        System.out.println("Number of means of transportation used : "+(itineraire.getLength()-2));
        
        retirerArcsDepartArrivee();
        
        return itineraire.getEdgeList();
	}
	
	public List<ArcTrajet> dijkstra(Station from, Station to, Horaire h, Set<Ligne> set) {
		DijkstraShortestPath<String, ArcTrajet> dijkstra = new DijkstraShortestPath<String, ArcTrajet>(this.g);
		
		ajouterDepart(from, set);
		ajouterArrivee(to, set);
		
		GraphPath<String, ArcTrajet> itineraire = dijkstra.getPath("depart", "arrivee");
		
		System.out.println("Shortest Path : "+itineraire.getEdgeList());
        System.out.println("Weight of this path : "+itineraire.getWeight());
        System.out.println("Number of means of transportation used : "+(itineraire.getLength()-2));
        
        retirerArcsDepartArrivee();
        
        return itineraire.getEdgeList();
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
