package Domaine;
import Transport.*;

import java.io.IOException;


public class Application {

	public static void main(String[] args) throws IOException {
		
		long debut = System.currentTimeMillis(); 
		
		
		//Carte v = new Carte("Bordeaux", lignes);
		//HashSet<Integer> set = new HashSet<Integer>();
		//set.add(5);
		
		Horaire hor = new Horaire(3, 7, 14,0);
		
		GrapheTrajet g = new GrapheTrajet("src/GTFS_TEST");
		System.out.println("Creation du graphe: "+(double)((System.currentTimeMillis()-debut)/1000)+" secondes");
		
		debut = System.currentTimeMillis();
		g.astar("Tauzia" ,"Cracovie", hor);
		System.out.println("A*: "+(double)((System.currentTimeMillis()-debut)/1000)+" secondes");
		
		debut = System.currentTimeMillis();
		SerializeGrapheTrajet.serialiserGrapheTrajet(g);
		System.out.println("Serialisation : "+(double)((System.currentTimeMillis()-debut))+" secondes");
		
		
		System.out.println((double)((System.currentTimeMillis()-debut))+" secondes");

		g.astar("Tauzia" ,"Cracovie", hor);
		/*GrapheTrajet aa = SerializeGrapheTrajet.deserialiserGrapheTrajet(122);
		aa.toString();*/
		
	} // fin du main();
}
