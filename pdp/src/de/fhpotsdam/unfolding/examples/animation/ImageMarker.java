package de.fhpotsdam.unfolding.examples.animation;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractMarker;


public class ImageMarker extends AbstractMarker {

	PImage img;

	public ImageMarker(Location location, PImage img) {
		super(location);
		this.img = img;
		img.resize(20, 20);
	}

	@Override
	public void draw(PGraphics pg, float x, float y) {
		pg.pushStyle();
		pg.imageMode(PConstants.CORNER);
		pg.image(img, x - 10, y -10);
		pg.popStyle();
	}

	@Override
	protected boolean isInside(float checkX, float checkY, float x, float y) {
		return checkX > x && checkX < x + img.width && checkY > y && checkY < y + img.height;
	}

}