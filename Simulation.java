import java.util.*;

public class Simulation {
	public static int INIT_DEPTH = 0;
	public static int MAX_DEPTH = 100;
	public static long SEED = 999;
	public static int GRID_SIZE = 4;

	/* different algorithms/policies */
	public static int ALG_BFT = 0;
	public static int ALG_APPXIMATE_BFT = 1;

	/*TODO: Joseph*/
	public static List<Board> expand(Board board, Direction action){
		// TODO 1: get all empty space after making the move

		// TODO 2: fill in each space and generate a new Board

		// TODO 3: return all possible resulting boards.
		return null;
	}

	/*TODO: Le*/
	public static Direction[] moves(Board board){
		Direction[] directions = new Direction[4];
		directions[0] = board.canMove(Direction.LEFT) ? Direction.LEFT : null;
		directions[1] = board.canMove(Direction.RIGHT) ? Direction.RIGHT : null;
		directions[2] = board.canMove(Direction.UP) ? Direction.UP : null;
		directions[3] = board.canMove(Direction.DOWN) ? Direction.DOWN : null;
		return directions;
	}

	/*TODO: Dustin*/
	// return the final expected score
	public static int BFT(Board board, int currDepth, int maxDepth){return 0;}

	/*TODO: Carlos*/
	// return the final expected score
	public static int approximateBFT(Board board, int currDepth, int maxDepth){return 0;}

	/*TODO: Le*/
	public static double simulate(int currDepth, int maxDepth, int repeat, int alg){
		int count = 0;
		double score = 0;
		while (count < repeat) {
			Board board = new Board(new Random(SEED), GRID_SIZE);	
			if (alg == ALG_BFT)
				score += BFT(board, currDepth, MAX_DEPTH);
			else if (alg == ALG_APPXIMATE_BFT)
				score += approximateBFT(board, currDepth, MAX_DEPTH);
			count++;
		}
		return score/(double)count;
	}

	public static void main(String[] args) {
		simulate(INIT_DEPTH, MAX_DEPTH, 1, ALG_BFT);
	}
}

