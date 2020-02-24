package Domain;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.graph.DefaultEdge;

public abstract class Oiseau implements AStarAdmissibleHeuristic<Integer>{
	
	public Graph<String, DefaultEdge> g;
	
	public Oiseau() {
		
	}
	
	public double getCostEstimate() {
		return 1;
	}
	
}