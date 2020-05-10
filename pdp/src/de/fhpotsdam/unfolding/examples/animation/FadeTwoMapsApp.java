package de.fhpotsdam.unfolding.examples.animation;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.ALTAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;

import Interface.Other;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

// load map with route
public class FadeTwoMapsApp extends PApplet {

	private static final long serialVersionUID = 9020953886475083992L;
	UnfoldingMap map2; // map to draw
	private SimpleWeightedGraph<String, Edge> g; // graph to solve walking time
	private List<Location> tronconStart; // keep in which section start is
	private int indexOfStart; // position in this section
	private List<Location> tronconEnd;// keep in which section end is
	private int indexOfEnd; // position in this section

	public static SimpleWeightedGraph<String, Edge> loadedGraphe;

	private List<Location> tronconStartAndEndEquals = new ArrayList<Location>();

	private List<Feature> table = new ArrayList<Feature>();

	public void setup() {
		
		table = GeoJSONReader.loadData(this, "src/data1/fv_tronc_l.geojson");
		
		try {
			ObjectInputStream load = new ObjectInputStream(new FileInputStream("src/data1/grapheBx.dat"));
			loadedGraphe = (SimpleWeightedGraph<String, Edge>) load.readObject();
			load.close();
		}
		catch(Exception e) {
			System.out.println("Error " + e.getMessage());
		}
		

		size(800, 800, OPENGL);

		// set the position and size of our map.
		int mapXposition = 0;
		int mapYposition = 30;
		int mapWidth = width;
		int mapHeight = height - mapYposition;
		// set our location of the maps
		float lon = 44.835f;
		float lat = -0.6f;

		// initialize map
		map2 = new UnfoldingMap(this, mapXposition, mapYposition, mapWidth, mapHeight, new Microsoft.AerialProvider());
		map2.zoomAndPanTo(new Location(lon, lat), 12);
		MapUtils.createDefaultEventDispatcher(this, map2);

		// import all lines of the city
		List<Feature> transitLines = GeoJSONReader.loadData(this, "src/data1/MBTARapidTransitLines.json");

		// create marker from features
		List<Marker> transitMarkers = new ArrayList<Marker>();

		for (Feature feature : transitLines) {
			List<Location> line = new ArrayList<Location>();

			// all stations made
			boolean end = true;
			for(int i = 0; i < Other.listNumeroStations.size(); i++) {
				if(Other.listNumeroStations.get(i) != "-1" && Other.listNumeroStations.get(i) != "0") {
					end = false;
				}
			}
			if(end == true) {
				break;
			}

			ShapeFeature lineFeature = (ShapeFeature) feature; 
			String numeroStation = lineFeature.getStringProperty("ROUTE");

			int index = Other.listNumeroStations.indexOf(numeroStation);

			if(index != -1) {

				int j = 0;
				boolean areCloth = false;
				boolean areClothNext = false;

				int index1 = 0;
				int index2 = 0;

				// search the location cloth to the station in lineFeature
				while(j < lineFeature.getLocations().size()-1) {
					areCloth = areCloth(Other.listCoordStations.get(index), lineFeature.getLocations().get(j));
					areClothNext = areCloth(Other.listCoordStations.get(index+1), lineFeature.getLocations().get(j));

					if(index1 != 0 && index2 != 0) {
						break;
					}

					if(areCloth) {
						index1 = j;
					}
					if(areClothNext) {
						index2 = j;
					}

					j++;
				}

				if(index1 != 0 && index2 != 0) { // if a path exists

					if(index1 < index2) {
						for(int i = index1; i <= index2; i++) {
							line.add(lineFeature.getLocations().get(i));
						}
					}
					else {
						for(int i = index2; i <= index1; i++) {
							line.add(lineFeature.getLocations().get(i));
						}
					}

					Other.listNumeroStations.set(index, "-1");

					SimpleLinesMarker m = new SimpleLinesMarker(line);
					m.setStrokeWeight(5);
					m.setColor(255);
					transitMarkers.add(m);

				}
				else {
					line.clear();
				}
			}
		}

		// walking time
		for(int i = 0; i < Other.listStations.size()-1; i++) {
			List<Location> line = new ArrayList<Location>();
			if(Other.listStations.get(i).getTransport() == 2) {
				Location s = new Location(Other.listCoordStations.get(i).getLat(), Other.listCoordStations.get(i).getLon());
				Location t = new Location(Other.listCoordStations.get(i+1).getLat(), Other.listCoordStations.get(i+1).getLon());
				line = astarWalk(s,t);
				SimpleLinesMarker m = new SimpleLinesMarker(line);
				m.setStrokeWeight(5);
				m.setColor(200);
				transitMarkers.add(m);
			}
		}

		// walk all the time
		if(transitMarkers.size() == 0) { 
			List<Location> line = new ArrayList<Location>();
			Location s = new Location(Other.listCoordStations.get(0).getLat(), Other.listCoordStations.get(0).getLon());
			Location t = new Location(Other.listCoordStations.get(1).getLat(), Other.listCoordStations.get(1).getLon());
			line = astarWalk(s,t);
			SimpleLinesMarker m = new SimpleLinesMarker(line);
			m.setStrokeWeight(5);
			m.setColor(200);
			transitMarkers.add(m);
		}

		map2.addMarkers(transitMarkers);
	}

	// solve astar 
	private List<Location> astarWalk(Location s, Location t) {
		List<Location> listEnd = new ArrayList<Location>();

		g = new SimpleWeightedGraph<String, Edge>(Edge.class);
		double sLat = (double) Math.round(s.getLat()*10000000)/10000000;
		double sLon = (double) Math.round(s.getLon()*10000000)/10000000;
		double tLat = (double) Math.round(t.getLat()*10000000)/10000000;
		double tLon = (double) Math.round(t.getLon()*10000000)/10000000;
		String start = sLat + "&" + sLon;
		String end = tLat + "&" + tLon;

		g.addVertex(start); 
		g.addVertex(end);
		loadGraphe(s, t); // load vector and edges which are no far to the middle of the start and the end
		loadEdgeStartAndEnd(s, t); // check in which street are the start and the end 


		if(tronconStartAndEndEquals.size() > 0) {
			listEnd = takeAllLocWithStartAndEndOneTheSameStreet(s, t);
		}
		else {							
			AStarShortestPath<String, Edge> astar = new AStarShortestPath<String, Edge>(g, new ALTAdmissibleHeuristic<String, Edge>(g,g.vertexSet()));

			double sol1 = astar.getPathWeight(start, end);

			if(sol1 < 1500 && sol1 > 0) {
				GraphPath<String, Edge> resEdge = astar.getPath(start, end);

				List<Location> locas = new ArrayList<Location>();

				Iterator<String> it = resEdge.getVertexList().iterator();
				while(it.hasNext()) {
					String a = (String) it.next();
					int index = a.indexOf("&");
					double x = Double.parseDouble(a.substring(0, index));
					double y = Double.parseDouble(a.substring(index+1));
					Location l = new Location(x, y);
					locas.add(l);
				}

				if(locas.size() > 2) {
					listEnd = takeAllLocationsTroncon(locas);
				}
			}
		}
		return listEnd;
	}

	private boolean areCloth(Location location, Location location2) {

		if(Math.abs(location.getLat() - location2.getLat()) <= 0.0001 && Math.abs(location.getLon() - location2.getLon()) <= 0.0001) {
			return true;
		}
		return false;
	}

	public void draw() {
		background(0);

		tint(255);
		map2.draw();

		// Description at the Top
		fill(255);
		String word = " De : " + Other.start + " a : " + Other.end;
		text(word , 10, 20);
		text(" CLiquez sur ECHAP pour quitter", 300, 20);

	}

	public void keyPressed() {
		if(key == 27) {
			this.frame.dispose();
		}
	}

	public static void OneMain() {
		PApplet.main(new String[] { "de.fhpotsdam.unfolding.examples.animation.FadeTwoMapsApp" });
	}

	private void loadGraphe(Location s, Location t) {

		Iterator<String> it = loadedGraphe.vertexSet().iterator();
		String p = "";

		Location m = new Location((double) ((s.getLat() + t.getLat()) / 2), (double) ((s.getLon() + t.getLon()) / 2));
		double ecartX = (double) Math.abs(s.getLat() - t.getLat());
		double ecartY = (double) Math.abs(s.getLon() - t.getLon());

		while(it.hasNext()) {
			p = (String) it.next();

			if(isNotFar(p, m, ecartX, ecartY)) {
				g.addVertex(p);
			}
		}

		Iterator<Edge> it2 = loadedGraphe.edgeSet().iterator();
		while(it2.hasNext()) {
			Edge e = (Edge) it2.next();

			if(g.vertexSet().contains(e.getLocS()) && g.vertexSet().contains(e.getLocT())) {
				g.addEdge(e.getLocS(), e.getLocT(), e);
				g.setEdgeWeight(e.getLocS(), e.getLocT(), e.getWeight());
			}
		}

	}

	private void loadEdgeStartAndEnd(Location s, Location t) {

		boolean toutFiniS = false;
		boolean toutFiniT = false;


		for(Feature feature : table){
			ShapeFeature lineFeature = (ShapeFeature) feature;

			// si s a moins de 700 metres des deux extremites du troncons
			if(toutFiniS == false) {
				if(Math.abs(s.getLat()-lineFeature.getLocations().get(0).getLat()) <= 0.008 && Math.abs(s.getLon()-lineFeature.getLocations().get(0).getLon()) <= 0.008 && Math.abs(s.getLat()-lineFeature.getLocations().get(lineFeature.getLocations().size()-1).getLat()) <= 0.008 && Math.abs(s.getLon()-lineFeature.getLocations().get(lineFeature.getLocations().size()-1).getLon()) <= 0.008) { 
					for(int i = 0; i < lineFeature.getLocations().size()-1; i++) { // on parcourt la liste de locations
						Location loc = lineFeature.getLocations().get(i);
						Location loc1 = lineFeature.getLocations().get(i+1);
						if(isInside(s, loc, loc1)) { // si une location pas loin de start
							indexOfStart = i;

							Location dep = lineFeature.getLocations().get(0);
							Location fin = lineFeature.getLocations().get(lineFeature.getLocations().size()-1);
							List<Location> l1 = new ArrayList<Location>();
							l1.add(s);
							l1.add(dep);
							List<Location> l2 = new ArrayList<Location>();
							l2.add(s);
							l2.add(fin);
							double size1 =  sizeTroncon(l1);
							double size2 = sizeTroncon(l2);
							Edge e1 = new Edge((double) Math.round(dep.getLat()*10000000)/10000000 + "&" + (double) Math.round(dep.getLon()*10000000)/10000000, (double) Math.round(s.getLat()*10000000)/10000000 + "&" + (double) Math.round(s.getLon()*10000000)/10000000, size1);
							Edge e2 = new Edge((double) Math.round(fin.getLat()*10000000)/10000000 + "&" + (double) Math.round(fin.getLon()*10000000)/10000000, (double) Math.round(s.getLat()*10000000)/10000000 + "&" + (double) Math.round(s.getLon()*10000000)/10000000, size2);

							if(e1.getWeight() != 0 && e2.getWeight() != 0) {
								if(g.vertexSet().contains((String) e1.getLocS()) && g.vertexSet().contains((String) e1.getLocT())){
									g.addEdge(e1.getLocS(), e1.getLocT() , e1);
									g.setEdgeWeight(e1.getLocS(), e1.getLocT(), size1);
								}
								if(g.vertexSet().contains((String) e2.getLocS()) && g.vertexSet().contains((String) e2.getLocT())){					
									g.addEdge(e2.getLocS(), e2.getLocT(), e2);
									g.setEdgeWeight(e2.getLocS(), e2.getLocT(), size2);
								}
							}
							tronconStart = new ArrayList<Location>();
							tronconStart.add(dep);
							tronconStart.add(fin);
							toutFiniS = true;
							break;
						}
						if(toutFiniS == true) {break;}
					}
				}
			}

			// si t a moins de 700m des extremites du troncon
			if(toutFiniT == false) {
				if(Math.abs(t.getLat()-lineFeature.getLocations().get(0).getLat()) <= 0.008 && Math.abs(t.getLon()-lineFeature.getLocations().get(0).getLon()) <= 0.008 && Math.abs(t.getLat()-lineFeature.getLocations().get(lineFeature.getLocations().size()-1).getLat()) <= 0.008 && Math.abs(t.getLon()-lineFeature.getLocations().get(lineFeature.getLocations().size()-1).getLon()) <= 0.008) {
					for(int i = 0; i < lineFeature.getLocations().size()-1; i++) {
						Location loc = lineFeature.getLocations().get(i);
						Location loc1 = lineFeature.getLocations().get(i+1);
						if(isInside(t, loc, loc1)) { // si une location pas loin de start
							indexOfEnd = i;

							Location dep = lineFeature.getLocations().get(0);
							Location fin = lineFeature.getLocations().get(lineFeature.getLocations().size()-1);
							List<Location> l1 = new ArrayList<Location>();
							l1.add(t);
							l1.add(dep);
							List<Location> l2 = new ArrayList<Location>();
							l2.add(t);
							l2.add(fin);
							double size1 =  sizeTroncon(l1);
							double size2 = sizeTroncon(l2);
							Edge e1 = new Edge((double) Math.round(dep.getLat()*10000000)/10000000 + "&" + (double) Math.round(dep.getLon()*10000000)/10000000, (double) Math.round(t.getLat()*10000000)/10000000 + "&" + (double) Math.round(t.getLon()*10000000)/10000000, size1);
							Edge e2 = new Edge((double) Math.round(fin.getLat()*10000000)/10000000 + "&" + (double) Math.round(fin.getLon()*10000000)/10000000, (double) Math.round(t.getLat()*10000000)/10000000 + "&" + (double) Math.round(t.getLon()*10000000)/10000000, size2);

							if(e1.getWeight() != 0 && e2.getWeight() != 0) {
								if(g.vertexSet().contains((String) e1.getLocS()) && g.vertexSet().contains((String) e1.getLocT())){
									g.addEdge(e1.getLocS(), e1.getLocT() , e1);
									g.setEdgeWeight(e1.getLocS(), e1.getLocT(), size1);
								}
								if(g.vertexSet().contains((String) e2.getLocS()) && g.vertexSet().contains((String) e2.getLocT())){					
									g.addEdge(e2.getLocS(), e2.getLocT(), e2);
									g.setEdgeWeight(e2.getLocS(), e2.getLocT(), size2);
								}
							}
							tronconEnd = new ArrayList<Location>();
							tronconEnd.add(dep);
							tronconEnd.add(fin);
							toutFiniT = true;
							break;
						}
						if(toutFiniT == true) {break;}
					}
				}	
			}
			if(toutFiniS == true && toutFiniT == true) {
				if(tronconStart.get(0).equals(tronconEnd.get(0)) && tronconStart.get(1).equals(tronconEnd.get(1))) {
					tronconStartAndEndEquals = lineFeature.getLocations(); 
				}
				break;
			}
		}
	}

	private List<Location> takeAllLocationsTroncon(List<Location> locas) {
		List<List<Location>> l = new ArrayList<List<Location>>();
		List<Location> l1 = new ArrayList<Location>();

		for(int i = 0; i < locas.size(); i++) {
			l1 = new ArrayList<Location>();
			l.add(l1);
		}

		for(Feature feature : table) {
			l1 = new ArrayList<Location>();

			ShapeFeature lineFeature = (ShapeFeature) feature;
			int index0 = locas.indexOf(lineFeature.getLocations().get(0));
			int index1 = locas.indexOf(lineFeature.getLocations().get(lineFeature.getLocations().size()-1));			

			if(tronconStart.contains(lineFeature.getLocations().get(0)) && tronconStart.contains(lineFeature.getLocations().get(lineFeature.getLocations().size()-1))) {

				if(index1 == -1) {
					int k = 0;
					while(k <= indexOfStart) {
						l1.add(lineFeature.getLocations().get(k));
						k++;
						if(k+1 == lineFeature.getLocations().size()) {
							break;
						}
					}
				}
				else {
					int k = lineFeature.getLocations().size()-1;
					while(k > indexOfStart) {
						l1.add(lineFeature.getLocations().get(k));
						k--;
						if(k == 0) {
							break;
						}
					}
				}
				l1.add(locas.get(0));
				Collections.reverse(l1);
				index0 = 0;
			}


			else if(tronconEnd.contains(lineFeature.getLocations().get(0)) && tronconEnd.contains(lineFeature.getLocations().get(lineFeature.getLocations().size()-1))) {

				if(index1 == -1) {
					int k = 0;
					while(k <= indexOfEnd) {
						l1.add(lineFeature.getLocations().get(k));
						k++;
						if(k+1 == lineFeature.getLocations().size()) {
							break;
						}
					}
				}
				if(index0 == -1) {
					int k = lineFeature.getLocations().size()-1;
					while(k > indexOfEnd) {
						l1.add(lineFeature.getLocations().get(k));
						k--;
						if(k == 0) {
							break;
						}
					}
				}
				l1.add(locas.get(locas.size()-1));
				index0 = locas.size()-1;
			}


			else if(index0 != -1 && index1 != -1) {
				if(index0 > index1) {					
					Collections.reverse(lineFeature.getLocations());
				}
				l1.addAll(lineFeature.getLocations());
			}

			if(l1.size() > 0) {
				l.set(index0, l1);
			}	
		}

		l1 = new ArrayList<Location>();
		for(int i = 0; i < l.size(); i++) {
			l1.addAll(l.get(i));
		}

		return l1;
	}


	private boolean isNotFar(String p, Location loc, double ecartX, double ecartY) { // teste si p n'est pas loin du milieu de s et t
		int indexP = p.indexOf("&");

		double p1 = Double.parseDouble(p.substring(0, indexP));
		double p2 = Double.parseDouble(p.substring(indexP+1));

		if(Math.abs(p1 - loc.getLat()) <= (double)(ecartX/2 + 0.003) && Math.abs(p2 - loc.getLon()) <= (double) (ecartY/2 + 0.003) ) { // 300 meters around
			return true;
		}

		return false;
	}

	private boolean isInside(Location loc, Location dep, Location fin) { // + or - 6 meters
		double x = loc.getLat();
		double y = loc.getLon();

		if(dep.getLat() <= fin.getLat() && dep.getLon() <= dep.getLon()) { // depart en bas a gauche
			if(x >= dep.getLat()-0.00006 && x <= fin.getLat()+0.00006 && y >= dep.getLon()-0.00006 && y <= fin.getLon()+0.00006) {
				return true;
			}
		}
		else if (dep.getLat() <= fin.getLat() && dep.getLon() > dep.getLon()) { // depart en haut a gauche
			if(x >= dep.getLat()-0.00006 && x <= fin.getLat()+0.00006 && y <= dep.getLon()+0.00006 && y >= fin.getLon()-0.00006) {
				return true;
			}
		}
		else if (dep.getLat() > fin.getLat() && dep.getLon() <= dep.getLon()) { // depart en bas a droite
			if(x <= dep.getLat()+0.00006 && x >= fin.getLat()-0.00006 && y >= dep.getLon()-0.00006 && y <= fin.getLon()+0.00006) {
				return true;
			}
		}
		else if (dep.getLat() > fin.getLat() && dep.getLon() > dep.getLon()) { // depart en haut a droite
			if(x <= dep.getLat()+0.00006 && x >= fin.getLat()-0.00006 && y <= dep.getLon()+0.00006 && y >= fin.getLon()-0.00006) {
				return true;
			}
		}
		return false;
	}

	private double sizeTroncon(List<Location> locations) { // size of the troncon in meters
		double res = 0;
		for(int i = 0; i < locations.size()-1; i++) {
			double x = locations.get(i).getLat();
			double y = locations.get(i).getLon();
			double x1 = locations.get(i+1).getLat();
			double y1 = locations.get(i+1).getLon();

			double hori = Math.abs(x-x1);
			double verti = Math.abs(y-y1);

			res += Math.sqrt(hori*hori + verti* verti);

		}
		return 7.89*res/0.0001; // 7.89 meter <=> 0.0001 coordinates (https://fr.wikipedia.org/wiki/Coordonn%C3%A9es_g%C3%A9ographiques)
	}

	private List<Location> takeAllLocWithStartAndEndOneTheSameStreet(Location s, Location t) {
		List<Location> l = new ArrayList<Location>();

		if(indexOfStart <= indexOfEnd) {
			l.add(s);
			for(int i = indexOfStart+1; i < indexOfEnd; i++) {
				l.add(tronconStartAndEndEquals.get(i));
			}
			l.add(t);
		}
		else {
			l.add(t);
			for(int i = indexOfEnd+1; i < indexOfStart; i++) {
				l.add(tronconStartAndEndEquals.get(i));
			}
			l.add(s);
		}

		tronconStartAndEndEquals = new ArrayList<Location>();
		return l;
	}


	public void saveGraphe() {
		g = new SimpleWeightedGraph<String, Edge>(Edge.class);
		Set<String> set = new HashSet<String>();

		try {
			ObjectInputStream load = new ObjectInputStream(new FileInputStream("src/data1/vectors.dat"));
			String vector  = (String) load.readObject();
			while(!vector.equals(";")) {
				if(!this.g.containsVertex(vector)) {
					set.add(vector);
					g.addVertex(vector);
				}
				vector = (String) load.readObject();
			}
			load.close();
		}
		catch(Exception e) {
			System.out.println("Error : " + e.getMessage());
		}

		try {
			ObjectInputStream load = new ObjectInputStream(new FileInputStream("src/data1/edges.dat"));
			Edge edge = (Edge) load.readObject();
			int cpt = 0;
			while(!edge.getLocS().equals(";")) {

				if(!g.edgeSet().contains(edge) && cpt != 37073 && cpt != 24188) { // loops with 37073 object
					g.addEdge(edge.getLocS(), edge.getLocT(), edge);
					g.setEdgeWeight(edge.getLocS().toString(), edge.getLocT().toString(), edge.getWeight());
				}
				System.out.println(cpt++);

				edge = (Edge) load.readObject();
			}
			load.close();
		}
		catch(Exception e) {
			System.out.println("Error : " + e.getMessage());
		}

		try {
			ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/data1/grapheBx.dat"));
			save.writeObject(g);
			save.close();
		}
		catch(Exception e ) {
			System.out.println("Error : " + e.getMessage());
		}
	}

	public void saveSommetsArcs() { 
		try { 
			ObjectOutputStream load = new ObjectOutputStream(new FileOutputStream("src/data1/vectors.dat"));
			ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/data1/edges.dat"));

			Set<String> tab = new HashSet<String>();
			int cpt = 0;

			for (Feature feature : table) { // parcours de tous les troncons
				ShapeFeature lineFeature = (ShapeFeature) feature;
				System.out.println(cpt++);

				double p1 = (double) Math.round(lineFeature.getLocations().get(0).getLat() * 10000000) / 10000000;
				double p2 = (double) Math.round(lineFeature.getLocations().get(0).getLon() * 10000000) / 10000000;
				double p3 = (double) Math.round(lineFeature.getLocations().get(lineFeature.getLocations().size()-1).getLat() * 10000000) / 10000000;
				double p4 = (double) Math.round(lineFeature.getLocations().get(lineFeature.getLocations().size()-1).getLon() * 10000000) / 10000000;

				String s1 = Double.toString(p1);
				String s2 = Double.toString(p2);
				String s3 = Double.toString(p3);
				String s4 = Double.toString(p4);

				double size = sizeTroncon(lineFeature.getLocations());
				Edge e = new Edge(s1 + "&" + s2, s3 + "&" + s4, size);

				tab.add(s1 + "&" + s2);
				tab.add(s3 + "&" + s4);

				System.out.println(e);
				e.setSize(size);
				save.writeObject(e);
			}
			Edge e1 = new Edge(";");
			save.writeObject(e1);
			save.close();

			Iterator<String> it = tab.iterator();
			String s = "";
			while(it.hasNext()) {
				s = it.next();
				load.writeObject(s);
			}
			s = ";";
			load.writeObject(s);
			load.close();
		}
		catch (Exception e) {
			System.out.println("Error : " + e.getMessage());
		}
	}

	private void saveEdgeWalk() { // a partir des coordonnees des stations on forme les aretes entre deux stations de lignes differentes et temps de marche

		List<Feature> tab = GeoJSONReader.loadData(this, "src/data1/sv_arret_p.geojson");

		List<Location> list = new ArrayList<Location>();
		List<String> listStationName = new ArrayList<String>();

		try {
			ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/data1/coordonneesStations.dat"));
			for(Feature feature : tab) {
				PointFeature lineFeature = (PointFeature) feature;

				Location l = lineFeature.getLocation();
				list.add(l);
				String s = (double) Math.round(l.getLat()*10000000)/10000000 + "&" + (double) Math.round(l.getLon()*10000000)/10000000;
				save.writeObject(s);

				String str = lineFeature.getStringProperty("libelle");
				listStationName.add(str);
			}
			save.close();
		}
		catch(Exception e) {
			System.out.println("error : " + e.getMessage());
		}

		List<Edge> listEdge = new ArrayList<Edge>();
		int cpt = 0; 

		int cptFin = tab.size()-1; 

		for(int i = cpt; i < cptFin; i++) {
			System.out.println(i);
			Location s = list.get(i);

			for(int j = i+1; j < tab.size(); j++) {
				Location t = list.get(j);

				g = new SimpleWeightedGraph<String, Edge>(Edge.class);

				double sLat = (double) Math.round(s.getLat()*10000000)/10000000;
				double sLon = (double) Math.round(s.getLon()*10000000)/10000000;
				double tLat = (double) Math.round(t.getLat()*10000000)/10000000;
				double tLon = (double) Math.round(t.getLon()*10000000)/10000000;

				if(Math.abs(sLat - tLat) <= 0.01 && Math.abs(sLon - tLon) <= 0.01) { // a moins de 789 metres

					String start = sLat + "&" + sLon;
					String end = tLat + "&" + tLon;

					g.addVertex(start);
					g.addVertex(end);

					tronconStartAndEndEquals = new ArrayList<Location>();

					loadGraphe(s, t); 
					loadEdgeStartAndEnd(s, t);

					List<Location> listEnd = new ArrayList<Location>();

					if(tronconStartAndEndEquals.size() > 0) {
						listEnd = takeAllLocWithStartAndEndOneTheSameStreet(s, t);

						double sol =  sizeTroncon(listEnd);

						//System.out.println("Meme rue : " + sol);
						if(sol > 0 && sol <= 500) {
							Edge e = new Edge(listStationName.get(i), listStationName.get(j), sol, listEnd);
							listEdge.add(e);
						}

					}
					else {							
						AStarShortestPath<String, Edge> astar = new AStarShortestPath<String, Edge>(g, new ALTAdmissibleHeuristic<String, Edge>(g,g.vertexSet()));

						double sol = astar.getPathWeight(start, end);

						if(sol < 1500 && sol > 0) {
							GraphPath<String, Edge> resEdge = astar.getPath(start, end);
							List<Location> locas = new ArrayList<Location>();

							Iterator<String> it = resEdge.getVertexList().iterator();
							while(it.hasNext()) {
								String a = (String) it.next();
								int index = a.indexOf("&");
								double x = Double.parseDouble(a.substring(0, index));
								double y = Double.parseDouble(a.substring(index+1));
								Location l = new Location(x, y);
								locas.add(l);
							}

							if(locas.size() > 2) {
								listEnd = takeAllLocationsTroncon(locas);
								sol = sizeTroncon(listEnd);
							}
							//System.out.println(sol);
							if(sol > 0) { 
								Edge e = new Edge(listStationName.get(i), listStationName.get(j), sol, listEnd);
								listEdge.add(e);
							}
						}
					}
				}
			}
		}

		try {
			ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/data1/arcsEntreStationsAPied.dat"));
			save.writeObject(listEdge);
			save.close();
		}
		catch(Exception e) {
			System.out.println("error : " + e.getMessage());
		}

	}

}
