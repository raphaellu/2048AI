import java.util.*;
import javafx.util.*;

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
	public static Tuple<Double, Direction, Map> BFT(Board board, int currDepth, int maxDepth){
        
        if (currDepth == maxDepth) return new Tuple<Double, Direction, Map>();
        
        //Store expected maxes for each direction
        double maxValue = 0.0;
        Map<int, Tuple<Double, Direction, Map>> maxCandidates;

        for (Direction d : moves(board)) {
            board.move(d);
            board.addRandomTile();
            double expMax = board.getScore();
            board.undo();
        
            //Build a tree of states so we know the path to traverse
            Map<int, Tuple<float, Direction, Map>> candidates = new HashMap();

            List<Board> nextStates = expand(board, d);
            for (Board sPrime : nextStates) {
                //Get the exp map, the direction taken to get it, and a map of all of the candidates in that direction
                Tuple<Double, Direction, Map> expMaxNextDir = BFT(sPrime, currDepth + 1, maxDepth);
                expMax += (1.0 / nextStates.size()) * expMaxNextDir.t1;
                //Add this to the list of candidates
                candidates.put(Arrays.hashCode(sPrime.getGrid()), expMaxNextDir);
            }

            //Update the max
            if (expMax > maxValue) {
                maxValue = expMax;
                maxCandidates = candidates;
            }
        } 

        return Tuple<Double, Direction, Map>(maxValue, d, maxCandidates); 

    }

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

