import java.io.*;
import java.util.*;

public class Simulation {
  private static int LIM_DEPTH = 3;
  public static int INIT_DEPTH = 0;
  public static int MAX_DEPTH = 100;
  public static long SEED = 999;
  public static int GRID_SIZE = 4;

  public static final int OPEN_SPACE = 0;
  public static final int MOVABLE_DIRS = 1;
  public static final int EDGE_TILES = 2;
  public static final int CORNER_TILES = 3;
  public static final int POTENTIAL_MERGE = 4;

  /* different algorithms/policies */
  public static boolean ALG_BFT = false;
  public static boolean ALG_APPROXIMATE_BFT = !ALG_BFT;


  public static HashSet<Integer> policies;

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
  public static Tuple<Double, Direction, Map> BFT_recurse(Board s, int currDepth, int maxDepth,
      boolean enableHeuristic) {
    if (currDepth == maxDepth || s.isGameOver()) {
      if (!s.isGameOver() && ALG_APPROXIMATE_BFT && enableHeuristic)
        return approximateBFT(s, policies); // run some heuristic
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
        Tuple<Double, Direction, Map> expMaxNextDir =
            BFT_recurse(sPrime, currDepth + 1, maxDepth, enableHeuristic);
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

  @SuppressWarnings("unchecked")
  public static Tuple<Double, Direction, Map> BFT(Board s, int currDepth, int maxDepth,
      boolean enableHeuristic) {
    if (currDepth == maxDepth || s.isGameOver()) {
      if (!s.isGameOver() && ALG_APPROXIMATE_BFT && enableHeuristic)
        return approximateBFT(s, policies); // run some heuristic
      // return new Tuple<Double, Direction, Map>(0.0, Direction.UP, null);
      else
        return new Tuple<Double, Direction, Map>(0.0, Direction.UP, null); // i.e. 0
    }

    Tuple<Double, Direction, Map> maxExpScoreTransition =
        Arrays.stream(moves(s)).parallel().map(d -> {
          // `d` is not a valid move at state `board`
          if (d == null)
            return new Tuple<Double, Direction, Map>(0.0, Direction.UP, null);

          Board ss = new Board(s.random, s.getGrid());

          ss.move(d);
          double expMax = ss.getScore();
          ss.undo();

          // Build a tree of states so we know the path to traverse
          Map<Integer, Tuple<Double, Direction, Map>> candidates = new HashMap<>();

          List<Board> nextStates = expand(ss, d);
          for (Board sPrime : nextStates) {
            // Get the exp map, the direction taken to get it, and a map of all of the candidates in
            // that direction
            Tuple<Double, Direction, Map> expMaxNextDir =
                BFT_recurse(sPrime, currDepth + 1, maxDepth, enableHeuristic);
            expMax = expMax + (1.0 / nextStates.size()) * expMaxNextDir.t1;
            // Add this to the list of candidates
            candidates.put(sPrime.hashCode(), expMaxNextDir);
          }

          return new Tuple<Double, Direction, Map>(expMax, d, candidates);
        }).max((dir1, dir2) -> {
          return dir1.t1 > dir2.t1 ? 1 : -1;
        }).orElse(new Tuple<Double, Direction, Map>(0.0, Direction.UP, null));

    return maxExpScoreTransition;
  }

  /* TODO: Carlos */
  // return the final expected score
  public static Tuple<Double, Direction, Map> approximateBFT(Board s, HashSet<Integer> policies) {
    // No more candidates, just an estimate
    Map<Integer, Tuple<Double, Direction, Map>> nowhereToGo = new HashMap<>();

    // Add all the values at the board at this point
    int[][] grids = s.getGrid();
    double total = 0.0;
    int numOpen = 0;
    int maxTile = 0, maxR = 0, maxC = 0;

    // Compute total sum and get number of open tiles
    // POLICY: OPEN_SPACE
    for (int r = 0; r < grids.length; ++r) {
      for (int c = 0; c < grids[0].length; ++c) {

        // get max corner value
        maxTile = Math.max(maxTile, grids[r][c]);


        total += grids[r][c];
        if (grids[r][c] == 0)
          numOpen++;
      }
    }

    // account for possibility of moves
    // POLICY: MOVABLE_DIRS
    Direction[] dirs = moves(s);
    int countOfMoves = 0;
    for (Direction dir : dirs)
      if (dir != null)
        countOfMoves++;


    // account for large tiles on the edge
    // POLICY: EDGE_TILES
    int numLargeEdgeTiles = 0;
    for (int r = 0; r < grids.length; ++r) {
      for (int c = 0; c < grids[0].length; ++c) {

        if (r == 0 || r == grids.length - 1 || c == 0 || c == grids.length - 1) {
          if (grids[r][c] >= maxTile / 2.0) numLargeEdgeTiles++;
        }
      }
    }

    // account for large tiles at the corner
    // POLICY: EDGE_TILES
    int numCornerTiles = 0;
    for (int r = 0; r < grids.length; ++r) {
      for (int c = 0; c < grids[0].length; ++c) {

        if ( (r == 0 && c == 0) || (r == 0 && c == grids.length-1) || (r == grids.length-1 && c == 0)
             || (r == grids.length-1 && c == grids.length-1) )
        {
          if (grids[r][c] >= maxTile / 2.0) numCornerTiles++;
        }
      }
    }

    // account for potential merges across rows and cols 
    // POLICY: POTENTIAL_MERGE
    int numPotentialMerges = 0;
    for (int r = 0; r < grids.length-1; ++r) {
      for (int c = 0; c < grids[0].length-1; ++c) {
        if (grids[r][c] == grids[r][c+1] || grids[r][c] == grids[r+1][c])
          numPotentialMerges ++;
      }
    }

    // Now estimate for `limDepth` to `maxDepth`
    // Multiply sum of current tiles by |num open spots|/|board size|
    // (bonus for having more open spots)
    int boardSize = grids.length * grids.length;

    double factor = 0.0;
    if (policies.contains(OPEN_SPACE))
      factor += numOpen;
    if (policies.contains(MOVABLE_DIRS))
      factor += grids.length * countOfMoves;
    if (policies.contains(EDGE_TILES))
      factor += numLargeEdgeTiles;
    if (policies.contains(CORNER_TILES))
      factor += grids.length * numCornerTiles;
    if (policies.contains(POTENTIAL_MERGE))
      factor += numPotentialMerges;
    total *= 1 + (factor / boardSize);  
    // total *= (numOpen + grids.length * countOfMoves) / boardSize;


    // reward highest value at corner
    return new Tuple<Double, Direction, Map>(total, Direction.UP, nowhereToGo);
  }

  /* TODO: Le */
  public static double simulate(int[][] initialGrid, int maxPlays, PrintWriter pw) {
    int count = 0;
    double score = 0;

    // approximate if flag is set, otherwise go all the way down to `maxPlays`
    int maxBFTDepth = ALG_APPROXIMATE_BFT ? LIM_DEPTH : MAX_DEPTH;

    double exp_at_k = 0.0, actual_at_k = 0.0, scoreAtPrevK = 0.0;

    while (count < maxPlays) {
      // initialize the board to the same initialGrid, but use a different seed
      // so that our plays vary
      Random rand = new Random(SEED + count);
      Board board = new Board(rand, initialGrid);
      System.out.println("Initial board:\n" + board.toString());

      // Just do the calculation once for full BFT
      Tuple<Double, Direction, Map> next = new Tuple<>();
      if (ALG_BFT) {
        next = BFT(board, 0, MAX_DEPTH, true);
      }

      Double sum_exp_at_k = 0.0;
      // Play the game up to depth `MAX_DEPTH`
      for (int currDepth = 0; currDepth < MAX_DEPTH; currDepth++) {
        // Evaluate our current state
        // Only calculate in the loop if we are approximating
        // and `currDepth` is multiple of `maxBFTDepth`

        if (ALG_APPROXIMATE_BFT && currDepth % maxBFTDepth == 0) {
          // copy the board to prevent the code from changing ours
          Board s = new Board(board);
          actual_at_k = board.getScore() - scoreAtPrevK;

          // Print how much we improved in `maxBFTDepth` vs how much we expected to improve
          if (currDepth > 0) {
            System.out.println(String.format("actual gain@%d: %f;", currDepth, actual_at_k));
            System.out.println(String.format("expect gain@%d: %f;", currDepth, exp_at_k));
            System.out
                .println(String.format("diff       @%d: %f;", currDepth, actual_at_k - exp_at_k));
          }

          next = BFT(s, 0, maxBFTDepth, true);
          scoreAtPrevK = board.getScore();
          exp_at_k = next.t1;
          sum_exp_at_k += BFT(s, 0, maxBFTDepth, false).t1;
          // sum_exp_at_k_wo += exp_at_k;
          // System.out.println("================");
          // System.out.println("Exp   @k: " + sum_exp_at_k);
          // System.out.println("Actual@k: " + board.getScore());
        }
        // Move in direction of highest expected score
        // Log actions
        System.out.println(String.format("@%d: took action %s (E[score@%d] = %.2f)", currDepth,
            next.t2, maxBFTDepth, next.t1));
        board.move(next.t2);
        System.out.println(board);
        board.addRandomTile();
        System.out.println(board);

        // Get the next state from the map
        next = (Tuple<Double, Direction, Map>) next.t3.get(board.hashCode());
      }
      score += board.getScore();

      count++;
      double diff = board.getScore() - sum_exp_at_k;
      pw.write("MAX_DEPTH: " + MAX_DEPTH + "\tLIM_DEPTH: " + LIM_DEPTH + "\texpected: " + sum_exp_at_k
          + " actual: " + board.getScore() + "\tdiff: " +  diff + "\tFlag: " + (diff > 0 ? 1 : 0) + "\n");
      pw.flush();
    }
    return score / count;
  }

  public static void main(String[] args) throws Exception {
    PrintWriter pw = new PrintWriter(new File("output.txt"));
    // Add all heuristic policies wishing to use
    policies = new HashSet<>();
    policies.add(OPEN_SPACE);
    policies.add(EDGE_TILES);
    policies.add(MOVABLE_DIRS);
    policies.add(CORNER_TILES);
    policies.add(POTENTIAL_MERGE);

    int[] lim_depth_list = {3, 4, 5};
    int[] max_depth_list = {50, 100, 200, 500};

    // int[] lim_depth_list = {3, 4};
    // int[] max_depth_list = {10, 20, 50};

    for (int max_depth : max_depth_list) {
    	MAX_DEPTH = max_depth;
    	for (int lim_depth : lim_depth_list) {
    		LIM_DEPTH = lim_depth;
    		simulate(new int[][] {{0, 0, 2, 0}, {0, 2, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}},
        				10 /* play 10 times */, pw);
    	}
    }

    pw.close();
  }
}

