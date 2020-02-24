package Domain;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.ALTAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.*;
import org.jgrapht.nio.*;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.traverse.*;

import java.io.*;
import java.net.*;
import java.util.*;


public class Graphe {
	
	SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> g;
	
	public Graphe() {
		g = new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		createStringGraph();
	}
	
	public void addWeightedEdge(String v1, String v2, double weight) {
		g.addEdge(v1, v2);
		g.setEdgeWeight(v1, v2, weight);
	}
	
	public void createStringGraph() {
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
        
        HashSet<String> s = new HashSet<String>();
        s.add(v1);
        s.add(v2);       
        
        ALTAdmissibleHeuristic<String, DefaultWeightedEdge> h = new ALTAdmissibleHeuristic<String, DefaultWeightedEdge>(g,s);
        AStarShortestPath<String, DefaultWeightedEdge> astar = new AStarShortestPath<String, DefaultWeightedEdge>(g, h);
        System.out.println(astar.getPath(v1, v3));
        System.out.println(astar.getPathWeight(v1, v3));
    }
	
	public String toString() {
		return g.toString();
	}
	

}