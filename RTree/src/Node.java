import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

public class Node {
	MBR mbr;
	
	private Point point;
	private Object contents;
	
	LinkedList<Node> children;
	
	public Node(){
		point = null;
		contents = null;
		children = new LinkedList<Node>();
		mbr = new MBR();
	}
	
	public Node(Point point, Object contents){
		this.point = point;
		this.contents = contents;
		children = new LinkedList<Node>();
		mbr = new MBR(point);
	}
	
	//Add a new point and its contents
	public void add(Point p, Object con){
		//Check if point already exists. If so, replace existing points contents with new contents
		if(replace(p,con)){
			return;
		}
		
		//Check if children are leaves
		if(children.getFirst().isLeaf()){
			children.add(new Node(p,con));
			expand(p);
		}
		//Must be an interior node with no leaf children
		else{
			int bestIndex = 0;
			double minDistance = children.get(0).mbr.distanceToPoint(p);
			for(int i = 1; i < children.size();i++){
				if(children.get(i).mbr.distanceToPoint(p) < minDistance){
					bestIndex = i;
					minDistance = children.get(i).mbr.distanceToPoint(p);
				}
			}
			children.get(bestIndex).add(p, con);
			expand(p);
			
			if(children.get(bestIndex).children.size() > RTree.PAGE_MAX){
				split(bestIndex);
			}
		}
		
	}
	
	//Return true if given point exists in tree, false otherwise
	public boolean contains(Point p){
		//If leaf node
		if(children.isEmpty()){
			if(p.equals(point)){
				return true;
			}
			return false;
		}
		
		//If interior node
		else{
			boolean found = false;
			
			//Search through children nodes
			for(int i = 0; i < children.size();i++){
				//Only recurse down children that can possibly contain the given point
				if(children.get(i).mbr.distanceToPoint(p) <= 0){
					found = children.get(i).contains(p);
					//If true is returned, then point was found down the tree. Return true
					if(found == true){
						return found;
					}
				}
			}
		}
		return false;
	}
	
	//Searches for a point. If the point is found, replace its contents with new contents
	public boolean replace(Point p, Object con){
			//If leaf node
			if(children.isEmpty()){
				if(p.equals(point)){
					contents = con;
					return true;
				}
				return false;
			}
			
			//If interior node
			else{
				boolean found = false;
				
				//Search through children nodes
				for(int i = 0; i < children.size();i++){
					//Only recurse down children that can possibly contain the given point
					if(children.get(i).mbr.distanceToPoint(p) <= 0){
						found = children.get(i).replace(p,con);
						//If true is returned, then point was found down the tree. Return true
						if(found == true){
							return found;
						}
					}
				}
			}
			return false;
		}
	
		//Searches for given point and removes it if found. Returns the associated contents if found, returns null otherwise
	public Object remove(Point p){
		//If leaf node
		if(children.isEmpty()){
			if(p.equals(point)){
				//Set this as a "dead" node to indicate that it should be deleted.
				Object rContents = contents;
				contents = null;
				point = null;
				return rContents;
			}
			return null;
		}
		//If interior node
		else{
			//Returned contents. Null by default
			Object rContents = null;
			
			//Search through children nodes
			Node n;
			for(int i = 0; i < children.size();i++){
				n = children.get(i);
				//Only recurse down children that can possibly contain the given point
				if(n.mbr.distanceToPoint(p) <= 0){
					rContents = n.remove(p);
					
					//If true is returned, then point was found down the tree. Check to see if child needs to be deleted. Also check to see if mbr needs to be resized
					if(rContents != null){
						//If the targeted node is "dead" after removing point, the targeted node should be removed from this node's children
						if((n.point == null)&&(n.contents == null)&&(n.children.isEmpty())){
							children.remove(i);
							
							//Call shrink using removed point as argument. MBR will only shrink if the removed point was a bounding point (this saves on unnecessary processing)
							shrink(p);
						}
						return rContents;
					}
				}
			}
		}
		return null;
	}
	
	//Returns contents associated with point. Does not remove point or contents. Returns null if point cannot be found in tree
	public Object get(Point p){
				//If leaf node
				if(children.isEmpty()){
					if(p.equals(point)){
						return contents;
					}
					return null;
				}
				//If interior node
				else{
					//Returned contents. Null by default
					Object rContents = null;
					
					//Search through children nodes
					for(int i = 0; i < children.size();i++){
						//Only recurse down children that can possibly contain the given point
						if(children.get(i).mbr.distanceToPoint(p) <= 0){
							rContents = children.get(i).get(p);
							//If true is returned, then point was found down the tree. Return contents
							if(rContents != null){
								return rContents;
							}
						}
					}
				}
				return null;
	}
	
	//Prints tree to console
	public void print(int level){
		for(int i = 0; i < level; i++){
			System.out.print("\t");
		}
		
		if(!children.isEmpty()){
			System.out.println(level + " Node: " + mbr);
			
			for(int i = 0; i < children.size();i++){
				children.get(i).print(level + 1);
			}
		}
		else{
			System.out.println("\tLeaf: " + MBR.formatPoint(point) + " Contents: " + contents);
		}
	}
	
	//Splits a child of this node into two children. Takes the list index of the child to be split as arguments
	public void split(int nodeIndex){
		
		if(children.get(nodeIndex).children.isEmpty()){
			return;
		}
		
		Node n = children.get(nodeIndex);
		Comparator compare;
		Random ran = new Random();
		boolean ranBool;
		
		double xmax = n.children.get(0).mbr.midX();
		double xmin = n.children.get(0).mbr.midX();
		double ymax = n.children.get(0).mbr.midY();
		double ymin = n.children.get(0).mbr.midY();
		
		for(int i = 1; i < n.children.size();i++){
			
			if(n.children.get(i).mbr.midX() > xmax)
				xmax = n.children.get(i).mbr.midX();
			if(n.children.get(i).mbr.midX() < xmin)
				xmin = n.children.get(i).mbr.midX();
			if(n.children.get(i).mbr.midY() > ymax)
				ymax = n.children.get(i).mbr.midY();
			if(n.children.get(i).mbr.midY() < ymin)
				ymin = n.children.get(i).mbr.midY();
		}
		
		//Split by x values if x-delta is larger than y-delta
		if((xmax-xmin) > (ymax-ymin)){
			compare = new Comparator<Node>(){
				@Override
				public int compare(Node arg0, Node arg1) {
					Node n0 = arg0;
					Node n1 = arg1;
					if(n0.mbr.midX() > n1.mbr.midX())
						return 1;
					if(n0.mbr.midX() < n1.mbr.midX())
						return -1;
					else
						return 0;
				}
			};
		}
		//Split by y value if y-delta is larger than x-delta
		else if((xmax-xmin) < (ymax-ymin)){
			compare = new Comparator<Node>(){
				@Override
				public int compare(Node arg0, Node arg1) {
					Node n0 = arg0;
					Node n1 = arg1;
					if(n0.mbr.midY() > n1.mbr.midY())
						return 1;
					if(n0.mbr.midY() < n1.mbr.midY())
						return -1;
					else
						return 0;
				}
			};
		}
		//Randomly pick a split if both delta's are the same
		else{
			ranBool = ran.nextBoolean();
			if(ranBool){
				compare = new Comparator<Node>(){
					@Override
					public int compare(Node arg0, Node arg1) {
						Node n0 = arg0;
						Node n1 = arg1;
						if(n0.mbr.midX() > n1.mbr.midX())
							return 1;
						if(n0.mbr.midX() < n1.mbr.midX())
							return -1;
						else
							return 0;
					}
				};
			}
			else{
				compare = new Comparator<Node>(){
					@Override
					public int compare(Node arg0, Node arg1) {
						Node n0 = arg0;
						Node n1 = arg1;
						if(n0.mbr.midY() > n1.mbr.midY())
							return 1;
						if(n0.mbr.midY() < n1.mbr.midY())
							return -1;
						else
							return 0;
					}
				};
			}
		}
		
		Collections.sort(n.children,compare);
		
		ranBool = ran.nextBoolean();
		
		int splitIndex;
		
		if(ranBool){
			splitIndex = n.children.size() / 2;

		}else{
			splitIndex = n.children.size() - (n.children.size() / 2);
		}
		
		LinkedList<Node> list = new LinkedList<Node>();
		while(splitIndex > 0){
			list.addFirst(n.children.removeFirst());
			--splitIndex;
		}
		
		//Create a new node to be added to children
		n.resize();
		n = new Node();
		n.children = list;
		n.resize();
		children.addFirst(n);
	}
	
	//Resize this node's mbr based on children. Returns true if bounds are resized
	public void resize(){
		
		
		int xmax = children.get(0).mbr.gettr().x;
		int xmin = children.get(0).mbr.getbl().x;
		int ymax = children.get(0).mbr.gettr().y;
		int ymin = children.get(0).mbr.getbl().y;
		
		for(int i = 1; i < children.size();i++){
			
			if(children.get(i).mbr.gettr().x > xmax){
				xmax = children.get(i).mbr.gettr().x;
			}
			
			if(children.get(i).mbr.getbl().x < xmin){
				xmin = children.get(i).mbr.getbl().x;
			}
			
			if(children.get(i).mbr.gettr().y > ymax){
				ymax = children.get(i).mbr.gettr().y;
			}
			
			if(children.get(i).mbr.getbl().y < ymin){
				ymin = children.get(i).mbr.getbl().y;
			}
		}

		mbr.settr(new Point(xmax,ymax));
		mbr.setbl(new Point(xmin,ymin));
	}
	//Expands MBR based on adding a new point. Requires less processing than resize(). Returns true if bounds are expanded
	public boolean expand(Point p){

		if(mbr.gettr() == null || mbr.getbl() == null){
			mbr.settr(p);
			mbr.setbl(p);
			return true;
		}
		//Check if point is already contained
		if(mbr.distanceToPoint(p) == 0){
			return false;
		}
		
		int xmax = mbr.gettr().x;
		int xmin = mbr.getbl().x;
		int ymax = mbr.gettr().y;
		int ymin = mbr.getbl().y;
		
		//Create new corner points (if needed) based on the new points x and y.
		if(p.x > xmax){
			xmax = p.x;
		}
		
		if(p.x < xmin){
			xmin= p.x;
		}
		
		if(p.y > ymax){
			ymax = p.y;
		}
		
		if(p.y < ymin){
			ymin = p.y;
		}
		mbr.settr(new Point(xmax,ymax));
		mbr.setbl(new Point(xmin,ymin));
		
		return true;
	}
	
	//Shrinks MBR based on removing a point. Checks if shrinking is necessary before calling resize(). Returns true if bounds are shrunk
	public boolean shrink(Point p){
		//Only need to shrink mbr if removed point was a boundary point
			if(children.size() == 0){
				mbr.settr(null);
				mbr.setbl(null);
				return false;
			}
		
				if((p.x == mbr.gettr().x) || (p.x == mbr.getbl().x) || (p.y == mbr.gettr().y) || (p.y == mbr.getbl().y)){
					resize();
					return true;
				}
				return false;
	}
	
	//Returns true if the current node has no children
	public boolean isLeaf(){
		if(children.isEmpty()){
			return true;
		}
		return false;
	}
	
	public String toString(){
		return mbr.toString();
	}
}
