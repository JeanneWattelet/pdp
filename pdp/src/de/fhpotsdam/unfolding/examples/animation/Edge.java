package de.fhpotsdam.unfolding.examples.animation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.geo.Location;

public class Edge extends org.jgrapht.graph.DefaultWeightedEdge implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -594602125881697035L;
	private String locS;
	private String locT;
	private double weight;
	private List<Location> trajet;
	
	public Edge(String s, String t, double e) {
		super();
		this.locS = s;
		this.locT = t;
		this.weight = e;
		trajet = new ArrayList<Location>();
	}
	
	public Edge() {
		super();
		this.locS = "";
		this.locT = "";
		this.weight = 0;
		trajet = new ArrayList<Location>();
	}

	public Edge(Location loc) {
		super();
		locS = "" + loc;
		trajet = new ArrayList<Location>();
	}

	public Edge(String string) {
		super();
		trajet = new ArrayList<Location>();
		this.locS = string;
	}

	public Edge(String s, String t, double e, List<Location> listEnd) {
		super();
		trajet = new ArrayList<Location>();
		this.locS = s;
		this.locT = t;
		this.weight = e;
		trajet = new ArrayList<Location>();
		trajet.addAll(listEnd);
	}

	public String getLocS() {
		return locS;
	}
	
	public String getLocT() {
		return locT;
	}
	
	public List<Location> getTrajet(){
		return trajet;
	}
	
	public double getWeight() {
		return weight;
	}

	public void set(Location loc, int j) {
		if(j == 1) {
			locS = "" + loc;
		}
		else {
			locT = "" + loc;
		}
	}
	
	public boolean equals(Edge e) {
		if(e.getLocS() == this.locS && e.getLocT() == this.locT) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		return b.append(locS).append(" ").append(locT).append(" ").append(weight).toString();	
	}
	
	public String str() {
		return locS + " " + locT + " " + weight;
	}

	public void setSize(double w) {
		this.weight = w;
	}
	
	
	
	
	
	
	
	
	
}
