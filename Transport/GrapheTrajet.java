package Transport;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.ALTAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.*;


public class GrapheTrajet implements java.io.Serializable{
	private static final long serialVersionUID = -7760248029679113128L;
	private SimpleDirectedWeightedGraph<String, ArcTrajet> g;

	public GrapheTrajet(List<Ligne> LignesTrajet) throws IOException {
		g = new SimpleDirectedWeightedGraph<String, ArcTrajet>(ArcTrajet.class);
		ajouterSommets(LignesTrajet);
		ajouterAretesDeTransport(LignesTrajet);
		ajouterAretesAttente(LignesTrajet);
		g.addVertex("depart");
		g.addVertex("arrivee");
	}    


	private String nommerSommet(Station s, Horaire h) {
		return s.toString()+"&"+h.toString();
	}

	private String denommer(String s) {
		if(s.contains("&")) {
			int i = s.indexOf('&');
			return s.substring(0, i);
		}
		return s;
	}

	private Horaire trouverHoraire(String s) {

		int j=0,h=0,m=0,sec=0;
		if(s.contains("&")) {
			String[] str = s.split("&");
			String[] horaire = str[1].split(":");
			j = Integer.valueOf(horaire[0]);
			h = Integer.valueOf(horaire[1]);
			m = Integer.valueOf(horaire[2]);
			sec = Integer.valueOf(horaire[3]);
		}
		return new Horaire(j, h, m, sec);
	}


	private void ajouterSommets(List<Ligne> LignesTrajet) {
		String sommet;
		Horaire h;
		for(Ligne l: LignesTrajet) {//pour chaque ligne
			for(Trajet t: l.getTrajets()) {//pour chaque trajet 
				for(Station s: t.getArrets().keySet()) {//et pour chaque station dans la map(staion/horaire) de t
					h= t.getArrets().get(s); //l'horaire de la station s sur le trajet t
					sommet = nommerSommet(s, h);
					//sommet=s.toString()+h.toString();//on cree un sommet au nom unique selon son lieu et son horaire
					if(!this.g.containsVertex(sommet)) {
						this.g.addVertex(sommet);
					}
				}
			}
		}
		System.out.println("Création des sommets terminée");
	}

	private void ajouterAretesDeTransport(List<Ligne> LignesTrajet){
		String sommetA, sommetB;
		Horaire hA, hB;
		Map<Station,Horaire> tmp = new HashMap<Station,Horaire>();
		List<Trajet> tr = new ArrayList<Trajet>();
		Map<Station,Horaire> mp = new HashMap<Station,Horaire>();

		for(Ligne l: LignesTrajet) {//On regarde chaque ligne successivement
			tr = l.getTrajets();
			for(Trajet t: tr) {//Pour chaque trajets de ces lignes
				tmp = t.getArrets();//////
				for(Station stationA: tmp.keySet()){//On regarde chaque station par lequel le trajet passe, qui pourraient Ãªtre une source d'un arc
					hA = tmp.get(stationA);
					mp = t.getArretsAfter(hA);/////
					for(Station stationB: mp.keySet()) {//La station B est la cible de l'arc( 
						hB = tmp.get(stationB);
						//le sens est bien le bon, car getArretAfter(hA) revoie que les arrets qui viennent apres stationA
						//sommetA = stationA.toString()+horaireA.toString();
						sommetA = nommerSommet(stationA, hA);
						//sommetB = stationB.toString()+horaireB.toString();
						sommetB = nommerSommet(stationB, hB);
						addWeightedEdge(sommetA, sommetB, hA.tempsEntre(hB), l.getVehicule());//ajout des sommets		
					}
				}
			}
		}
		System.out.println("Création des arcs de trajet terminée");
	}

	private void ajouterAretesAttente(List<Ligne> LignesTrajet) {
		List<Trajet> trajets = new ArrayList<Trajet>();
		for(Ligne l: LignesTrajet) {
			trajets.addAll(l.getTrajets());
		}
		ajouterAretesAttenteTrajets(trajets);
		System.out.println("Création des arcs d'attente terminée");
	}

	private void ajouterAretesAttenteTrajets(List<Trajet> trajets) {
		String  sommetA, sommetB ;
		Horaire horaireA, horaireB ;

		for(Trajet trajetA: trajets){
			for(Station stationA: trajetA.getArrets().keySet()) {//les stations du trajetA
				for(Trajet trajetB: trajets) {
					for(Station stationB: trajetB.getArrets().keySet()) {
						if(stationA.equals(stationB)) { // == par leurs 'id'
							horaireA = trajetA.getArrets().get(stationA);
							horaireB = trajetB.getArrets().get(stationB);
							if(horaireA.estAvant(horaireB)) { //  horaireA.estAvant(horaireB) renvoie 'false' (donc il n y aura pas d'arc d'attente d'une station vers elle meme) 
								//sommetA = stationA.toString()+horaireA.toString();
								sommetA = nommerSommet(stationA, horaireA);
								//sommetB = stationB.toString()+horaireB.toString();
								sommetB = nommerSommet(stationB, horaireB);
								addWeightedEdge(sommetA, sommetB, horaireA.tempsEntre(horaireB), 5);
							}
						}
					}
				}
			}
		}
	}

	private void addWeightedEdge(String v1, String v2, int weight, int vehicule) {
		if(!v1.contains(v2)) {
			ArcTrajet arc = new ArcTrajet(vehicule, denommer(v1), denommer(v2));
			g.addEdge(v1, v2, arc);
			g.setEdgeWeight(v1, v2, weight);
		}
	}

	private void ajouterDepart(String from, Horaire h) {
		for(String vertex: g.vertexSet()) {
			if(denommer(vertex).equals(from)&&h.estAvant(trouverHoraire(vertex))) {
				addWeightedEdge("depart", vertex, 0, 0);
				System.out.println("Depart ajouté");
			}
		}
	}

	private void ajouterArrivee(String to) {
		for(String vertex: g.vertexSet()) {
			if(denommer(vertex).equals(to)) {
				addWeightedEdge(vertex, "arrivee", 0, 0);
				System.out.println("arrivée ajoutée");
			}
		}
	}

	private void retirerArcsDepartArrivee() {
		Set<ArcTrajet> dep = g.edgesOf("depart");
		Set<ArcTrajet> arr = g.edgesOf("arrivee");

		for(ArcTrajet arc: dep) {
			if(arc.getSourceT().equals("depart")) {
				g.removeEdge(arc); // supprimer tt les arcs qui ont "depart" comme Sommet source 
			}
		}

		for(ArcTrajet arc: arr) {
			if(arc.getTargetT().equals("arrivee")) {
				g.removeEdge(arc); // supprimer tt les arcs qui ont "arrivee" comme Sommet cible(target) 
			}
		}
	}

	public List<ArcTrajet> astar(String from, String to, Horaire h) {

		AStarShortestPath<String, ArcTrajet> astar = new AStarShortestPath<String, ArcTrajet>(this.g, new ALTAdmissibleHeuristic<String, ArcTrajet>(g,g.vertexSet()));

		ajouterDepart(from, h);
		ajouterArrivee(to);

		System.out.println(g.toString());

		GraphPath<String, ArcTrajet> itineraire = astar.getPath("depart", "arrivee");

		System.out.println("/////////////////////// A* ////////////////////////////");
		System.out.println("Shortest Path : "+itineraire.getEdgeList());
		System.out.println("Weight of this path : "+AfficherTempsDeTrajet(itineraire.getWeight()));
		System.out.println("Number of means of transportation used : "+(itineraire.getLength()-2));
		System.out.println("////////////////////////////A* FIN///////////////////////////");

		retirerArcsDepartArrivee();

		return itineraire.getEdgeList();
	}

	public List<ArcTrajet> dijkstra(String from, String to, Horaire h) {
		DijkstraShortestPath<String, ArcTrajet> dijkstra = new DijkstraShortestPath<String, ArcTrajet>(this.g);

		ajouterDepart(from, h);
		ajouterArrivee(to);

		GraphPath<String, ArcTrajet> itineraire = dijkstra.getPath("depart", "arrivee");

		System.out.println("/////////////////////// Dikjstra ////////////////////////////");

		System.out.println("Shortest Path : "+itineraire.getEdgeList());
		System.out.println("Weight of this path : "+AfficherTempsDeTrajet(itineraire.getWeight()));
		System.out.println("Number of means of transportation used : "+(itineraire.getLength()-2));

		System.out.println("/////////////////////// DIKJSTRA FIN////////////////////////////");

		retirerArcsDepartArrivee();

		return itineraire.getEdgeList();
	}

	private String AfficherTempsDeTrajet(double duree) { // afficher la durée du trajet en mode "normal" 
		if( (int)(duree/3600) == 0 ) {
			if( (int) (duree/60) == 0 ) {
				return duree+"(seconde)";
			}else {
				return (int)(duree/60)+"(minute)"+(duree%60)+"seconde";
			}
		}else {
			return (int)(duree/3600)+"(heure)"+AfficherTempsDeTrajet(duree%3600);
		}
	}

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
