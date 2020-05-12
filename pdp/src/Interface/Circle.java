package Interface;
import java.io.Serializable;

import javafx.scene.image.Image;

class Circle implements Serializable{


	private static final long serialVersionUID = 1L;
	private double radius;
	private Point center;
	private Image image;

	public Circle(double radius, Point center, Image img){
		this.radius = radius;
		this.center = center;
		this.image = img;
	}

	public Circle(){
		this.radius=0;
		this.center= new Point(0,0);
	}
	
	public Circle(double r, Point c){
		this.radius = r;
		this.center = c;
	}

	public Circle(Circle c){
		this.radius = c.radius;
		this.center = c.center;  
		this.image = c.image;
	}

	public Circle(double r) {
		this.radius = r;
		this.center = new Point(0,0);
	}

	public double getRadius(){
		return this.radius;
	}

	public void setRadius(double radius){
		this.radius = radius;
	}

	public Point getCenter() {
		return center;
	}
	public void setCenter(Point center){
		this.center = center;
	}


	public double getCenterX(){
		return center.getPointX();
	}

	public void setCenterX(double a){
		this.center.setPointX(a);
	}

	public double getCenterY(){
		return center.getPointY();
	}

	public void setCenterY(double a){
		this.center.setPointY(a);
	}

	public void putTo(double a , double b) {
		this.setCenterX(a);
		this.setCenterY(b);
	}

	boolean isInside(Point p){
		return (Point.distance(p , center) <= radius );
	}

	boolean isFar(Circle c1) {
		return (Point.distance(this.center, c1.center)<100+(this.radius + c1.radius));
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==this)
			return true;
		if(obj==null)
			return false;
		if(!(obj instanceof Circle))
			return false;
		Circle o = (Circle) obj;
		if(o.radius!=this.radius)
			return false;
		if(o.center!=this.center)
			return false;
		if(o.image!=this.image)
			return false;
		return true;
	}
	
	public int hashCode() {
		return (int)radius+center.hashCode()+image.hashCode();
	}

	public String toString(){
		return "Circle : "+ center.toString()+" , "+  radius ;
	}


}
