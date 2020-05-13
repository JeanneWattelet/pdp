package de.fhpotsdam.unfolding.examples.animation;

import java.io.Serializable;

import de.fhpotsdam.unfolding.geo.Location;

// informations about one stations
public class InfoStation implements Serializable {

	private static final long serialVersionUID = -9060850012796436560L;
	public String name;
	public Location loc;
	public String numeroLigne;
	
	public InfoStation(String n, Location l) {
		name = n;
		loc = new Location(l.getLat(), l.getLon());
	}
	
	public InfoStation(String stat, Location loc2, String numeroStation) {
		name = stat;
		loc = new Location(loc2.getLat(), loc2.getLon());
		numeroLigne = numeroStation;
	}
	
	public boolean equals(InfoStation f) {
		return (name == f.getName()) && (loc.equals(f.getLoc())) && (numeroLigne == f.getNumero());
	}

	public String getNumero() {
		return numeroLigne;
	}

	public String getName() {
		return name;
	}
	public Location getLoc() {
		return loc;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		return b.append(name).append(" ").append(loc).append(" ").append(numeroLigne).toString();	
	}
}
