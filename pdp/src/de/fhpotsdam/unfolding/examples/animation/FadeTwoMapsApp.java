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
import domain.ArcTrajet;
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
	

	@SuppressWarnings({"deprecation" })
	public void setup() {

		loadSomeData();

		// for each city we have to launch 8 functions independent

		//saveSommetsArcs(); // save vectors/edges from troncon
		//saveGraphe(); System.exit(0); // save graph in file
		//saveEdgeWalk(); System.exit(0); // save all edge between stations
		//saveAllPositionStreet(); System.exit(0); // java heap space : -Xmx5g in VM
		//infoStations(); // save location of each station
		//infoLignes(); System.exit(0);
		//exportNumeroStations(); // export numero of the bus or tram of each station
		//exportListCheminStationsEnTransport(); System.exit(0); // export path between two stations with transport (doesnt work)

		size(600, 600, OPENGL);

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

		if(Other.objectif != 4) {

			// add markers position
			for(int i = 0; i < Other.listNumeroStations.size(); i++) {
				if(Other.listNumeroStations.get(i) != "marche" && Other.listNumeroStations.get(i) != "attente") {
					ImageMarker img = new ImageMarker(Other.listCoordStations.get(i), loadImage("src/imgLogo/" + Other.listNumeroStations.get(i) + ".png"));
					map2.addMarkers(img);
				}
			}
			ImageMarker img = new ImageMarker(Other.listCoordStations.get(Other.listCoordStations.size()-1), loadImage("src/imgLogo/autres/fin.png"));
			map2.addMarkers(img);

			// import all lines of the city
			List<Feature> transitLines = GeoJSONReader.loadData(this, "src/data1/tb_chem_l.geojson");

			List<String> listNumeroClone = new ArrayList<String>();
			listNumeroClone.addAll(Other.listNumeroStations);

			// create marker from features
			List<Marker> transitMarkers = new ArrayList<Marker>();

			for (Feature feature : transitLines) {
				List<Location> line = new ArrayList<Location>();

				// all stations made
				boolean end = true;
				for(int i = 0; i < listNumeroClone.size(); i++) {
					if(listNumeroClone.get(i) != "-1" && listNumeroClone.get(i) != "marche" && listNumeroClone.get(i) != "attente") {
						end = false;
					}
				}
				if(end == true) {
					break;
				}

				ShapeFeature lineFeature = (ShapeFeature) feature; 
				String numeroStation = lineFeature.getStringProperty("nomcomli");

				int index = listNumeroClone.indexOf(numeroStation);

				if(index != -1) {

					int j = 0;
					boolean areCloth = false;
					boolean areClothNext = false;

					int index1 = 0;
					int index2 = 0;

					// search the location cloth to the station in lineFeature
					while(j < lineFeature.getLocations().size()-1) {
						areCloth = isInside(Other.listCoordStations.get(index), lineFeature.getLocations().get(j), lineFeature.getLocations().get(j+1));
						areClothNext = isInside(Other.listCoordStations.get(index+1), lineFeature.getLocations().get(j), lineFeature.getLocations().get(j+1));

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
							line.add(Other.listCoordStations.get(index));
							for(int i = index1+1; i < index2; i++) {
								line.add(lineFeature.getLocations().get(i));
							}
							line.add(Other.listCoordStations.get(index+1));
						}
						else {
							line.add(Other.listCoordStations.get(index+1));
							for(int i = index2+1; i < index1; i++) {
								line.add(lineFeature.getLocations().get(i));
							}
							line.add(Other.listCoordStations.get(index));
						}

						listNumeroClone.set(index, "-1");

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
		else {
			List<Marker> transitMarkers = new ArrayList<Marker>();
			List<Location> line = new ArrayList<Location>();
			Location s = new Location(Other.listCoordStations.get(0).getLat(), Other.listCoordStations.get(0).getLon());
			Location t = new Location(Other.listCoordStations.get(1).getLat(), Other.listCoordStations.get(1).getLon());
			line = astarWalk(s,t);
			SimpleLinesMarker m = new SimpleLinesMarker(line);
			m.setStrokeWeight(5);
			int c = color(255,255,0);
			m.setColor(c);
			transitMarkers.add(m);
			map2.addMarkers(transitMarkers);
			Other.tempsFinal = (int) sizeTroncon(line);
		}
	}

	@SuppressWarnings("unchecked")
	private void loadSomeData() {
		table = GeoJSONReader.loadData(this, "src/data1/fv_tronc_l.geojson");

		try {
			ObjectInputStream load = new ObjectInputStream(new FileInputStream("src/data1/grapheBx.dat"));
			loadedGraphe = (SimpleWeightedGraph<String, Edge>) load.readObject();
			load.close();
		}
		catch(Exception e) {
			System.out.println("Error " + e.getMessage());
		}
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

		loadedGraphe.addVertex(start); 
		loadedGraphe.addVertex(end);

		loadGraphe(s, t); // load vector and edges which are no far to the middle of the start and the end
		loadEdgeStartAndEnd(s, t); // check in which street are the start and the end 

		if(tronconStartAndEndEquals.size() > 0) {
			listEnd = takeAllLocWithStartAndEndOneTheSameStreet(s, t);
		}
		else {							
			AStarShortestPath<String, Edge> astar = new AStarShortestPath<String, Edge>(g, new ALTAdmissibleHeuristic<String, Edge>(g,g.vertexSet()));

			double sol1 = astar.getPathWeight(start, end);

			if(sol1 < 10000 && sol1 > 0) {
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

	public void draw() {
		background(0);

		tint(255);
		map2.draw();

		// Description at the Top
		fill(255);
		String word = " De : " + Other.start + " a : " + Other.end + " Temps : " + Other.tempsFinal + "m";
		text(word , 10, 20);
		text(" Quitter : ECHAP", (float) (0.8*this.getSize().width), 20);

	}

	// echap to leave the page
	public void keyPressed() {
		if(key == 27) {
			this.frame.dispose();
		}
	}

	// main function
	public static void OneMain() {
		PApplet.main(new String[] { "de.fhpotsdam.unfolding.examples.animation.FadeTwoMapsApp" });
	}

	// load graph by reducing vector and edge with start and end location
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

	// search in which section start and end position are
	private void loadEdgeStartAndEnd(Location s, Location t) {

		boolean toutFiniS = false;
		boolean toutFiniT = false;

		for(Feature feature : table){
			ShapeFeature lineFeature = (ShapeFeature) feature;

			// if start is less than 700 meters from both ends of the section 
			if(toutFiniS == false) {
				if(Math.abs(s.getLat()-lineFeature.getLocations().get(0).getLat()) <= 0.008 && Math.abs(s.getLon()-lineFeature.getLocations().get(0).getLon()) <= 0.008 && Math.abs(s.getLat()-lineFeature.getLocations().get(lineFeature.getLocations().size()-1).getLat()) <= 0.008 && Math.abs(s.getLon()-lineFeature.getLocations().get(lineFeature.getLocations().size()-1).getLon()) <= 0.008) { 
					for(int i = 0; i < lineFeature.getLocations().size()-1; i++) { // browse the locations list
						Location loc = lineFeature.getLocations().get(i);
						Location loc1 = lineFeature.getLocations().get(i+1);
						if(isInside(s, loc, loc1)) { // if start is inside a section
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

			// if end is less than 700 meters from both ends of the section 
			if(toutFiniT == false) {
				if(Math.abs(t.getLat()-lineFeature.getLocations().get(0).getLat()) <= 0.008 && Math.abs(t.getLon()-lineFeature.getLocations().get(0).getLon()) <= 0.008 && Math.abs(t.getLat()-lineFeature.getLocations().get(lineFeature.getLocations().size()-1).getLat()) <= 0.008 && Math.abs(t.getLon()-lineFeature.getLocations().get(lineFeature.getLocations().size()-1).getLon()) <= 0.008) {
					for(int i = 0; i < lineFeature.getLocations().size()-1; i++) {
						Location loc = lineFeature.getLocations().get(i);
						Location loc1 = lineFeature.getLocations().get(i+1);
						if(isInside(t, loc, loc1)) {
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

	// search the real path with several locations
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


	// if a location is inside an interval
	private boolean isNotFar(String p, Location loc, double ecartX, double ecartY) {
		int indexP = p.indexOf("&");

		double p1 = Double.parseDouble(p.substring(0, indexP));
		double p2 = Double.parseDouble(p.substring(indexP+1)); 

		if(Math.abs(p1 - loc.getLat()) <= (double)(ecartX/2) + 0.01 && Math.abs(p2 - loc.getLon()) <= (double)(ecartY/2) + 0.01) {
			return true;
		}
		return false;
	}

	// if a location is inside a section
	private boolean isInside(Location loc, Location dep, Location fin) { // + or - 1 meters
		double x = loc.getLat();
		double y = loc.getLon();

		if(dep.getLat() <= fin.getLat() && dep.getLon() <= fin.getLon()) { // start at the bottom left of end 
			if(x >= dep.getLat()-0.000015 && x <= fin.getLat()+0.000015 && y >= dep.getLon()-0.000015 && y <= fin.getLon()+0.000015) {
				return true;
			}
		}
		else if (dep.getLat() <= fin.getLat() && dep.getLon() > fin.getLon()) { // start at the top left of end
			if(x >= dep.getLat()-0.000015 && x <= fin.getLat()+0.000015 && y <= dep.getLon()+0.000015 && y >= fin.getLon()-0.000015) {
				return true;
			}
		}
		else if (dep.getLat() > fin.getLat() && dep.getLon() <= fin.getLon()) { // start at the bottom right of end 
			if(x <= dep.getLat()+0.000015 && x >= fin.getLat()-0.000015 && y >= dep.getLon()-0.000015 && y <= fin.getLon()+0.000015) {
				return true;
			}
		}
		else if (dep.getLat() > fin.getLat() && dep.getLon() > fin.getLon()) { // start at the top right of end 
			if(x <= dep.getLat()+0.000015 && x >= fin.getLat()-0.000015 && y <= dep.getLon()+0.000015 && y >= fin.getLon()-0.000015) {
				return true;
			}
		}
		return false;
	}


	// find the real size of a section
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
		return 7.89*res/0.0001; // 7.89 meters <=> 0.0001 coordinates in Bordeaux (https://fr.wikipedia.org/wiki/Coordonn%C3%A9es_g%C3%A9ographiques)
	}

	// find path if start and end are in the same section
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


	// save walking graph
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

				if(!g.edgeSet().contains(edge) && cpt != 37073 && cpt != 24188) { // loops with 37073 and 24188 object (Bordeaux)
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

	// save vector and edge for walking graph
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

	// with coordinates station, we form walking edges between two stations
	@SuppressWarnings("unused")
	private void saveEdgeWalk() {

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

	// position of each stations
	@SuppressWarnings("unused")
	private void infoStations() {
		table = GeoJSONReader.loadData(this, "src/data1/sv_arret_p.geojson");

		try {
			ObjectOutputStream load = new ObjectOutputStream(new FileOutputStream("src/data1/infoStations.dat"));
			List<InfoStation> l = new ArrayList<InfoStation>();

			for(int i = 0; i < table.size(); i++) {
				Feature f = table.get(i);
				PointFeature p = (PointFeature) f;

				InfoStation k = new InfoStation(p.getStringProperty("libelle"), p.getLocation());
				l.add(k);
			}
			load.writeObject(l);
			load.close();

		}
		catch(Exception e) {

		}
	}

	// position of each stations with number of the line
	@SuppressWarnings({ "unchecked", "unused" })
	private void exportNumeroStations() {
		List<Feature> transitLines = new ArrayList<Feature>();
		List<InfoStation> listInfo = new ArrayList<InfoStation>();
		Set<InfoStation> listEnd = new HashSet<InfoStation>();


		try {
			transitLines = GeoJSONReader.loadData(this, "src/data1/tb_chem_l.geojson");
			ObjectInputStream load = new ObjectInputStream(new FileInputStream("src/data1/infoStations.dat"));
			listInfo = (List<InfoStation>) load.readObject();
			load.close();
		}
		catch(Exception e) {
			System.out.println("Error : " + e.getMessage());
		}


		for(int i = 0; i < listInfo.size(); i++) {
			String stat = listInfo.get(i).getName();
			Location loc = listInfo.get(i).getLoc();
			System.out.println(i);

			for (Feature feature : transitLines) {

				ShapeFeature lineFeature = (ShapeFeature) feature; 
				String numeroStation = lineFeature.getStringProperty("nomcomli");

				for(int k = 0; k < lineFeature.getLocations().size()-1; k++) {

					if(isInside(loc, lineFeature.getLocations().get(k), lineFeature.getLocations().get(k+1))) {
						InfoStation e = new InfoStation(stat, loc, numeroStation);

						listEnd.add(e);
						break;

					}
				}
			}
		}

		try {
			ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/data1/infoStationsAvecNumeroTransport.dat"));
			save.writeObject(listEnd);
			save.close();
		}
		catch(Exception e) {
			System.out.println("Error : " + e.getMessage());
		}
	}


	// file with path between two stations inside a transport (doesn't work)
	@SuppressWarnings("unchecked")
	public void exportListCheminStationsEnTransport() {

		List<Feature> transitLines = new ArrayList<Feature>();
		Set<InfoStation> setInfo = new HashSet<InfoStation>();

		List<List<Edge>> listEnd = new ArrayList<List<Edge>>();

		List<InfoStation> listInfo = new ArrayList<InfoStation>();
		List<InfoStation> l = new ArrayList<InfoStation>();


		try {
			transitLines = GeoJSONReader.loadData(this, "src/data1/tb_chem_l.geojson");
			ObjectInputStream load = new ObjectInputStream(new FileInputStream("src/data1/infoStationsAvecNumeroTransport.dat"));
			setInfo = (Set<InfoStation>) load.readObject();

			ObjectInputStream load2 = new ObjectInputStream(new FileInputStream("src/data1/infoStations.dat"));
			l = (List<InfoStation>) load2.readObject();

			load.close();
			load2.close();
		}
		catch(Exception e) {
			System.out.println("Error : " + e.getMessage());
		}

		listInfo.addAll(setInfo);

		List<String> listNom = new ArrayList<String>();

		for(int i = 0; i < l.size(); i++) {
			listEnd.add(new ArrayList<Edge>());
			listNom.add(l.get(i).getName());
		}


		for(int i = 0; i < listInfo.size()-1; i++) {
			String stat = listInfo.get(i).getName();
			Location loc = listInfo.get(i).getLoc();
			String num = listInfo.get(i).getNumero();

			for(int j = i+1; j < listInfo.size(); j++) {
				String stat2 = listInfo.get(j).getName();
				Location loc2 = listInfo.get(j).getLoc();
				String num2 = listInfo.get(j).getNumero();

				if(num.equals(num2)) {

					for (Feature feature : transitLines) {

						List<Location> listChemin = new ArrayList<Location>();
						ShapeFeature lineFeature = (ShapeFeature) feature; 
						String numeroStation = lineFeature.getStringProperty("nomcomli");

						if(numeroStation.equals(num)) {

							boolean areCloth = false;
							boolean areClothNext = false;

							int index1 = -1;
							int index2 = -1;

							for(int z = 0; z <= lineFeature.getLocations().size()-1; z++) {

								areCloth = isInside(loc, lineFeature.getLocations().get(z), lineFeature.getLocations().get(z+1));
								areClothNext = isInside(loc2, lineFeature.getLocations().get(z), lineFeature.getLocations().get(z+1));

								if(index1 != 0 && index2 != 0) {
									break;
								}

								if(areCloth) {
									index1 = z;
								}
								if(areClothNext) {
									index2 = z;
								}

							}


							if(index1 != -1 && index2 != -1) { // if a path exists

								if(index1 < index2) {
									listChemin.add(loc);
									for(int r = index1+1; r < index2; r++) {
										listChemin.add(lineFeature.getLocations().get(r));
									}
									listChemin.add(loc2);
								}
								else {
									listChemin.add(loc2);
									for(int r = index2+1; r < index1; r++) {
										listChemin.add(lineFeature.getLocations().get(r));
									}
									listChemin.add(loc);
									Collections.reverse(listChemin);
								}

								Edge e = new Edge(stat, stat2, 0, listChemin);
								Collections.reverse(listChemin);
								Edge e1 = new Edge(stat2, stat, 0, listChemin);

								if(!listEnd.get(listNom.indexOf(stat)).contains(e)) {
									listEnd.get(listNom.indexOf(stat)).add(e);
								}
								if(!listEnd.get(listNom.indexOf(stat2)).contains(e1)) {
									listEnd.get(listNom.indexOf(stat2)).add(e1);
								}
								break;
							}
						}	
					}
				}
			}
		}

		System.out.println(listEnd);

		List<Set<Edge>> listSet = new ArrayList<Set<Edge>>();

		for(int i = 0; i < 3; i++) {	
			Set<Edge> e = new HashSet<Edge>();
			e.addAll(listEnd.get(i));
			listSet.add(e);
			System.out.println(listSet.get(i));
		}
		System.exit(0);

		for(int i = 0; i < listEnd.size(); i++) {	
			System.out.println(i);
			try {
				// problem with name with a /
				String m = listNom.get(i);
				int index = m.indexOf("/");
				if(index != -1) {
					m = m.substring(0, index);
				}

				ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/data1/chemins/cheminEntreStationAvec" + m + ".dat"));
				save.writeObject(listEnd.get(i));
				save.close();
			}
			catch(Exception e) {
				System.out.println("Error : " + e.getMessage());
			}
		}
	}

	// street named -> coordinates (cause limit of gc memory : -Xmx5g)
	private void saveAllPositionStreet() {

		try {
			List<Feature> tab = GeoJSONReader.loadData(this, "src/data1/fv_adresse_p.geojson");
			ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/data1/allPositions.dat"));
			List<PositionInCity> list = new ArrayList<PositionInCity>();
			for(int i = 0; i < tab.size(); i++) {
				System.out.println(i);
				Feature feature = tab.get(i);
				PointFeature p = (PointFeature) feature;
				String str = p.getProperty("numero") + " " + p.getProperty("nom_voie") + p.getProperty("commune");
				Location loc = p.getLocation();
				PositionInCity pos = new PositionInCity(str, loc);
				list.add(pos);
			}
			save.writeObject(list);
			save.close();
		}
		catch(Exception e) {
			System.out.println("Error : " + e.getMessage());
		}
	}

	// all lines
	private void infoLignes() {
		List<String> line = new ArrayList<String>();
		try {
			ObjectInputStream load = new ObjectInputStream(new FileInputStream("src/data1/infoStationsAvecNumeroTransport.dat"));
			@SuppressWarnings("unchecked")
			Set<InfoStation> l = (Set<InfoStation>) load.readObject();	
			Iterator<InfoStation> it = l.iterator();
			while(it.hasNext()) {
				InfoStation e = it.next();
				if(!line.contains(e.getNumero())) {
					line.add(e.getNumero());
				}
			}
			load.close();
		}
		catch(Exception e) {
			System.out.println("Error : " + e.getMessage());
		}

		try {
			ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream("src/data1/infoLignes.dat"));
			save.writeObject(line);
			save.close();
		}
		catch(Exception e) {
			System.out.println("Error : " + e.getMessage());
		}
	}

}
