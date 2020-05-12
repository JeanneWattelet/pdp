package Interface;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Point implements Serializable {

	private static final long serialVersionUID = 1L;
	private double x;
	private double y;


  Point(double x , double y ){
    this.x=x;
    this.y=y;
  }
  
  Point(){
    this.x=0;
    this.y=0;
  }

  public String toString(){
	    return "Point (" + x + ", "  + y + ")";
	  }

public void setPoint (Point p) {
	this.x= p.getPointX();
	this.y= p.getPointY();
}
  public void setPoint(double x, double y){
    this.x = x;
    this.y = y;
  }

  public void setPointX(double x){
    this.x = x;
  }
  
  public void setPointY(double y){
    this.y=y;
  }

  public double getPointX(){
    return x;
  }
  
  public double getPointY(){
    return y;
  } 

  public static boolean equals(Point p1, Point p2) {
	  if (p1.getPointX() == p2.getPointX() && 
			  p1.getPointY()== p2.getPointY()) {
		  return true;
	  }
	  return false;
  }
  
@Override
	public boolean equals(Object obj) {
		if(obj==this)
			return true;
		if(obj==null)
			return false;
		if(!(obj instanceof Point))
			return false;
		Point o = (Point) obj;
		if(o.x!=this.x)
			return false;
		if(o.y!=this.y)
			return false;
		return true;
	}
	
	
  // Calcul la distance entre deux points
  public static double distance(Point p , Point q){
    double d = Math.sqrt(  (p.x-q.x)*(p.x-q.x)  +  (p.y-q.y)*(p.y-q.y)  );
    return d;
  }
 
  public static ArrayList<Point> createRandom(int n, int range){
	  ArrayList<Point> l =  new ArrayList<Point>();
	  for( int i=0; i<n ;i++ ) {
		  Point p=new Point(Math.random() * range ,Math.random() *
				 range);
		  l.add(p);
	  }
	  return l;
  }
  
  public static int removeInsideDisk(List<Point> l, Circle c) {
	  	Iterator<Point> e = l.iterator();
	  	int compteur=0;
	  
	  while(e.hasNext()) {
		  Point p = (Point)e.next();
		  if(c.isInside(p)) {
			  e.remove();
			  compteur++;
		  }
	  }
	  return compteur;
	  
  }

	public void clear() {
		this.x=0;
	    this.y=0;
	} 
}
