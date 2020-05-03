package Domaine;
import Transport.*;

import java.io.IOException;
import java.util.List;


public class Application {

	public static void main(String[] args) throws IOException {
		
		long debut = System.currentTimeMillis(); 
		

		Donnees recup = new Donnees();
		List<Ligne> Lignes = recup.ChargerDonnees("src\\GTFS_TEST"); // Recuperer les donneees dans lignes 
		
		
		GrapheTrajet g = new GrapheTrajet(Lignes,3);
		System.out.println("Creation du graphe: "+(double)((System.currentTimeMillis()-debut)/1000)+" secondes");
		
		
		/*debut = System.currentTimeMillis();
		SerializeGrapheTrajet.serialiserGrapheTrajet(g);
		System.out.println("Serialisation : "+(double)((System.currentTimeMillis()-debut)/1000)+" secondes"); */
		
		
		
		//System.out.println((double)((System.currentTimeMillis()-debut))+" secondes");

		
		Horaire hor = new Horaire(7, 14,10);
		g.astar("Tauzia", "Cracovie" ,hor);
		
		/*GrapheTrajet aa = SerializeGrapheTrajet.deserialiserGrapheTrajet(122);
		aa.toString();*/
		
	} // fin du main();
}
