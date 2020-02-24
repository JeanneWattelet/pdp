package Domain;

import java.util.HashMap;

//import java.util.HashSet;
//import java.util.Set;

//import infra.Donnees;
//import infra.DonneesAgency;
//import infra.DonneesCalendar;

public class SomeTests {

	public static void main(String[] args) {
		Graphe g = new Graphe();
		System.out.println(g);
		g.astar("Gare St-Jean", "Peixotto");
		
		System.out.println("---------");
		
		HashMap<String, HashMap<String, Double>> map = new HashMap<String, HashMap<String, Double>>();
		HashMap<String, Double> mapA = new HashMap<String, Double>();
		HashMap<String, Double> mapB = new HashMap<String, Double>();
		mapB.put("A", 6.0);
		mapA.put("B", 8.0);
		map.put("B", mapB);
		map.put("A", mapA);
		Graphe g2 = new Graphe(map);
		System.out.println(g2);
		g2.astar("A", "B");
		//System.out.println(g.astar());
	}

}
