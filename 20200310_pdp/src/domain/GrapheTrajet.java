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
	
	public GrapheTrajet(HashSet<Trajet> trajets) {
		g = new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Set<String> heuristique=ajouterArrets(trajets);
		ajouterAretesDeTransport(trajets);
		creatHeuristicForAStar(heuristique);

	}
	
	public Set<String> ajouterArrets(Set<Trajet> trajets) {
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
	
	public void ajouterAretesDeTransport(Set<Trajet> trajets){
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
					if(hA.estAvant(hB)) {
						sommetA = sA.toString()+hA.toString();
						sommetB = sB.toString()+hB.toString();
						addWeightedEdge(sommetA, sommetB, hA.tempsEntre(hB));
					}
				}
			}
		}
	}
	
	public void addWeightedEdge(String v1, String v2, int weight) {
		g.addEdge(v1, v2);
		g.setEdgeWeight(v1, v2, weight);
	}
	
	public void creatHeuristicForAStar(Set<String> set) {
		h = new ALTAdmissibleHeuristic<String, DefaultWeightedEdge>(g,set);
	}
	
	public void astar(String from, String to) {
        AStarShortestPath<String, DefaultWeightedEdge> astar = new AStarShortestPath<String, DefaultWeightedEdge>(g, h);
        System.out.println("Shortest Path : "+astar.getPath(from, to));
        System.out.println("Weight of this path : "+astar.getPathWeight(from, to));
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
