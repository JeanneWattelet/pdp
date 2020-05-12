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
	
	@Override
	public boolean equals(Object obj) {
		if(obj==this)
			return true;
		if(obj==null)
			return false;
		if(!(obj instanceof PositionInCity))
			return false;
		PositionInCity o = (PositionInCity) obj;
		if(o.string!=this.string)
			return false;
		if(o.location!=this.location)
			return false;
		return true;
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
