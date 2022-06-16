import java.util.ArrayList;
import java.util.Scanner;

/* **  EDIT: I meant to say file in the email, not class. My apologies. It was a long day */

/* Classes: Main, Traverser, Board, Node & ENUM: Direction */
public class Main {
	
	public static void main(String[] args) {
		Board b = new Board();
		b.setupBoard();
		
		System.out.println("Traverse from where?");
		// Take in the original node from user
		Scanner s = new Scanner(System.in);
		// Get the node from board
		Node n = b.getNode(s.nextInt());
		
		// Setup Traverser
		Traverser t = new Traverser(n);
		ArrayList<Integer> p = new ArrayList<Integer>();
		p.add(n.getValue());
		t.traverse(n, null, n.getValue(), p);
		t.print();
	}

}

/**
 * Enum for directionality,
 * Use to convert int to direction
 */
enum Direction {
	RIGHT(0),
	LEFT(1),
	DOWN(2),
	UP(3);
	
	int i;
	Direction(int i)
	{
		this.i = i;
	}
	
	public static Direction toDirection(int i)
	{
		if(i == 0)
			return Direction.RIGHT;
		if(i == 1)
			return Direction.LEFT;
		if(i == 2)
			return Direction.DOWN;
		if(i == 3)
			return Direction.UP;
		
		return null;
	}
}

/* THIS was not my original idea for the code had 2 other attempts at trying doing this 
*  Original Idea is down below at the bottom of the file [Couldn't be used recursively]
*/
class Traverser {
	private ArrayList<ArrayList<Integer>> paths;
	private Node orig;
	
	// Initialize The Traverser class
	public Traverser(Node n) {
		this.orig = n;
		paths = new ArrayList<ArrayList<Integer>>();
	}
	
	/**
	 * Ended up using this as the method.
	 * 		Once the function/class was structured for recursion, it was finished rather quickly
	 * 
	 * @param node - node to be analyzed
	 * @param prev - previous node not actually needed since we just cross checked the path, but originally I did prev 
	 * @param sum - sum of all elements
	 * @param path - path taken to reach current position
	 * @return
	 */
	public void traverse(Node node, Node prev, int sum, ArrayList<Integer> path) {
		
		// For every direction at node check all neighboring nodes
		for(int i = 0; i < 4; i++) {
			/* Convert integer to direction
			* Get the node at the desired direction
			* If Node is null its a boundary -> go next
			*/
			Direction d = Direction.toDirection(i);
			Node n = node.getNode(d);
			if(n == null)
				continue;
			
			/* If the node is equal to previous (DEPRECATED) or if the path contains any of the previous nodes ( need to check past nodes too )
			* 		THEN we move on
			*/
			if(n.equals(prev) || path.contains(n.getValue()))
				continue;
			
			// if the value is MORE than 20, we end that process and back up to see if there's anything else we can work with
			if(sum + n.getValue() > 20)
				continue;
			
			// If its equal to 20 we add current path to successful paths and move on 
			ArrayList<Integer> p = new ArrayList<Integer>(path);
			if(sum + n.getValue() == 20) {
				path.add(n.getValue());
				paths.add(path);
				continue;
			}
			
			/* Now this actually gave me a problem, I was adding the sum before the traverse recursion occurred so when the program came back to do the next iteration, the sum had already been incremented  
			* I ended up subtracting the sum after the recursion occurred
			* wasn't my best or preferred solution but it worked for this.
			*/
			if(sum + n.getValue() < 20) {
				sum += n.getValue();
				p.add(n.getValue());
				traverse(n, node, sum, p);
				sum -= n.getValue();
			}
		}
		
	}
	
	/**
	 * Prints all paths to 20
	 */
	void print() {
		int count = 0;
		for(ArrayList<Integer> path : paths)
		{
			System.out.println("Path " + count);
			for(int i : path)
				System.out.print(i + ", ");
			
			count++;
			System.out.println();
		}
	}
	

	
}

/**
 * Board functionality
 */
class Board {
	Node[][] board;
	
	Board() {
		board = new Node[3][3];
	}

	// Returns requested nodes, not used except in main method
	Node getNode(int i) {
		for(Node[] r : board)
			for(Node n : r)
				if(n.getValue() == i)
					return n;
		
		return null;
	}
	
	void setupBoard() {
		// For each node add incremented number
		for(int i = 0; i< 3; i++)
		{
			for(int j = 0; j < 3; j++)
			{
				// Multiply i (row) by three to get the proper number for the row
				// Add 1 to make sure you account for index offset
				Node n = new Node(i*3 + j + 1, i, j);
				board[i][j] = n;
			}
		}
		
		// For each node check to see if there is a boundary if there is a boundary make sure to set its node to null
		for(int i = 0; i< 3; i++)
		{
			for(int j = 0; j < 3; j++)
			{
				Node n = board[i][j];
				// x
				switch(j) {
				case 0:
					n.setRight(board[i][j+1]);
					break;
				case 2:
					n.setLeft(board[i][j-1]);
					break;
				default:
					n.setLeft(board[i][j-1]);
					n.setRight(board[i][j+1]);
					break;
				}
				// y
				switch(i) {
				case 0:
					n.setUp(board[i+1][j]);
					break;
				case 2:
					n.setDown(board[i-1][j]);
					break;
				default:
					n.setUp(board[i+1][j]);
					n.setDown(board[i-1][j]);
					break;
				}
			}
		}
		
	}
	
	
}

class Node {
	
	Node right;
	Node left;
	Node down;
	Node up;
	
	// Ended up not using these, would be removed, but could provide more functionality long term
	int x, y;
	
	int value;
	
	/* Boundary nullifying was unnecessary because of above, but this was my original idea
	* Wanted to create using a default constructor to identify which were not initialized
	* threw errors first try, so the idea was scrapped
	*/
	Node(int val, int y, int x) {
		this.value = val;
		
		this.x = x;
		this.y = y;
		
		if(y == 0)
			down = null;
		if(x == 0)
			left = null;
		if(y == 2)
			up = null;
		if(x == 2)
			right = null;
	}
	
	// Get node to requested direction
	public Node getNode(Direction d) {
		if(d.equals(Direction.DOWN))
			return down;
		if(d.equals(Direction.LEFT))
			return left;
		if(d.equals(Direction.UP))
			return up;
		if(d.equals(Direction.RIGHT))
			return right;
		
		return null;
	}
	
	public void setRight(Node right) {
		this.right = right;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public void setDown(Node down) {
		this.down = down;
	}

	public void setUp(Node up) {
		this.up = up;
	}
	
	int getValue() {
		return value;
	}
	
	int getX() {
		return x;
	}
	
	int getY() {
		return y;
	}
	
	Node getLeft() {
		return left;
	}
	
	Node getRight() {
		return right;
	}
	
	Node getDown() {
		return right;
	}
	
	Node getUp() {
		return up;
	}
}


/**
 * Below code was my original idea for the Traverse Class
 * Just didnt't seem feasible, way too many variables were class variables, made it impossible to use recursion
 */
//int sum;
//Node original;
//Node past = null;
//ArrayList<ArrayList<Integer>> paths;
//ArrayList<Integer> currentPath;
//
//Traverser(Node n) {
//	this.original = n;
//	sum = original.getValue();
//	paths = new ArrayList<ArrayList<Integer>>();
//}
//

//
//void traverse(Node node, ArrayList<Integer> path, int sum) {
//	if(path.get(0) == null)
//		path.add(node.getValue());
//	for(int i = 0; i < 4; i++) {
//		Direction d = Direction.toDirection(i);
//		Node n = node.getNode(d);
//		
//		if(n == null)
//			continue;
//		if(n == past)
//			continue;
//		
//		ArrayList<Integer> p = new ArrayList<>(path);
//		
//		if(sum + n.getValue() > 20) {
//			sum = original.getValue();
//			p = new ArrayList<>();
//			continue;
//		}
//		
//		p.add(n.getValue());
//		
//		if(sum + n.getValue() == 20) {
//			paths.add(new ArrayList<>(p));
//			sum = original.getValue();
//			continue;
//		}
//
//		if(sum+n.getValue() < 20) {
//			sum += n.getValue();
//			past = n;
//			p.add(n.getValue());
//			traverse(n, p, sum);
//		}
//		
//	}
//
////	for(int i = 0; i < 4; i++) {
////		Direction d = Direction.toDirection(i);
////		Node n = current.getNode(d);
////		if(n == null)
////			continue;
////		if(n == past)
////			continue;
////		
////		currentPath.add(current.getValue());
////		if(sum + n.getValue() == 20) {
////			paths.add(currentPath);
////			currentPath = new ArrayList<>();
////			sum = 0;
////			continue;
////		}
////		
////		if(sum+n.getValue() > 20) {
////			sum = current;
////			currentPath = new ArrayList<>();
////			continue;
////		} 
////		
////		if(sum+n.getValue() < 20) {
////			sum += n.getValue();
////			past = current;
////			current = current.move(d);
////			traverse();
////		}
////	}
