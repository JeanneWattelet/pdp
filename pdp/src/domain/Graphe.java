package domain;


import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.ALTAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.*;
import java.util.*;


public class Graphe {
	
	SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> g;
	AStarAdmissibleHeuristic<String> h;
	
	//Contructors
	
	//default : creat 4 vertexs and 4 edges
	/*public Graphe() {
		g = new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		createStringGraph();
	}*/
	
	//creat a graph with all map's key as vertexs and all pairs [key;map(key)] as edges with a weight of map(key(key))
	public Graphe(HashMap<String, HashMap<String, Double>> map) {
		g = new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		creatVertexsFromStringSet(map.keySet());
		creatWeightedEdgesFromStringMap(map);
		creatHeuristicForAStar(map.keySet());

	}
	
	
	
	//default graph
	/*public void createStringGraph() {
        String v1 = "Gare St-Jean";
        String v2 = "Peixotto";
        String v3 = "v3";
        String v4 = "v4";

        // add the vertices
        this.g.addVertex(v1);
        this.g.addVertex(v2);
        this.g.addVertex(v3);
        this.g.addVertex(v4);    
        
        // add edges to create a circuit
        addWeightedEdge(v1, v2, 5);
        addWeightedEdge(v2, v3, 4);
        addWeightedEdge(v1, v4, 8);
        addWeightedEdge(v4, v3, 8);
       
        //configure the heuristic for AStar
        HashSet<String> s = new HashSet<String>();
        s.add(v1);
        s.add(v2);
		creatHeuristicForAStar(s);
    }*/
	
	
	//Used by constructor
	
	public void creatVertexsFromStringSet(Set<String> set) {
		Iterator<String> i = set.iterator();
		String stop;
		while(i.hasNext()) {
			stop=i.next();
			this.g.addVertex(stop);
		}
	}
	
	public void creatWeightedEdgesFromStringMap(HashMap<String, HashMap<String, Double>> map) {
		Set<String> set = map.keySet(); 
		Iterator<String> i = set.iterator();
		String from;
		while(i.hasNext()) {
			from=i.next();
			String to;
			Iterator<String> j = map.get(from).keySet().iterator();
			while(j.hasNext()) {
				to=j.next();
				addWeightedEdge(from, to, map.get(from).get(to));
			}
		}
	}
	
	public void addWeightedEdge(String v1, String v2, double weight) {
		g.addEdge(v1, v2);
		g.setEdgeWeight(v1, v2, weight);
	}
	
	
	//For AStar
	
	public void creatHeuristicForAStar(Set<String> set) {
		h = new ALTAdmissibleHeuristic<String, DefaultWeightedEdge>(g,set);
	}
	
	public void astar(String from, String to) {
        AStarShortestPath<String, DefaultWeightedEdge> astar = new AStarShortestPath<String, DefaultWeightedEdge>(g, h);
        System.out.println("Shortest Path : "+astar.getPath(from, to));
        System.out.println("Weight of this path : "+astar.getPathWeight(from, to));
	}
	
	
	//Override from Object
	
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