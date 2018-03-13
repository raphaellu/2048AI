import java.util.*;

public class Simulation {

	/*TODO: Joseph*/
	public static List<Board> expand(Board board){return null;}

	/*TODO: Le*/
	public static Direction[] moves(Board board){return new Direction[4];}

	/*TODO: Dustin*/
	// return the final expected score
	public static int BFT(Board board, int currDepth, int maxDepth){return 0;}

	/*TODO: Carlos*/
	// return the final expected score
	public static int ApproximateBFT(Board board, int currDepth, int maxDepth){return 0;}

	/*TODO: Le*/
	public static void simulate(Board board, int maxDepth, int seed){}


	public static void main(String[] args) {
		Board board = new Board(new Random(), 4);
		System.out.println(board);
	}
}

