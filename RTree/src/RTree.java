/**
 * This contains the code for a simple 2D-Point RTree. The tree is fully functional
 * and searches in log N time. This current version adds elements at around Nlog N time
 * (This can be improved upon in later versions). This version lacks a merge function
 * that would help with keeping the tree running efficiently. A "search in this radius" function
 * will be added in later versions as well.
 * 
 * @author  Andrew T Harmon
 * @version 30 Dec 16; 
 */
import java.awt.Point;

public class RTree {
	private Node root;
	public static int PAGE_MAX;
	
	public RTree(){
		root = new Node();
		PAGE_MAX = 10; //Default size
	}
	
	public RTree(Point point, Object contents){
		root = new Node();
		PAGE_MAX = 10; //Default size
		add(point, contents);
	}
	
	//Add a new point and its associated contents. NOTE: Will not hold duplicate points, will override previous points and their contents. Contents must not be "null", will cause problems.
	public void add(Point point, Object contents){
		if(point == null || contents == null){
			return;
		}
		
		//Will trigger when first point is added (root node cannot hold a point)
		if(root.children.isEmpty()){
			Node n = new Node(point, contents);
			root.children.addFirst(n);
			root.expand(point);
		}
		else{
			root.add(point, contents);
			root.expand(point);
		}
		
		//Check if root has too many children. If so, grow the tree upwards by creating a new root and splitting the old one
		if(root.children.size() > PAGE_MAX){
			Node n = new Node();
			n.children.add(root);
			root = n;
			root.split(0);
			root.resize();
		}
	}
	
	//Searches for given point. If found, return true.
	public boolean contains(Point p){
		if(p == null ){
			return false;
		}
		return root.contains(p);
	}
	
	//Searches for given point and removes it if found. Returns the associated contents if found, returns null otherwise
	public Object remove(Point p){
		if(p == null ){
			return null;
		}
		return root.remove(p);
	}
	
	//Searches for given point and returns associated contents. Returns null if point not found.
	public Object get(Point p){
		if(p == null ){
			return null;
		}
		return root.get(p);
	}
	
	//Prints a visual representation of the current tree in the console
	public void print(){
		root.print(0);
	}
	
	//Changes the maximum number of nodes per page (page refers to the number of children each node can have. For example, a binary tree has a maximum of two nodes per page)
	public void setPageMax(int max){
		PAGE_MAX = max;
	}
	
}
