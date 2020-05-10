package de.fhpotsdam.unfolding.examples.animation;

import java.io.Serializable;

import de.fhpotsdam.unfolding.geo.Location;

public class PositionInCity implements Serializable {

	private static final long serialVersionUID = 8880726472998283387L;
	private String string;
	private Location location;

	public PositionInCity(String str, Location loc) {
		string = str;
		location = new Location(loc.getLat(), loc.getLon());
	}

	public String toString() {
		StringBuffer b = new StringBuffer();
		return b.append(string).append(" ").append(location).toString();	
	}
	
	public boolean equals(PositionInCity e) {
		if(string == e.getString() && location == e.getLocation()) {
			return true;
		}
		return false;
	}

	public Location getLocation() {
		return location;
	}

	public String getString() {
		return string;
	}
	
	public int hashCode() {
		return this.hashCode();
	}

}
