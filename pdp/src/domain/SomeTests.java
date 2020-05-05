package domain;

import java.util.HashSet;
import java.util.Scanner;
import java.util.List;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import transport.*;

//import java.util.HashSet;
//import java.util.Set;

//import infra.Donnees;
//import infra.DonneesAgency;
//import infra.DonneesCalendar;

public class SomeTests {

	public static void main(String[] args) {
		
		String h = "8:34:00";
		
		long temps = System.currentTimeMillis(); 
		
		temps = System.currentTimeMillis();
		
		ChercherChemin c = new ChercherChemin();
		
		c.chercherChemin("Russel", "La Belle Rose", h.toString(), 2, 0, true);
		
		System.out.println("Deserialisation : "+(System.currentTimeMillis()-temps)+" millisecondes");
		
		
		
		/*GrapheTrajet g;
		try {
			/*System.out.println("Début création 3");
			g = new GrapheTrajet(3);
			System.out.println("Creation du graphe 3 : "+(System.currentTimeMillis()-temps)+" millisecondes");
			
			System.out.println("Début sérialisation 3");
			temps = System.currentTimeMillis();
			SerializeGrapheTrajet.serialiserGrapheTrajet(g);
			System.out.println("Serialisation 3 : "+(System.currentTimeMillis()-temps)+" millisecondes");
			System.out.println("Début création 5");
			temps = System.currentTimeMillis();
			g = new GrapheTrajet(5);
			System.out.println("Creation du graphe 5: "+(System.currentTimeMillis()-temps)+" millisecondes");
			System.out.println("Début sérialisation 5");
			temps = System.currentTimeMillis();
			SerializeGrapheTrajet.serialiserGrapheTrajet(g);
			System.out.println("Serialisation "+g.getJour()+" : "+(System.currentTimeMillis()-temps)+" millisecondes");
			/*System.out.println("Début création 6");
			temps = System.currentTimeMillis();
			g = new GrapheTrajet(6);
			System.out.println("Creation du graphe 6 : "+(System.currentTimeMillis()-temps)+" millisecondes");
			System.out.println("Début sérialisation 6");
			temps = System.currentTimeMillis();
			SerializeGrapheTrajet.serialiserGrapheTrajet(g);
			System.out.println("Serialisation 6 : "+(System.currentTimeMillis()-temps)+" millisecondes");
			System.out.println("Début création 7");
			temps = System.currentTimeMillis();
			g = new GrapheTrajet(7);
			System.out.println("Creation du graphe 7 : "+(System.currentTimeMillis()-temps)+" millisecondes");
			System.out.println("Début sérialisation 7");
			temps = System.currentTimeMillis();
			SerializeGrapheTrajet.serialiserGrapheTrajet(g);
			System.out.println("Serialisation 7 : "+(System.currentTimeMillis()-temps)+" millisecondes");
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		/*GrapheTrajet g2 = SerializeGrapheTrajet.deserialiserGrapheTrajet(1);
		System.out.println("Deserialisation : "+(System.currentTimeMillis()-temps)+" millisecondes");
		
		Horaire h = new Horaire(Horaire.VENDREDI, 8, 34, 00);
		//Horaire h2 = new Horaire(Horaire.VENDREDI, 17, 34, 00);
		/*PrintWriter writer;
		try {
			writer = new PrintWriter("graphe1.txt","utf8");
			writer.println(g2);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("End");*/
		/*
		temps = System.currentTimeMillis();
		g2.dijkstraArriverA("Stade Musard", "La Belle Rose", h2);
		System.out.println("Dijkstra : "+(System.currentTimeMillis()-temps)+" millisecondes");
		
		/*temps = System.currentTimeMillis();
		g2.astar("Saige", "Belcier", h);
		System.out.println("A* : "+(System.currentTimeMillis()-temps)+" millisecondes");
			
		temps = System.currentTimeMillis();
		g2.astar("Saige", "Belcier", h);
		System.out.println("A* : "+(System.currentTimeMillis()-temps)+" millisecondes");
			
		temps = System.currentTimeMillis();
		g2.dijkstraArriverA("Saige", "Belcier", h2);
		System.out.println("DiskstraArriverA : "+(System.currentTimeMillis()-temps)+" millisecondes");
			
		temps = System.currentTimeMillis();
		g2.astarPenalisant("Saige", "Belcier", h, "Attente");
		System.out.println("A* : "+(System.currentTimeMillis()-temps)+" millisecondes");*/
	}
}
