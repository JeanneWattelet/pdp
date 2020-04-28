package domain;

import java.util.HashSet;
import java.util.Scanner;
import java.util.List;
import java.io.IOException;

import transport.*;

//import java.util.HashSet;
//import java.util.Set;

//import infra.Donnees;
//import infra.DonneesAgency;
//import infra.DonneesCalendar;

public class SomeTests {

	public static void main(String[] args) {
		
		
		long debut = System.currentTimeMillis(); 
		

		Donnees recup = new Donnees();
		List<Ligne> Lignes;
		try {
			Lignes = recup.ChargerDonnees("src\\GTFS_TEST");
			
			GrapheTrajet g = new GrapheTrajet(Lignes);
			System.out.println("Creation du graphe: "+(double)((System.currentTimeMillis()-debut)/1000)+" secondes");
			
			
			debut = System.currentTimeMillis();
			SerializeGrapheTrajet.serialiserGrapheTrajet(g);
			System.out.println("Serialisation : "+(double)((System.currentTimeMillis()-debut))+" secondes");
			
			
			
			//System.out.println((double)((System.currentTimeMillis()-debut))+" secondes");

			
			Horaire hor = new Horaire(3, 7, 14,0);
			g.astar("Tauzia" ,"Cracovie", hor);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Recuperer les donneees dans lignes 
		
		
		
	}
}
