import java.awt.Point;

public class MBR {
	//tr = top right point, bl = bottom left point
	private Point tr;
	private Point bl;
	
	//Rectangle can be represented using only two corner points
	public MBR(Point tr, Point bl){
		this.tr = tr;
		this.bl = bl;
	}
	//If MBR contains only a single point, top right and bottom left are equal
	public MBR(Point p){
		tr = p;
		bl = p;
	}
	
	//Set MBR to null by default
	public MBR(){
		tr = null;
		bl = null;
	}
	//Set top right point
	public void settr(Point tr){
		this.tr = tr;
	}
	//Set bottom left point
	public void setbl(Point bl){
		this.bl = bl;
	}
	//Return top left point
	public Point gettr(){
		return tr;
	}
	//Return bottom left point
	public Point getbl(){
		return bl;
	}
	//Compose top left point using tr and bl
	public Point gettl(){
		return new Point(getbl().x,gettr().y);
	}
	//Compose bottom right point using tr and bl 
	public Point getbr(){
		return new Point(gettr().x,getbl().y);
	}
	//Return the shortest distance between a point and the perimeter of this MBR. Return 0 if point lies within MBR
	public double distanceToPoint(Point p){
		//Point is within MBR
		if(containX(p) && containY(p)){
			return 0;
		}
		//Point is in MBR x bounds
		if(containX(p) && !containY(p)){
			//Closest distance is y delta between point and top edge
			if(p.getY() > gettr().getY()){
				return p.getY() - gettr().getY();
			}
			//Closest distance is y delta between point and bottom edge
			return getbl().getY() - p.getY();
		}
		//Point is in MBR y bounds
		if(!containX(p) && containX(p)){
			//Closest distance is x delta between point and right edge
			if(p.getX() > gettr().getX()){
				return p.getX() - gettr().getX();
			}
			//Closest distance is x delta between point and left edge
			return getbl().getX() - p.getX();
		}
		
		//Point is outside of MBR x and y bounds. The closest distance must be from the point to one of MBR's corners
		if((p.getX() > gettr().getX()) && (p.getY() > gettr().getY()))
			return p.distance(gettr());
		
		if((p.getX() < gettl().getX()) && (p.getY() > gettr().getY()))
			return p.distance(gettl());
		
		if((p.getX() > getbr().getX()) && (p.getY() < getbl().getY()))
			return p.distance(getbr());
		//bottom left quadrant
		
		return p.distance(getbl());
		
	}
	//returns true if the point's y value is between the MBR's y bounds
	public boolean containY(Point p){
		if((p.getY() <= tr.getY()) && (p.getY() >= bl.getY()))
			return true;
		return false;
	}
	
	//returns true if the point's x value is between the MBR's x bounds
	public boolean containX(Point p){
		if((p.getX() <= tr.getX()) && (p.getX() >= bl.getX()))
			return true;
		return false;
	}
	
	//Returns the x value of the center of the MBR
	public double midX(){
		if(gettr().getX() == getbl().getX()){
			return gettr().getX();
		}
		return (gettr().getX() - getbl().getY())/2;
	}
	
	//Returns the y value of the center of the MBR
	public double midY(){
		if(gettr().getY() == getbl().getY()){
			return gettr().getY();
		}
		return (gettr().getY() - getbl().getY())/2;
	}
	
	public String toString(){
		if(gettl() == null || gettr() == null){
			return "Null coordinates!";
		}
		
		return "[" + formatPoint(gettl()) + " , " + formatPoint(gettr()) + " , " + formatPoint(getbr()) + " , " + formatPoint(getbl()) + "]";
	}
	
	//Formats the point to be more readable when printed
	public static String formatPoint(Point p){
		return "(" + p.x + "," + p.y + ")";
	}
}