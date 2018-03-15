import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Simulation {
  private static final int LIM_DEPTH =4;
  public static int INIT_DEPTH = 0;
  public static int MAX_DEPTH = 4;
  public static long SEED = 999;
  public static int GRID_SIZE = 4;

  /* different algorithms/policies */
  public static boolean ALG_BFT = true;
  public static boolean ALG_APPROXIMATE_BFT = false;

  /* TODO: Joseph */
  public static List<Board> expand(Board s, Direction direction) {
    // TODO 1: get all empty space after making the move
    ArrayList<Board> boards = new ArrayList<>();
    s.move(direction);

    // TODO 2: fill in each space and generate a new Board
    int[][] grids = s.getGrid();
    for (int r = 0; r < grids.length; ++r) {
      for (int c = 0; c < grids[0].length; ++c) {
        if (grids[r][c] == 0) {
          grids[r][c] = 2;
          boards.add(new Board(new Random(SEED), grids));
          grids[r][c] = 0;
        }
      }
    }
    s.undo();
    // TODO 3: return all possible resulting boards.
    return boards;
  }

  /* TODO: Le */
  public static Direction[] moves(Board board) {
    Direction[] directions = new Direction[4];

    directions[0] = board.canMove(Direction.LEFT) ? Direction.LEFT : null;
    directions[1] = board.canMove(Direction.RIGHT) ? Direction.RIGHT : null;
    directions[2] = board.canMove(Direction.UP) ? Direction.UP : null;
    directions[3] = board.canMove(Direction.DOWN) ? Direction.DOWN : null;

    return directions;
  }

  /* TODO: Dustin */
  // return the final expected score
  public static Tuple<Double, Direction, Map> BFT(Board s, int currDepth, int maxDepth) {
    if (currDepth == maxDepth) {
      if (ALG_APPROXIMATE_BFT)
        return approximateBFT(s); // run some heuristic
      else
        return new Tuple<Double, Direction, Map>(0.0, Direction.UP, null); // i.e. 0
    }


    // Store expected maxes for each direction
    double maxValue = 0.0;
    Direction maxDir = Direction.DOWN;
    Map<Integer, Tuple<Double, Direction, Map>> maxCandidates = new HashMap<>();

    for (Direction d : moves(s)) {
      // `d` is not a valid move at state `board`
      if (d == null)
        continue;

      s.move(d);
      double expMax = s.getScore();
      s.undo();

      // Build a tree of states so we know the path to traverse
      Map<Integer, Tuple<Double, Direction, Map>> candidates = new HashMap<>();

      List<Board> nextStates = expand(s, d);
      for (Board sPrime : nextStates) {
        // Get the exp map, the direction taken to get it, and a map of all of the candidates in
        // that direction
        Tuple<Double, Direction, Map> expMaxNextDir = BFT(sPrime, currDepth + 1, maxDepth);
        expMax += (1.0 / nextStates.size()) * expMaxNextDir.t1;
        // Add this to the list of candidates
        candidates.put(sPrime.hashCode(), expMaxNextDir);
      }

      // Update the max
      if (expMax > maxValue) {
        maxValue = expMax;
        maxDir = d;
        maxCandidates = candidates;
      }
    }
    return new Tuple<Double, Direction, Map>(maxValue, maxDir, maxCandidates);
  }

  /* TODO: Carlos */
  // return the final expected score
  public static Tuple<Double, Direction, Map> approximateBFT(Board s) {
    // No more candidates, just an estimate
    Map<Integer, Tuple<Double, Direction, Map>> nowhereToGo = new HashMap<>();

    // Add all the values at the board at this point
    int[][] grids = s.getGrid();
    double total = 0.0;
    int numOpen = 0;

    // Compute total sum and get number of open tiles
    for (int r = 0; r < grids.length; ++r) {
      for (int c = 0; c < grids[0].length; ++c) {
        total += grids[r][c] = 2;
        if (grids[r][c] == 0)
          numOpen++;
      }
    }

    // account for possibility of moves
    Direction[] dirs = moves(s);
    int countOfMoves = 0;
    for (Direction dir : dirs)
      if (dir != null)
        countOfMoves++;

    // Now estimate for `limDepth` to `maxDepth`
    // Multiply sum of current tiles by |num open spots|/|board size|
    // (bonus for having more open spots)
    int boardSize = grids.length * grids.length;
    total *= (numOpen + grids.length * countOfMoves) / boardSize;
    return new Tuple<Double, Direction, Map>(total, Direction.UP, nowhereToGo);
  }

  /* TODO: Le */
  public static double simulate(int[][] initialGrid, int maxPlays) {
    int count = 0;
    double score = 0;

    // approximate if flag is set, otherwise go all the way down to `maxPlays`
    int maxBFTDepth = ALG_APPROXIMATE_BFT ? LIM_DEPTH : MAX_DEPTH;

    while (count < maxPlays) {
      // initialize the board to the same initialGrid, but use a different seed
      // so that our plays vary
      Random rand = new Random(SEED + count);
      Board board = new Board(rand, initialGrid);
      System.out.println("Initial board:\n" + board.toString());
      Tuple<Double, Direction, Map> next = BFT(board, 0, maxBFTDepth);

      // Play the game up to depth `MAX_DEPTH`
      for (int currDepth = 0; currDepth < MAX_DEPTH; currDepth++) {
        // Evaluate our current state
        // copy the board to prevent the code from changing ours
        //Board s = new Board(rand, board.getGrid());
        //Tuple<Double, Direction, Map> next = BFT(s, 0, maxBFTDepth);

        // Move in direction of highest expected score
        // Log actions
        System.out.println(String.format("@%d: took action %s (E[score@%d] = %.2f)", currDepth,
            next.t2, maxBFTDepth, next.t1));
        board.move(next.t2);
        System.out.println(board);
        board.addRandomTile();
        System.out.println(board);
        next = (Tuple<Double, Direction,Map>) next.t3.get(board.hashCode());
      }
      score += board.getScore();

      count++;
    }
    return score / count;
  }

  public static void main(String[] args) {
     simulate(new int[][] {{0, 0, 2, 0}, {0, 2, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}}, 
            10 /* play 10 times */);
    // Random generator = new Random(9);
    // Board board = new Board(generator, 3);
    // System.out.println("init:");
    // System.out.println(board);
    // List<Board> res = expand(board, Direction.DOWN);
    // for (Board b : res)
    // System.out.println(b);
  }
}

