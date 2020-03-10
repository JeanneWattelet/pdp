package domain;

import transport.*;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.ALTAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.*;
import java.util.*;


public class GrapheTrajet {
	SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> g;
	AStarAdmissibleHeuristic<String> h;
	
	public GrapheTrajet(Set<Trajet> set) {
		g = new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Set<String> heuristique=ajouterSommets(set);
		ajouterAretesDeTransport(set);
		ajouterAretesAttente(set);
		g.addVertex("depart");
		g.addVertex("arrive");
		heuristique.add("depart");
		heuristique.add("arrive");
		creatHeuristicForAStar(heuristique);

	}
	
	public Set<String> ajouterSommets(Set<Trajet> trajets) {
		HashSet<String> set = new HashSet<String>();
		String sommet;
		Trajet t;
		Station s;
		Iterator<Trajet> i = trajets.iterator();
		Horaire h;
		while(i.hasNext()) {
			t=i.next();
			Map<Station, Horaire> arrets = t.getArrets();
			Set<Station> stations = arrets.keySet();
			Iterator<Station> j = stations.iterator();
			while(j.hasNext()) {
				s=j.next();
				h=arrets.get(s);
				sommet=s.toString()+h.toString();
				if(!this.g.containsVertex(sommet)) {
					this.g.addVertex(sommet);
					set.add(sommet);
				}
			}
			
		}
		return set;
	}
	
	private void ajouterAretesDeTransport(Set<Trajet> trajets){
		String sommetA, sommetB;
		Trajet t;
		Station sA, sB;
		Iterator<Trajet> i = trajets.iterator();
		Horaire hA, hB;
		while(i.hasNext()) {
			t=i.next();
			Map<Station, Horaire> arrets = t.getArrets();
			Set<Station> stations = arrets.keySet();
			Iterator<Station> a = stations.iterator();
			while(a.hasNext()) {
				sA = a.next();
				hA = arrets.get(sA);
				Iterator<Station> b = stations.iterator();
				while(b.hasNext()) {
					sB = b.next();
					hB = arrets.get(sB);
					if(hA.estAvant(hB)){
						sommetA = sA.toString()+hA.toString();
						sommetB = sB.toString()+hB.toString();
						addWeightedEdge(sommetA, sommetB, hA.tempsEntre(hB));
					}
				}
			}
		}
	}
	
	private void ajouterAretesAttente(Set<Trajet> trajets) {
		String sommetA, sommetB;
		Trajet tA, tB;
		Horaire hA, hB;
		Station sA, sB;
		Iterator<Trajet> iA = trajets.iterator();
		while(iA.hasNext()) {
			tA=iA.next();
			Map<Station, Horaire> arretsA = tA.getArrets();
			Set<Station> stationsA = arretsA.keySet();
			Iterator<Station> a = stationsA.iterator();
			while(a.hasNext()) {
				sA = a.next();
				Iterator<Trajet> iB = trajets.iterator();
				while(iB.hasNext()) {
					tB = iB.next();
					Map<Station, Horaire> arretsB = tB.getArrets();
					Set<Station> stationsB = arretsB.keySet();
					Iterator<Station> b = stationsB.iterator();
					while(b.hasNext()) {
						sB = b.next();
						if(sA==sB) {
							hA = arretsA.get(sA);
							hB = arretsB.get(sB);
							if(hA.estAvant(hB)) {
								sommetA = sA.toString()+hA.toString();
								sommetB = sB.toString()+hB.toString();
								addWeightedEdge(sommetA, sommetB, hA.tempsEntre(hB));
							}
						}
					}
				}
			}
		}
	}
	
	public void addWeightedEdge(String v1, String v2, int weight) {
		if(!v1.contains(v2)) {
			g.addEdge(v1, v2);
			g.setEdgeWeight(v1, v2, weight);
		}
	}
	
	public void creatHeuristicForAStar(Set<String> set) {
		h = new ALTAdmissibleHeuristic<String, DefaultWeightedEdge>(g,set);
	}
	
	public void ajouterDepart(Station from, Set<Trajet> set) {

		String sommet;
		
		Iterator<Trajet> i = set.iterator();
		Trajet t;
		while(i.hasNext()) {
			t=i.next();
			Map<Station, Horaire> map = t.getArrets();
			Set<Station> stations = map.keySet();
			Iterator<Station> j = stations.iterator();
			while(j.hasNext()) {
				Station s = j.next();
				if(s.equals(from)) {
					sommet = s.toString()+map.get(s).toString();
					addWeightedEdge("depart", sommet, 0);
				}
			}
		}
		
	}
	
	public void ajouterArrive(Station to, Set<Trajet> set) {
		String sommet;
		
		Iterator<Trajet> i = set.iterator();
		Trajet t;
		while(i.hasNext()) {
			t=i.next();
			
			Map<Station, Horaire> map = t.getArrets();
			Set<Station> stations = map.keySet();
			Iterator<Station> j = stations.iterator();
			while(j.hasNext()) {
				Station s = j.next();
				if(s.equals(to)) {
					sommet = s.toString()+map.get(s).toString();
					addWeightedEdge(sommet, "arrive", 0);
				}
			}
		}
		
		
	}
	
	public void astar(Station from, Station to, Horaire h, Set<Trajet> set) {
		AStarShortestPath<String, DefaultWeightedEdge> astar = new AStarShortestPath<String, DefaultWeightedEdge>(this.g, this.h);
        
		ajouterDepart(from, set);
		
		ajouterArrive(to, set);
		
		
		System.out.println("Shortest Path : "+astar.getPath("depart", "arrive"));
        System.out.println("Weight of this path : "+astar.getPathWeight("depart", "arrive"));
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
		Graphe o = (Graphe) obj;
		if(o.g!=this.g)
			return false;
		return true;
	}
	
}
