package de.fhpotsdam.unfolding.examples.animation;

import processing.core.PConstants;
import processing.core.PGraphics;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractMarker;


public class TextMarker extends AbstractMarker {

	String str;

	public TextMarker(Location location, String s) {
		super(location);
		this.str = s;
	}

	@Override
	public void draw(PGraphics pg, float x, float y) {
		pg.pushStyle();
		pg.fill(255, 0, 0);
		pg.textSize(15);
		
		pg.text(str, x - 10 - str.length(), y - 10);
		pg.popStyle();
	}

	@Override
	protected boolean isInside(float checkX, float checkY, float x, float y) {
		return checkX > x && checkX < x && checkY > y && checkY < y;
	}

}
