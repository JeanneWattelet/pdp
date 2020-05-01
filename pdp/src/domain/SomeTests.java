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
		
		
		
		long temps = System.currentTimeMillis(); 
		
		temps = System.currentTimeMillis();
		GrapheTrajet g2 = SerializeGrapheTrajet.deserialiserGrapheTrajet(1);
		System.out.println("Deserialisation : "+(System.currentTimeMillis()-temps)+" millisecondes");
		
		//Horaire h = new Horaire(Horaire.VENDREDI, 8, 34, 00);
		Horaire h2 = new Horaire(Horaire.VENDREDI, 17, 34, 00);
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
