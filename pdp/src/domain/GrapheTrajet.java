package domain;

import transport.*;

import de.fhpotsdam.unfolding.examples.animation.*;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.ALTAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.*;

import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;


public class GrapheTrajet implements java.io.Serializable{
	private static final long serialVersionUID = -7760248029679113128L;
	private SimpleDirectedWeightedGraph<String, ArcTrajet> g;
	private int jour;
	
	private static double PENALITE = 2;
	private static double TEMPSMAXATTENTE = 1200; //on attendra jamais plus de 1200 secondes soit 20 minutes à un arrêt.
	
	public GrapheTrajet(List<Ligne> LignesTrajet, int jour) throws IOException {
		this.jour = jour;
		g = new SimpleDirectedWeightedGraph<String, ArcTrajet>(ArcTrajet.class);
		ajouterSommets(LignesTrajet,2);
		ajouterAretesDeTransport(LignesTrajet,2);
		ajouterAretesMarche();
		ajouterAretesAttenteTrajets();
		g.addVertex("depart");
		g.addVertex("arrivee");
	} 
	
	public GrapheTrajet(int jour) throws IOException {
		this.jour = jour;
		g = new SimpleDirectedWeightedGraph<String, ArcTrajet>(ArcTrajet.class);
		
		// sauvegarde tram et bus
		//SerializeGrapheTrajet.saveTramEtBus(jour);
		
		// charge tram et bus
		//List<Ligne> tram = SerializeGrapheTrajet.loadTram(jour);
		//List<Ligne> bus = SerializeGrapheTrajet.loadBus(jour);
		
		// charger les sommets pour tous les jours
		//ajouterSommets(tram, 0); 
		//ajouterSommets(bus, 1);
		
		// sauvegarde aretes de transport
		//ajouterAretesDeTransport(tram, 0); // ok (a faire lors du lancement de la carte - load tram avant)
		//ajouterAretesDeTransport(bus, 1); // ok (a faire lors du lancement de la carte - load bus avant)
		
		// sauvegarde aretes attente meme station
		//creerArretesAttenteMemeStation(1); // inutiles pour les trams sur bordeaux
		//ajouterAttente();
		
		// sauvegarde aretes attente en fonction de marche
		//creerAretesAttenteEnFonctionDeMarche(); 
		
		// sauvegarde aretes de marche
		

		//ajouterAretesMarche(); //  ok (a faire lors du lancement de l'interface)
		//ajouterAretesAttenteTrajets(); // en cours - besoin des vertex (fait pour jour 3-) (a faire lors du lancement de la carte)
		
		// test
		/*List<ArcTrajet> l1 = SerializeGrapheTrajet.deserialiserGrapheTrajet(jour);
		for(int i = 0; i < l1.size(); i++) {
			ArcTrajet a = l1.get(i);
			if(!g.containsVertex(a.getSourceT())) {
				g.addVertex(a.getSourceT());
			}
			if(!g.containsVertex(a.getTargetT())) {
				g.addVertex(a.getTargetT());
			}
			if(!a.getSourceT().contains(a.getTargetT())) {
				g.addEdge(a.getSourceT(), a.getTargetT(), a);
				g.setEdgeWeight(a.getSourceT(), a.getTargetT(), a.getWeightT());
			}
		}
		System.out.println(g.vertexSet());*/
		
	} 
	
	private void reduceAretesMarche() {
		List<ArcTrajet> l = new ArrayList<ArcTrajet>();

		try {
			ObjectInputStream load = new ObjectInputStream(new FileInputStream("src/someData/aretesMarche" + jour + ".dat"));	
			l = (List<ArcTrajet>) load.readObject();
			System.out.println(l.size());
			load.close();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		for(int i = 0; i < l.size(); i++) {
			System.out.println(i);
			ArcTrajet a = l.get(i);
			if(a.getSourceT() != null && a.getTargetT() != null) {
				String s1 = a.getSourceT();
				String s2 = a.getTargetT();
				Horaire h1 = trouverHoraire(s1);
				Horaire h2 = trouverHoraire(s2);
				int to1 = toSec(h1);
				int to2 = toSec(h2);
				
				
				if(a.getWeightT() < 1000 && Math.abs(to1 - to2) < 1800) {
					ArcTrajet tmp = addWeightedEdge(a.getSourceT(), a.getTargetT(), a.getWeightT(), Ligne.PIED, Ligne.PIED);
				}
			}
		}
		List<ArcTrajet> lEnd = new ArrayList<ArcTrajet>();
		lEnd.addAll(g.edgeSet());
		System.out.println(lEnd.size());

		try {
			ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/someData/aretesMarcheSimplifier" + jour + ".dat"));
			save.writeObject(lEnd);
			save.close();
		}
		catch(Exception e) {

		}
		
	}

	private int toSec(Horaire h2) {
		return h2.getHeure()*3600+h2.getMinute()*60+h2.getSeconde();
	}

	private void ajouterAttente() {
		List<MemeEndroit> l = new ArrayList<MemeEndroit>();
		List<String> listS = new ArrayList<String>();
		List<Integer> listInt = new ArrayList<Integer>();
		List<MemeEndroit> best = new ArrayList<MemeEndroit>();

		try {
			ObjectInputStream save = new ObjectInputStream(new FileInputStream("src/someData/MemeEndroit" + jour + ".dat"));
			l = (List<MemeEndroit>) save.readObject();
			save.close();
		}
		catch(Exception e) {
			System.out.println("error : " + e.getMessage());
		}

		System.out.println(l.size());

		Set<String> set = new HashSet<String>();

		for(int i = 0; i < l.size(); i++) {
			System.out.println(i);
			String m = l.get(i).getLocS() + l.get(i).getNumeroS() + l.get(i).getNumeroT();
			set.add(m);
			listInt.add(10000);
			best.add(new MemeEndroit());
		}

		listS.addAll(set);
		System.out.println(best.size());

		for(int i = 0; i < l.size(); i++) {
			System.out.println(i);

			int index = listS.indexOf(l.get(i).getLocS()+l.get(i).getNumeroS()+l.get(i).getNumeroT());
			if(l.get(i).getEcart() < listInt.get(index)) {
				listInt.set(index, l.get(i).getEcart());
				best.set(index, l.get(i));
			}
		}

		for(int i = 0; i < best.size(); i++) {
			MemeEndroit m = l.get(i);
			ArcTrajet a = addWeightedEdge(m.getLocS(), m.getLocT(), m.getEcart(), Ligne.ATTENTE, "attente");
		}

		List<ArcTrajet> lEnd = new ArrayList<ArcTrajet>();
		lEnd.addAll(g.edgeSet());

		try {
			ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/someData/aretesAttenteMemeEndroitBus" + jour + ".dat"));
			save.writeObject(lEnd);
			save.close();
		}
		catch(Exception e) {

		}

	}

	private void creerArretesAttenteMemeStation(int p) {
		List<Ligne> tran = new ArrayList<Ligne>();
		try {
			if(p ==0) {
				ObjectInputStream save = new ObjectInputStream(new FileInputStream("src/someData/tram" + jour + ".dat"));
				tran = (List<Ligne>) save.readObject();
				save.close();
			}
			else if(p ==1) {
				ObjectInputStream save2 = new ObjectInputStream(new FileInputStream("src/someData/bus" + jour + ".dat"));
				tran = (List<Ligne>) save2.readObject();
				save2.close();
			}
		}
		catch(Exception e) {
			System.out.println("error : " + e.getMessage());
		}

		System.out.println(tran.size());
		List<MemeEndroit> lEnd = new ArrayList<MemeEndroit>();

		for(int i = 0; i < tran.size()-1; i++) { // parcourt des lignes
			List<Trajet> l1 = tran.get(i).getTrajets();
			System.out.println(i);

			for(int j = i+1; j < tran.size(); j++) { // parcourt des autres lignes
				List<Trajet> l2 = tran.get(j).getTrajets();

				for(int k = 0; k < l1.size(); k++) {
					Map<Station, Horaire> t1 = l1.get(k).getArrets();
					for(int l = 0; l < l2.size(); l++) {
						Map<Station, Horaire> t2 = l2.get(l).getArrets();

						Iterator<Station> it1 = t1.keySet().iterator();
						while(it1.hasNext()) {
							Station s1 = it1.next();
							Horaire h1 = t1.get(s1);
							Iterator<Station> it2 = t2.keySet().iterator();
							while(it2.hasNext()) {
								Station s2 = it2.next();
								Horaire h2 = t2.get(s2);

								if(s1.getNom().equals(s2.getNom()) && s1.getPosition().equals(s2.getPosition())) {
									MemeEndroit p1 = new MemeEndroit();
									if(h2.estAvant(h1)) {
										p1 = new MemeEndroit(nommerSommet(s2, h2), nommerSommet(s1,h1), differenceHoraire(h2, h1), tran.get(j).getNom(), tran.get(i).getNom());
									}
									else {
										p1 = new MemeEndroit(nommerSommet(s1, h1), nommerSommet(s2,h2), differenceHoraire(h1, h2), tran.get(i).getNom(), tran.get(j).getNom());
									}
									if(!lEnd.contains(p1)) {
										lEnd.add(p1);
									}
								}
							}
						}
					}
				}

			}
		}
		System.out.println("Creation des arcs de trajet terminee");
		System.out.println(lEnd.size());

		List<ArcTrajet> l1 = new ArrayList<ArcTrajet>();
		//l1.addAll(g.edgeSet());

		try {
			if(p == 0) {
				ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/someData/MemeEndroit" + jour + ".dat"));
				save.writeObject(l1);
				save.close();
			}
			else if(p==1) {
				ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/someData/MemeEndroit" + jour + ".dat"));
				save.writeObject(lEnd);
				save.close();
			}
			else {
				ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/someData/MemeEndroit" + jour + ".dat"));
				save.writeObject(l1);
				save.close();
			}
		}
		catch(Exception e) {
			System.out.println("error : " + e.getMessage());
		}

		System.out.println("Creation du fichier de trajet terminee");

	}
	
	private void creerAretesAttenteEnFonctionDeMarche() {
		List<ArcTrajet> l = new ArrayList<ArcTrajet>();
		List<String> set = new ArrayList<String>();

		try {

			ObjectInputStream load = new ObjectInputStream(new FileInputStream("src/data1/ListArcTrajetPied" + jour + ".ser"));
			ObjectInputStream load1 = new ObjectInputStream(new FileInputStream("src/dataSommetsAretes/vectorTram" + jour + ".dat"));
			ObjectInputStream load2 = new ObjectInputStream(new FileInputStream("src/dataSommetsAretes/vectorBus" + jour + ".dat"));

			l = (List<ArcTrajet>) load.readObject();
			System.out.println(l.size());
			
			try {
				ObjectOutputStream load11 = new ObjectOutputStream(new FileOutputStream("src/dataSommetsAretes/aretesMarche" + jour + ".dat"));
				load11.writeObject(l);
				load11.close();
			}
			catch(Exception e) {
				System.out.println(e.getMessage());
			}
			System.exit(0);
			set = (List<String>) load1.readObject();
			System.out.println(set.size());
			set.addAll((List<String>) load2.readObject());
			System.out.println(set.size());

			load.close();
			load1.close();
			load2.close();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}

		int ecartCourant = 0;
		for(int i = 0; i < l.size(); i++) {
			System.out.println(i);
			int ecart = 100000;
			String best = ""; 

			ArcTrajet a = l.get(i);
			String str1 = denommer(a.getTargetT());
			Horaire h1 = trouverHoraire(a.getTargetT());
			for(int j = 0; j < set.size(); j++) {
				String str2 = denommer(set.get(i));
				Horaire h2 = trouverHoraire(str1);
				if(str1.contentEquals(str2) && h1.estAvant(h2)) {
					ecartCourant = differenceHoraire(h1, h2);
					if(ecartCourant < ecart) {
						ecart = ecartCourant;
						best = set.get(i);
					}
				}
			}
			ArcTrajet tmp = addWeightedEdge(str1, best, ecart, Ligne.ATTENTE, "attente");
		}
		System.out.println("Creation des arcs de trajet terminee");

		List<ArcTrajet> l1 = new ArrayList<ArcTrajet>();
		l1.addAll(g.edgeSet());

		try {

			ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/dataSommetsAretes/aretesAttenteApresMarche" + jour + ".dat"));
			save.writeObject(l1);
			save.close();

		}
		catch(Exception e) {
			System.out.println("error : " + e.getMessage());
		}

		System.out.println("Creation du fichier de trajet terminee");
	}

	
	private int differenceHoraire(Horaire h1, Horaire h2) {
		return Math.abs(h2.getHeure()-h1.getHeure())*3600 + Math.abs(h2.getMinute()-h1.getMinute())*60 + Math.abs(h2.getSeconde()-h1.getSeconde());
	}
	
	public GrapheTrajet() {
		g = new SimpleDirectedWeightedGraph<String, ArcTrajet>(ArcTrajet.class);
		this.jour = 0;
	}
	
	public int getJour() {
		return jour;
	}
	
	/*
	 * Création d'un nouveau graphe à partir d'un ensemble d'entités du package "Transport"
	 * (Cette partie est utile avant la sérialisation du graphe)
	 * (Une fois que celui-ci est créé, ces fonctions ne sont plus utiles pour la résolution)
	 */
	
	private void ajouterSommets(List<Ligne> LignesTrajet, int p) {

		if(jour >7 || jour <1) { // si jour n'est pas entre 1 et 7 ==> Férier ==> meme horaire que Dimanche (== 7)
			jour = 7 ;
		}
		String sommet;
		Horaire h;

		List<String> lEnd = new ArrayList<String>();

		for(Ligne l: LignesTrajet) {//pour chaque ligne

			for(Trajet t: l.getTrajets()) {//pour chaque trajet 

				for(Station s: t.getArrets().keySet()) {//et pour chaque station dans la map(staion/horaire) de t
					h= t.getArrets().get(s); //l'horaire de la station s sur le trajet t

					if( (int) h.getJour() == (int) jour ) { // On prend que les horaires du jour indiqué   
						sommet = nommerSommet(s, h);

						//sommet=s.toString()+h.toString();//on cree un sommet au nom unique selon son lieu et son horaire
						if(!this.g.containsVertex(sommet)) {
							this.g.addVertex(sommet);
							lEnd.add(sommet);
							//System.out.println(sommet);
						}
					}
				}
			}
		}

		try {
			if(p == 0) {
				ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/dataSommetsAretes/vectorTram" + jour + ".dat"));
				save.writeObject(lEnd);
				save.close();
			}
			else if(p == 1) {
				ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/dataSommetsAretes/vectorBus" + jour + ".dat"));
				save.writeObject(lEnd);
				save.close();
			}
			else {
				ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/dataSommetsAretes/vectorOther" + jour + ".dat"));
				save.writeObject(lEnd);
				save.close();
			}
		}
		catch(Exception e) {
			System.out.println("error : " + e.getMessage());
		}
		System.out.println("Creation du fichier terminee");
	}
	
	private void ajouterAretesDeTransport(List<Ligne> LignesTrajet, int p){

		if(jour >7 || jour <1) { // si jour n'est pas entre 1 et 7 ==> Férier ==> meme horaire que Dimanche (== 7)
			jour = 7 ;
		}
		String sommetA, sommetB;
		Horaire hA, hB;
		Map<Station,Horaire> tmp = new HashMap<Station,Horaire>();
		List<Trajet> tr = new ArrayList<Trajet>();
		Map<Station,Horaire> mp = new HashMap<Station,Horaire>();

		for(int k = 0; k < LignesTrajet.size(); k++) {//On regarde les lignes successivement

			Ligne l = LignesTrajet.get(k);
			System.out.println(k);
			tr = l.getTrajets();
			for(Trajet t: tr) {//Pour chaque trajets de ces lignes
				tmp = t.getArrets();

				List<Station> list = new ArrayList<Station>();
				list.addAll(tmp.keySet());
				for(int i = 0; i < list.size()-1; i++){//On regarde chaque station par lequel le trajet passe, qui pourraient être une source d'un arc
					Station stationA = list.get(i);
					hA = tmp.get(stationA);

					if(hA.getJour() == jour) { // On prend les horaires du jour indiqué 

						for(int j = i+1; j < list.size()-1; j++) {
							Station stationB = list.get(j);

							hB = tmp.get(stationB);
							sommetA = nommerSommet(stationA, hA);
							sommetB = nommerSommet(stationB, hB);

							if(!g.containsVertex(sommetA)) {
								g.addVertex(sommetA);
							}
							if(!g.containsVertex(sommetB)) {
								g.addVertex(sommetB);
							}
							addWeightedEdge(sommetA, sommetB, hA.tempsEntre(hB), l.getVehicule(), l.getNom());//ajout des sommets	
						}
					}

				}
			}
		}
		System.out.println("Creation des arcs de trajet terminee");

		List<ArcTrajet> l = new ArrayList<ArcTrajet>();
		l.addAll(g.edgeSet());

		try {
			if(p == 0) {
				ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/dataSommetsAretes/aretesTramEntreTous" + jour + ".dat"));
				save.writeObject(l);
				save.close();
			}
			else if(p==1) {
				ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/dataSommetsAretes/aretesBusEntreTous" + jour + ".dat"));
				save.writeObject(l);
				save.close();
			}
			else {
				ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/dataSommetsAretes/aretesOtherEntreTous" + jour + ".dat"));
				save.writeObject(l);
				save.close();
			}
		}
		catch(Exception e) {
			System.out.println("error : " + e.getMessage());
		}

		System.out.println("Creation du fichier de trajet terminee");
	}
		
	private void ajouterAretesAttenteTrajets() {
		String  stationA, stationB, sommetA, sommetB ;
		Horaire horaireA, horaireB ;

		Set<String> sommets = g.vertexSet();
		
		Iterator<String> iterSommetA = sommets.iterator();
			
		ArcTrajet arc = new ArcTrajet("Pied", "Depart", "Arrivee", "Nom"), tmp;
		
		double best;
		
		while(iterSommetA.hasNext()) {
			sommetA = iterSommetA.next();
			stationA = denommer(sommetA);
			horaireA = trouverHoraire(sommetA);
			Iterator<String> iterSommetB = sommets.iterator();
			best = 0;
			while(iterSommetB.hasNext()) {
				sommetB = iterSommetB.next();
				stationB = denommer(sommetB);
				horaireB = trouverHoraire(sommetB);
				if(!(sommetA.contentEquals(sommetB))&&stationA.contentEquals(stationB)&&horaireA.estAvant(horaireB)&&(TEMPSMAXATTENTE>horaireA.tempsEntre(horaireB))) {
					if((best==0)||(best>horaireA.tempsEntre(horaireB))) {
						sommetA = nommerSommet(stationA, horaireA, trouverPosition(sommetA));
						sommetB = nommerSommet(stationB, horaireB, trouverPosition(sommetB));
						if(best!=0) {
							g.removeEdge(arc);
						}
						best = arc.getWeightT();
						tmp = addWeightedEdge(sommetA, sommetB, horaireA.tempsEntre(horaireB), Ligne.ATTENTE, "attente");
						//System.out.println(sommetA+" to "+sommetB);
						arc=tmp;
					}
				}
			}
		}
		if(g.containsEdge("depart", "arrivee")) {
			g.removeEdge("depart", "arrivee");
		}
		System.out.println("Creation des arcs d'attente");
	}
	
	private void ajouterAretesMarche() {
		ObjectInputStream ois = null;
		List<Edge> listeEdge = new ArrayList<Edge>();
	    try {
	      final FileInputStream fichier = new FileInputStream("src\\data1\\arcsEntreStationsAPied.dat");
	      ois = new ObjectInputStream(fichier);
	      listeEdge = (List<Edge>)ois.readObject() ;
	    } catch (final java.io.IOException e) {
	      e.printStackTrace();
	      return;
	    } catch (final ClassNotFoundException e) {
	      e.printStackTrace();
	      return;
	    } finally {
	      try {
	        if (ois != null) {
	          ois.close();
	        }
	      } catch (final IOException ex) {
	        ex.printStackTrace();
	      }
	    }
	    String sommet, nouveauSommet;
	    String station;
	    Set<String> sommets = new HashSet<String>();
	    int index;
	    float x, y;
	    sommets.addAll(g.vertexSet());
	    Iterator<String> iterSommets = sommets.iterator();
	    while(iterSommets.hasNext()) {
	    	sommet = iterSommets.next();
	    	station = denommer(sommet);
	    	for(Edge e : listeEdge) {
		    	if(station.contentEquals(e.getLocS())) {
		    		
		    		index = e.getTrajet().size();
		    		x = e.getTrajet().get(index).getLat();
		    		y = e.getTrajet().get(index).getLon();
		    		
		    		nouveauSommet = nommerSommet(e.getLocT(), ajouterPoids(e.getWeight(), trouverHoraire(sommet)), x, y);
		    		if(!g.containsVertex(nouveauSommet)) {
		    			g.addVertex(nouveauSommet);
		    		}
		    		addWeightedEdge(sommet, nouveauSommet, e.getWeight(), Ligne.PIED, Ligne.PIED);
		    	}
		    }
	    }
	    System.out.println("Creation des arcs de marche terminee");
	}
	
	public void transformerEdgeEnArcTrajet(int j) {
		
		jour = j;
		
		//List<Ligne> tram;
		List<Ligne> bus;
		try {
			//tram = Donnees.ChargerDonnees("src\\keolis_tram", j);
			bus = Donnees.ChargerDonnees("src\\keolis_bus", j);
			
			//ajouterSommets(tram);
			ajouterSommets(bus,1);
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ObjectInputStream ois = null;
		List<Edge> listeEdge = new ArrayList<Edge>();
	    try {
	      final FileInputStream fichier = new FileInputStream("src\\data1\\arcsEntreStationsAPied.dat");
	      ois = new ObjectInputStream(fichier);
	      listeEdge = (List<Edge>)ois.readObject() ;
	    } catch (final java.io.IOException e) {
	      e.printStackTrace();
	      return;
	    } catch (final ClassNotFoundException e) {
	      e.printStackTrace();
	      return;
	    } finally {
	      try {
	        if (ois != null) {
	          ois.close();
	        }
	      } catch (final IOException ex) {
	        ex.printStackTrace();
	      }
	    }
	    String sommet, nouveauSommet;
	    String station;
	    Set<String> sommets = new HashSet<String>();
	    int index;
	    float x, y;
	    ArcTrajet at;
	    List<ArcTrajet> lat = new ArrayList<ArcTrajet>();
	    sommets.addAll(g.vertexSet());
	    Iterator<String> iterSommets = sommets.iterator();
	    while(iterSommets.hasNext()) {
	    	sommet = iterSommets.next();
	    	station = denommer(sommet);
	    	for(Edge e : listeEdge) {
		    	if(station.contentEquals(e.getLocS())) {
		    		
		    		index = e.getTrajet().size();
		    		x = e.getTrajet().get(index-1).getLat();
		    		y = e.getTrajet().get(index-1).getLon();
		    		
		    		nouveauSommet = nommerSommet(e.getLocT(), ajouterPoids(e.getWeight(), trouverHoraire(sommet)), x, y);
		    		if(!g.containsVertex(nouveauSommet)) {
		    			g.addVertex(nouveauSommet);
		    		}
		    		at = addWeightedEdge(sommet, nouveauSommet, e.getWeight(), Ligne.PIED, Ligne.PIED);
		    		lat.add(at);
		    		at = addWeightedEdge(nouveauSommet, sommet, e.getWeight(), Ligne.PIED, Ligne.PIED);
		    		lat.add(at);
		    		//System.out.println(sommet+" - "+nouveauSommet);
		    	}
		    }
	    }
	    SerializeGrapheTrajet.serialiserArcTrajet(lat, j);
	}
	
	private Horaire ajouterPoids(double d, Horaire h) {
		return new Horaire(h.getHeure()+(int)(d/60),(int)(h.getMinute()+d)%60,h.getMinute());
	}
	
	/*
	 * Gestion des noms des sommets
	 */
	
	public static String denommer(String s) {
		if(s.contains("%")) {
			int i = s.indexOf('%');
			return s.substring(0, i);
		}
		return s;
	}
	
	public Horaire trouverHoraire(String s) {

		int h=0,m=0,sec=0;//j=0,
		if(s.contains("%")) {
			String[] str = s.split("%");
			String[] horaire = str[2].split(":");
			//j = Integer.valueOf(horaire[0]);
			h = Integer.valueOf(horaire[0]);
			m = Integer.valueOf(horaire[1]);
			sec = Integer.valueOf(horaire[2]);
		}
		return new Horaire(jour, h, m, sec); //j,
	}
	
	public static String trouverPosition(String s) {
		String[] rep;
		rep = s.split("%");
		return rep[1];
	}
	
	public String nommerSommet(Station s, Horaire h) {
		return s.toString()+"%"+s.getPosition().getX()+";"+s.getPosition().getY()+"%"+h.toString();
	}
	
	public String nommerSommet(String s, Horaire h, String position) {
		return s+"%"+position+"%"+h.toString();
	}
	
	public String nommerSommet(String s, Horaire h, float x, float y) {
		return s+"%"+x+";"+y+"%"+h.toString();
	}
	
	/*
	 * Fonction d'ajout d'un arc valué.
	 * Utile à la fois à la création du graphe et lors de la résolution du plus court chemin
	 */
	
	private ArcTrajet addWeightedEdge(String v1, String v2, double weight, String vehicule, String nom) {
		ArcTrajet arc = new ArcTrajet(vehicule, denommer(v1), denommer(v2), nom);
		if(!g.containsVertex(v1)) {
			g.addVertex(v1);
		}
		if(!g.containsVertex(v2)) {
			g.addVertex(v2);
		}
		if(!v1.contains(v2)) {
			g.addEdge(v1, v2, arc);
			g.setEdgeWeight(v1, v2, weight);
		}
		return arc;
	}
	
	private void ajouterDepart(String from, Horaire h, GrapheTrajet gr) {
		ArcTrajet arc = new ArcTrajet("Pied", "Depart", "Arrivee", "Nom");
		double best=0;
		Iterator<String> i = gr.g.vertexSet().iterator();
		String vertex;
		while(i.hasNext()) {
			vertex = i.next();
			if(denommer(vertex).equals(from)&&h.estAvant(trouverHoraire(vertex))) {
				if(best==0||h.tempsEntre(trouverHoraire(vertex))<best) {
					if(best!=0) {
						gr.g.removeEdge(arc);
					}
					best = h.tempsEntre(trouverHoraire(vertex));
					arc = gr.addWeightedEdge("depart", vertex, 0, Ligne.ATTENTE, "attente");
				}		
			}
		}
	}
	
	private void ajouterArrivee(String to, GrapheTrajet gr) {
		Iterator<String> i = gr.g.vertexSet().iterator();
		String vertex;
		while(i.hasNext()) {
			vertex = i.next();
			if(denommer(vertex).equals(to)) {
				gr.addWeightedEdge(vertex, "arrivee", 0, Ligne.ATTENTE, "attente");
			}
		}
	}

	private void retirerArcsDepartArrivee(GrapheTrajet gr) {
		Set<ArcTrajet> dep = gr.g.edgesOf("depart");
		Set<ArcTrajet> arr = gr.g.edgesOf("arrivee");
		
		Iterator<ArcTrajet> i = dep.iterator();
		Iterator<ArcTrajet> j = arr.iterator();
		
		ArcTrajet arc;
		
		while(i.hasNext()) {
			arc = i.next();
			if(arc.getSourceT().equals("depart")) {
				gr.g.removeEdge(arc);
			}
		}
		
		while(j.hasNext()) {
			arc = j.next();
			if(arc.getTargetT().equals("arrivee")) {
				gr.g.removeEdge(arc);
			}
		}
	}
	
	/*
	 * Algos de résolution (A* et Dijkstra
	 */
	
	
	public List<ArcTrajet> astar(String from, String to, Horaire h) {
		
		GrapheTrajet gr = filtrerGraphe();
		
		ajouterDepart(from, h, gr);
		ajouterArrivee(to, gr);
		
		AStarShortestPath<String, ArcTrajet> astar = new AStarShortestPath<String, ArcTrajet>(gr.g, new ALTAdmissibleHeuristic<String, ArcTrajet>(gr.g,gr.g.vertexSet()));
		
		GraphPath<String, ArcTrajet> itineraire = astar.getPath("depart", "arrivee");
        
        retirerArcsDepartArrivee(gr);
        try {
        	System.out.println("Shortest Path : "+itineraire.getEdgeList());
            System.out.println("Weight of this path : "+itineraire.getWeight());
            System.out.println("Number of means of transportation used : "+(itineraire.getLength()-2));
        	return itineraire.getEdgeList();
        }catch(java.lang.NullPointerException e){
        	System.out.println("Aucun itineraire correspondant.");
        	ArrayList<ArcTrajet> liste = new ArrayList<ArcTrajet>();
        	return liste;
        }
	}

	public List<ArcTrajet> dijkstra(String from, String to, Horaire h) {
		GrapheTrajet gr = filtrerGraphe();
		
		ajouterDepart(from, h, gr);
		ajouterArrivee(to, gr);
		
		DijkstraShortestPath<String, ArcTrajet> dijkstra = new DijkstraShortestPath<String, ArcTrajet>(gr.g);
		
		GraphPath<String, ArcTrajet> itineraire = dijkstra.getPath("depart", "arrivee");
		
        retirerArcsDepartArrivee(gr);
        try {
        	System.out.println("Shortest Path : "+itineraire.getEdgeList());
            System.out.println("Weight of this path : "+itineraire.getWeight());
            System.out.println("Number of means of transportation used : "+(itineraire.getLength()-2));
        	return itineraire.getEdgeList();
        }catch(java.lang.NullPointerException e){
        	System.out.println("Aucun itineraire correspondant.");
        	ArrayList<ArcTrajet> liste = new ArrayList<ArcTrajet>();
        	return liste;
        }
	}
	
	/*
	 * Fonctionnalitées suppélementaires : Arriver A
	 */
	
	private void ajouterDepartArriverA(String from, GrapheTrajet gr) {
		Iterator<String> i = gr.g.vertexSet().iterator();
		String vertex;
		while(i.hasNext()) {
			vertex = i.next();
			if(denommer(vertex).equals(from)) {
				gr.addWeightedEdge("depart", vertex, 0, Ligne.ATTENTE, "attente");
			}
		}
	}
	
	private void ajouterArriveeArriverA(String to, Horaire h, GrapheTrajet gr) {
		ArcTrajet arc = new ArcTrajet("Pied", "Depart", "Arrivee", "Nom");
		double best=0;
		Iterator<String> i = gr.g.vertexSet().iterator();
		String vertex;
		while(i.hasNext()) {
			vertex = i.next();
			if(denommer(vertex).equals(to)&&trouverHoraire(vertex).estAvant(h)) {
				if(best==0||trouverHoraire(vertex).tempsEntre(h)<best) {
					if(best!=0) {
						gr.g.removeEdge(arc);
					}
					best = h.tempsEntre(trouverHoraire(vertex));
					arc = gr.addWeightedEdge(vertex, "arrivee", 0, Ligne.ATTENTE, "attente");
				}	
			}
		}
	}
	
	public List<ArcTrajet> dijkstraArriverA(String from, String to, Horaire h) {
		GrapheTrajet gr = filtrerGraphe();
		
		ajouterDepartArriverA(from, gr);
		ajouterArriveeArriverA(to, h, gr);
		
		DijkstraShortestPath<String, ArcTrajet> dijkstra = new DijkstraShortestPath<String, ArcTrajet>(gr.g);
		
		GraphPath<String, ArcTrajet> itineraire = dijkstra.getPath("depart", "arrivee");
		
        retirerArcsDepartArrivee(gr);
        
        try {
        	System.out.println("Shortest Path : "+itineraire.getEdgeList());
            System.out.println("Weight of this path : "+itineraire.getWeight());
            System.out.println("Number of means of transportation used : "+(itineraire.getLength()-2));
        	return itineraire.getEdgeList();
        }catch(java.lang.NullPointerException e){
        	System.out.println("Aucun itineraire correspondant.");
        	ArrayList<ArcTrajet> liste = new ArrayList<ArcTrajet>();
        	return liste;
        }
	}
	
	public List<ArcTrajet> astarArriverA(String from, String to, Horaire h) {
		
		GrapheTrajet gr = filtrerGraphe();
		
		ajouterDepartArriverA(from, gr);
		ajouterArriveeArriverA(to, h, gr);
		
		AStarShortestPath<String, ArcTrajet> astar = new AStarShortestPath<String, ArcTrajet>(gr.g, new ALTAdmissibleHeuristic<String, ArcTrajet>(gr.g,gr.g.vertexSet()));
		
		GraphPath<String, ArcTrajet> itineraire = astar.getPath("depart", "arrivee");
        
        retirerArcsDepartArrivee(gr);
        try {
        	System.out.println("Shortest Path : "+itineraire.getEdgeList());
            System.out.println("Weight of this path : "+itineraire.getWeight());
            System.out.println("Number of means of transportation used : "+(itineraire.getLength()-2));
        	return itineraire.getEdgeList();
        }catch(java.lang.NullPointerException e){
        	System.out.println("Aucun itineraire correspondant.");
        	ArrayList<ArcTrajet> liste = new ArrayList<ArcTrajet>();
        	return liste;
        }
	}

	/*
	 * Fonctionnalitées suppélementaires : éviter tel trajet
	 */

	private GrapheTrajet filtrerGraphe() {
		return filtrerGraphe(this);
	}
	
	private GrapheTrajet filtrerGraphe(String p) {
		return filtrerGraphe(p, this);
	}

	private GrapheTrajet filtrerGraphe(GrapheTrajet g) {
		GrapheTrajet h = new GrapheTrajet();
		String line;
		HashSet<String> tabouLigne = new HashSet<String>();
		HashSet<String> tabouTransport = new HashSet<String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader("src/saves/perturbations.txt"));
			while(in.ready()) {
				line = in.readLine();
				switch(line){
				case "Tram" :
					tabouTransport.add(Ligne.TRAM);
					break;
				case "Marche" :
					tabouTransport.add(Ligne.PIED);
					break;
				case "Bus" :
					tabouTransport.add(Ligne.BUS);
					break;
				case "Attente" :
					tabouTransport.add(Ligne.ATTENTE);
					break;	
				case "Metro" :
					tabouTransport.add(Ligne.METRO);
					break;
				case "Bateau" :
					tabouTransport.add(Ligne.BATEAU);
					break;
				default :
					tabouLigne.add(line);
				}
				
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(tabouLigne.isEmpty()&&tabouTransport.isEmpty()) {
			return this;
		}
		Set<String> sommets = g.g.vertexSet();
		Iterator<String> iterSommets = sommets.iterator();
		String s;
		while(iterSommets.hasNext()) {
			s = iterSommets.next();
			h.g.addVertex(s);
		}
		Iterator<ArcTrajet> iterArcs = g.g.edgeSet().iterator();
		ArcTrajet arc;
		while(iterArcs.hasNext()) {
			arc = iterArcs.next();
			if(!tabouTransport.contains(arc.getTransportString())&&!tabouLigne.contains(arc.getNom())) {
				h.addWeightedEdge((String)arc.getSourceT(), (String)arc.getTargetT(), (int)arc.getWeightT(), arc.getTransportString(), arc.getNom());
				//System.out.println((String)arc.getSourceT()+" "+(String)arc.getTargetT()+" "+(int)arc.getWeightT()+" "+arc.getTransport()+" "+arc.getNom());
			}
		}
		return h;
	}
	
	private GrapheTrajet filtrerGraphe(String penalite, GrapheTrajet g) {
		GrapheTrajet h = new GrapheTrajet();
		String line;
		HashSet<String> tabouLigne = new HashSet<String>();
		HashSet<String> tabouTransport = new HashSet<String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader("src/saves/perturbations.txt"));
			while(in.ready()) {
				line = in.readLine();
				switch(line){
				case "Tram" :
					tabouTransport.add(Ligne.TRAM);
					break;
				case "Marche" :
					tabouTransport.add(Ligne.PIED);
					break;
				case "Bus" :
					tabouTransport.add(Ligne.BUS);
					break;
				case "Attente" :
					tabouTransport.add(Ligne.ATTENTE);
					break;	
				case "Metro" :
					tabouTransport.add(Ligne.METRO);
					break;
				case "Bateau" :
					tabouTransport.add(Ligne.BATEAU);
					break;
				default :
					tabouLigne.add(line);
				}
				
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Set<String> sommets = g.g.vertexSet();
		Iterator<String> iterSommets = sommets.iterator();
		String s;
		while(iterSommets.hasNext()) {
			s = iterSommets.next();
			h.g.addVertex(s);
		}
		Iterator<ArcTrajet> iterArcs = g.g.edgeSet().iterator();
		ArcTrajet arc;
		while(iterArcs.hasNext()) {
			arc = iterArcs.next();
			if(!tabouTransport.contains(arc.getTransportString())&&!tabouLigne.contains(arc.getNom())) {
				if(arc.getTransportString().equals(penalite))
					h.addWeightedEdge((String)arc.getSourceT(), (String)arc.getTargetT(), (int)arc.getWeightT()*PENALITE, arc.getTransportString(), arc.getNom());
				else
					h.addWeightedEdge((String)arc.getSourceT(), (String)arc.getTargetT(), (int)arc.getWeightT(), arc.getTransportString(), arc.getNom());
			}
		}
		return h;
	}
	
	/*
	 * Fonctionnalité supplémentaire : pénaliser un moyen de transport
	 */
	
	private List<ArcTrajet> depenaliser(GrapheTrajet gr, List<ArcTrajet> l, String p){
		ArrayList<ArcTrajet> liste = new ArrayList<ArcTrajet>();
		for(ArcTrajet arc : l) {
			if(arc.getTransportString()==p) {
				gr.g.setEdgeWeight(arc, arc.getWeightT()/PENALITE);
			}
			liste.add(arc);
		}
		return liste;
	}
	
	public List<ArcTrajet> astarPenalisant(String from, String to, Horaire h, String penalite) {

		GrapheTrajet gr = filtrerGraphe(penalite);
		
		ajouterDepart(from, h, gr);
		ajouterArrivee(to, gr);
		
		AStarShortestPath<String, ArcTrajet> astar = new AStarShortestPath<String, ArcTrajet>(gr.g, new ALTAdmissibleHeuristic<String, ArcTrajet>(gr.g,gr.g.vertexSet()));
		
		GraphPath<String, ArcTrajet> itineraire = astar.getPath("depart", "arrivee");
        
        retirerArcsDepartArrivee(gr);
        
        List<ArcTrajet> liste;
        
        try {
        	liste = itineraire.getEdgeList();
        	liste = depenaliser(gr, liste, penalite);
        	System.out.println("Shortest Path : "+liste);
        	return liste;
        }catch(java.lang.NullPointerException e){
        	System.out.println("Aucun itineraire correspondant.");
        	liste = new ArrayList<ArcTrajet>();
        	return liste;
        }
	}
	
	public List<ArcTrajet> dijkstraPenalisant(String from, String to, Horaire h, String penalite) {
		GrapheTrajet gr = filtrerGraphe(penalite);
		
		ajouterDepart(from, h, gr);
		ajouterArrivee(to, gr);
		
		DijkstraShortestPath<String, ArcTrajet> dijkstra = new DijkstraShortestPath<String, ArcTrajet>(gr.g);
		
		GraphPath<String, ArcTrajet> itineraire = dijkstra.getPath("depart", "arrivee");
		
        retirerArcsDepartArrivee(gr);
        
        List<ArcTrajet> liste;
        
        try {
        	liste = itineraire.getEdgeList();
        	liste = depenaliser(gr, liste, penalite);
        	System.out.println("Shortest Path : "+liste);
        	return liste;
        }catch(java.lang.NullPointerException e){
        	System.out.println("Aucun itineraire correspondant.");
        	liste = new ArrayList<ArcTrajet>();
        	return liste;
        }
	}
	
	public List<ArcTrajet> dijkstraArriverAPenalisant(String from, String to, Horaire h, String p) {
		GrapheTrajet gr = filtrerGraphe(p);
		
		ajouterDepartArriverA(from, gr);
		ajouterArriveeArriverA(to, h, gr);
		
		DijkstraShortestPath<String, ArcTrajet> dijkstra = new DijkstraShortestPath<String, ArcTrajet>(gr.g);
		
		GraphPath<String, ArcTrajet> itineraire = dijkstra.getPath("depart", "arrivee");
		
        retirerArcsDepartArrivee(gr);
        
        try {
        	System.out.println("Shortest Path : "+itineraire.getEdgeList());
            System.out.println("Weight of this path : "+itineraire.getWeight());
            System.out.println("Number of means of transportation used : "+(itineraire.getLength()-2));
        	return itineraire.getEdgeList();
        }catch(java.lang.NullPointerException e){
        	System.out.println("Aucun itineraire correspondant.");
        	ArrayList<ArcTrajet> liste = new ArrayList<ArcTrajet>();
        	return liste;
        }
	}
	
	public List<ArcTrajet> astarArriverAPenalisant(String from, String to, Horaire h, String p) {
		
		GrapheTrajet gr = filtrerGraphe(p);
		
		ajouterDepartArriverA(from, gr);
		ajouterArriveeArriverA(to, h, gr);
		
		AStarShortestPath<String, ArcTrajet> astar = new AStarShortestPath<String, ArcTrajet>(gr.g, new ALTAdmissibleHeuristic<String, ArcTrajet>(gr.g,gr.g.vertexSet()));
		
		GraphPath<String, ArcTrajet> itineraire = astar.getPath("depart", "arrivee");
        
        retirerArcsDepartArrivee(gr);
        try {
        	System.out.println("Shortest Path : "+itineraire.getEdgeList());
            System.out.println("Weight of this path : "+itineraire.getWeight());
            System.out.println("Number of means of transportation used : "+(itineraire.getLength()-2));
        	return itineraire.getEdgeList();
        }catch(java.lang.NullPointerException e){
        	System.out.println("Aucun itineraire correspondant.");
        	ArrayList<ArcTrajet> liste = new ArrayList<ArcTrajet>();
        	return liste;
        }
	}
	/*
	 * Fonctions de base d'une classe
	 */
	
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
		GrapheTrajet o = (GrapheTrajet) obj;
		if(o.g!=this.g)
			return false;
		return true;
	}
	
}
