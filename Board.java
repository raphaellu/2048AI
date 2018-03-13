
/*
 * Name: Abena Bonsu
 * Login: cs8bwalo
 * Date: February 4, 2016
 * File: Board.java
 * Sources of Help: textbook, tutors
 *
 * Describe what the program does here:
 * This Class is used to construct a Board object to be used
 * for the simulation of the game 2048. It can create a fresh
 * board or load an already existing board. In addition this
 * class allows the user to save their current game to a new, 
 * specified file. The class also allows for the board to be 
 * rotated 90 degrees to the right or left. Baed on the direction
 * passed in by the user, this class will then move tiles
 * existing on the board in a certain direction, combining tiles
 * of the same value. The game is considered to be over when
 * the board cannot move in any direction.
 * /

//------------------------------------------------------------------//
// Board.java                                                       //
//                                                                  //
// Class used to represent a 2048 game board                        //
//                                                                  //
// Author:  W16-CSE8B-TA group      																//
// Last updated by: Carlos Mattoso                                  //
// Date:    2/12/18                                                 //
//------------------------------------------------------------------//

/**
 * Sample Board
 * <p/>
 * 0   1   2   3
 * 0   -   -   -   -
 * 1   -   -   -   -
 * 2   -   -   -   -
 * 3   -   -   -   -
 * <p/>
 * The sample board shows the index values for the columns and rows
 * Remember that you access a 2D array by first specifying the row
 * and then the column: grid[row][column]
 */

import java.util.*;

public class Board {

	/** Number of tiles showing when the game starts */
	public final int NUM_START_TILES = 2;  

	/** The probability (times 100) that a randomly 
	 * generated tile will be a 2 (vs a 4)
	 */
	public final int TWO_PROBABILITY = 90;

	/**
	 * The size of the grid
	 */
	public final int GRID_SIZE;  

	private int[][] grid;  // The grid of tile values
	private int score;     // The current score

	// You do not have to use these variables
	private final Random random;  // A random number generator (for testing)
	private Stack<int[][]> undoGrids;  // History of game boards
	private Stack<Integer> undoScores;     // History of scores

	/*
	 * Name: Board(Random random, int boardSize)
	 *
	 * Purpose: The purpose of this method is to create or construct a fresh
	 * board for the user with two random tiles places within the board. This
	 * board will have a particular boardSize that the user sets, as well as a
	 * random
	 *
	 * Parameters: int boardsize which represents the size of the 2048 game
	 * board to be used. Random random represents the random number which be
	 * used to specifiy where (after every move) a new tile should be added to
	 * the board when playing.
	 *
	 * Return: void
	 */
	public Board(Random random, int boardSize) {
		// intialize member variables
		this.random = random;
		if (boardSize < 2 || boardSize < 0) {
			boardSize = 4;
		}
		this.GRID_SIZE = boardSize;
		this.grid = new int[boardSize][boardSize];
		this.score = 0;

		// variable used to store a saved board
		this.undoGrids = new Stack<>();
		this.undoScores = new Stack<>();

		// loop through and add two initial tiles to the board randomly
		for (int index = 0; index < NUM_START_TILES; index++) {
			addRandomTile();
		}
	}


	/** Constructor used to load boards for grading/testing
	 *
	 * DO NOT REMOVE THIS CONSTRUCTOR.
	 * 
	 * @param random2
	 * @param inputBoard
	 */
	public Board(Random random2, int[][] inputBoard) {
		this.random = random2;
		this.GRID_SIZE = inputBoard.length;
		setGrid(inputBoard);
	}


	/** 
	 * return the tile value in a particular cell in the grid.
	 * @param row The row
	 * @param col The column
	 * @return The value of the tile at (row, col)
	 */
	public int getTileValue(int row, int col) {
		return grid[row][col];
	}

	/**
	 * get a copy of the grid
	 * @return A copy of the grid
	 */
	public int[][] getGrid() {
		int[][] gridCopy = new int[GRID_SIZE][GRID_SIZE];
		for (int r = 0; r < grid.length; r++)
		{
			for (int c = 0; c < grid[r].length; c++) {
				gridCopy[r][c] = grid[r][c];
			}
		}
		return gridCopy;
	}

	/**
	 * Get the current score
	 * @return the current score of the game
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Set the grid to a new value.  This method can be used for 
	 * testing and is used by our testing/grading script.
	 * @param newGrid The values to set the grid to
	 */
	public void setGrid(int[][] newGrid) {
		if (newGrid.length != GRID_SIZE || 
				newGrid[0].length != GRID_SIZE) {
			System.out.println("Attempt to set grid to incorrect size");
			return;
				}
		this.grid = new int[GRID_SIZE][GRID_SIZE];
		for (int r = 0; r < grid.length; r++)
		{
			for (int c = 0; c < grid[r].length; c++) {
				grid[r][c] = newGrid[r][c];
			}
		}
	}

	/*
	 * Name: addRandomTile()
	 *
	 * Purpose: The purpose of this method is to add a random tile of either
	 * value 2 or 4 to a random empty space on the 2048
	 * board. The place where this tile is added is dependent on the random
	 * value associated with each board object. If no tiles are empty, it
	 * returns without changing the board.
	 *
	 * Parameters: none
	 *
	 * Return: void
	 */
	public void addRandomTile() {
		int count = 0;
		// loop through grid keeping count of every empty space on board
		for (int rowI = 0; rowI < grid.length; rowI++) {
			for (int colI = 0; colI < grid[rowI].length; colI++) {
				if (grid[rowI][colI] == 0) {
					count++;
				}
			}
		}

		// if count is still 0 after loop, no empty spaces, return
		if (count == 0) {
			System.out.println("There are no empty spaces!");
			return;
		}

		// keep track of where on board random tile should be placed
		int location = random.nextInt(count);
		int value = random.nextInt(100);

		// reset count
		count = 0;
		// loop through grid checking where grid is 0 & incrementing count
		for (int rowI = 0; rowI < grid.length; rowI++) {
			for (int colI = 0; colI < grid[rowI].length; colI++) {
				if (grid[rowI][colI] == 0) {
					// if count equals random location generated, place tile
					if (count == location) {
						// System.out.println("Adding a tile to location " + rowI + ", " + colI);
						grid[rowI][colI] = 2;
					}
					count++;
				}
			}
		}
	}

	public void addTile(int row, int col) {
		if (grid[row][col] == 0)
			grid[row][col] = 2;
		else
			System.out.println("You cannot add tile to row " + row + " and col " + col);
	}


	/*
	 * Name: isGameOver()
	 *
	 * Purpose: The purpose of this method is to check whether or not the game
	 * in play is over. The game is officially over once there are no longer any
	 * valid moves that can be made in any direction. If the game is over, this
	 * method will return true and print the words: "Game Over!" This method
	 * will be checked before any movement is ever made.
	 *
	 * Parameters: none
	 * 
	 * Return: boolean: true if the game is over, and false if the game isn't
	 * over
	 *
	 */
	public boolean isGameOver() {
		return (!canMoveLeft() && !canMoveRight() && !canMoveUp() 
				&& !canMoveDown());
	}

	/*
	 * Name: canMove(Direction direction)
	 *
	 * Purpose: The purpose of this method is to check to see if the movement of
	 * the tiles in any direction can actually take place. It does not move the
	 * tiles, but at every index of the grid, checks to see if there is a tile
	 * above, below, to the left or right that has the same value. If this is
	 * the case, then that tile can be moved. It also checks if there is an
	 * empty (0) tile at a specified index, as this also indicates that movement
	 * can be possible. This method is called within move() so that that method
	 * can determine whether or not tiles should be moved.
	 *
	 * Parameters: Direction direction which represents the direction the tiles
	 * will move (if possible)
	 *
	 * Return: boolean: true if the movement can be done and false if it cannot
	 *
	 */
	public boolean canMove(Direction direction) {
		// utilize submethods to check if movement in a particular
		// direction is possible
		if (direction == Direction.LEFT) {
			return canMoveLeft();
		}
		else if (direction == Direction.RIGHT) {
			return canMoveRight();
		}
		else if (direction == Direction.UP) {
			return canMoveUp();
		}
		else if (direction == Direction.DOWN) {
			return canMoveDown();
		}
		// If we got here, something went wrong, so return false
		return false;
	}


	// ================================ PART 4 ==================================

	/*
	 * Name: canMoveLeft()
	 *
	 * Purpose: The purpose of this method is to test to see if a particular
	 * tile at a particular index inside of the grid can be moved to the left.
	 * This method does this by checking to see if a tile to the left of the
	 * specified element is either 0, or holds the same value as the tile. If
	 * either is the case, then the method returns true so that the board knows
	 * that it can be moved. However, it this method itself doesnt move any
	 * tiles.
	 *
	 * Parameters: none
	 *
	 * Return: boolean: returns true if a tile can be moved left. Returns false
	 * if it can't
	 *
	 */
	private boolean canMoveLeft() {
		// determine the position of the element in the list
		for (int i = 0; i < grid.length; i++) {
			for (int j = 1; j < grid[i].length; j++) {
				// determine possibility of movement at particular index
				if (grid[i][j - 1] == grid[i][j] && grid[i][j - 1] != 0) {
					return true;
				} else if (grid[i][j - 1] == 0 && grid[i][j] > 0) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * Name: canMoveRight()
	 *
	 * Purpose: The purpose of this method is to test to see if a particular
	 * tile at a particular index inside of the grid can be moved to the right.
	 * This method does this by checking to see if a tile to the right of the
	 * specified element is either 0, or holds the same value as the tile. If
	 * either is the case, then the method returns true so that the board knows
	 * that it can be moved. However, it this method itself doesnt move any
	 * tiles.
	 *
	 * Parameters: none
	 *
	 * Return: boolean: returns true if a tile can be moved right. Returns false
	 * if it can't
	 *
	 */
	private boolean canMoveRight() {
		// determine the position of the element in the list
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length - 1; j++) {
				// determine possibility of movement at particular index
				if (grid[i][j + 1] == grid[i][j] && grid[i][j + 1] != 0) {
					return true;
				} else if (grid[i][j + 1] == 0 && grid[i][j] > 0) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * Name: canMoveUp()
	 *
	 * Purpose: The purpose of this method is to test to see if a particular
	 * tile at a particular index inside of the grid can be moved up. This
	 * method does this by checking to see if a tile above the specified element
	 * is either 0, or holds the same value as the tile. If either is the case,
	 * then the method returns true so that the board knows that it can be
	 * moved. However, it this method itself doesnt move any tiles.
	 *
	 * Parameters: none
	 *
	 * Return: boolean: returns true if a tile can be moved up. Returns false if
	 * it can't
	 *
	 */
	private boolean canMoveUp() {
		// determine the position of the element in the list
		for (int i = 1; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				// determine possiblility of movement at particular index
				if (grid[i - 1][j] == grid[i][j] && grid[i - 1][j] != 0) {
					return true;
				} else if (grid[i - 1][j] == 0 && grid[i][j] > 0) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * Name: canMoveDown()
	 *
	 * Purpose: The purpose of this method is to test to see if a particular
	 * tile at a particular index inside of the grid can be moved down. This
	 * method does this by checking to see if a tile beneath the specified
	 * element is either 0, or holds the same value as the tile. If either is
	 * the case, then the method returns true so that the board knows that it
	 * can be moved. However, it this method itself doesnt move any tiles.
	 *
	 * Parameters: none
	 *
	 * Return: boolean: returns true if a tile can be moved down. Returns false
	 * if it can't
	 *
	 */
	private boolean canMoveDown() {
		// determine the position of the element in the list
		for (int i = 0; i < grid.length - 1; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				// determine possibility of movement at particular index
				if (grid[i + 1][j] == grid[i][j] && grid[i + 1][j] != 0) {
					return true;
				} else if (grid[i + 1][j] == 0 && grid[i][j] > 0) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * Name: move(Direction direction)
	 *
	 * Purpose: The purpose of this method is to move the tiles in the game
	 * board by a specified direction passed in as a parameter. If the movement
	 * cannot be done, the method returns false. If the movement can be done, it
	 * moves the tiles and returns true. This method relies on the help of four
	 * other helper methods to perform the game play.
	 *
	 * Parameters: Direction direction which represents the direction the tiles
	 * will move (if possible)
	 *
	 * Return: boolean: true if the movement can be done and false if it cannot
	 *
	 */

	// ============================= End of PART 4 ==============================

	// One more thing... return to Gui2048.java for the final part: the Undo button.
	// (just fill out the createUndoButton method at the end of Gui2048.java)
	//																 o(^▽^)o

	/*
	 * Name: move(Direction direction)
	 *
	 * Purpose: The purpose of this method is to move the tiles in the game
	 * board by a specified direction passed in as a parameter. If the movement
	 * cannot be done, the method returns false. If the movement can be done, it
	 * moves the tiles and returns true. This method relies on the help of four
	 * other helper methods to perform the game play.
	 *
	 * Parameters: Direction direction which represents the direction the tiles
	 * will move (if possible)
	 *
	 * Return: boolean: true if the movement can be done and false if it cannot
	 *
	 */
	public boolean move(Direction direction) {
		// first check if canMove can be done
		if (canMove(direction) == true) {
			// preserve the board so we can undo our last move
			this.undoGrids.push(this.getGrid());
			this.undoScores.push(this.score);

			// move in relationship to the direction passed in
			if (direction == Direction.LEFT) {
				moveLeft(direction);
			} else if (direction == Direction.RIGHT) {
				moveRight(direction);
			} else if (direction == Direction.UP) {
				moveUp(direction);
			} else if (direction == Direction.DOWN) {
				moveDown(direction);
			}
		}
		// else if canMove is false, exit and don't move tiles
		else {
			return false;
		}
		return true;
	}

	/*
	 * Name: moveRight(Direction direction)
	 *
	 * Purpose: To move the tiles in the board as far right as they can go,
	 * while combining tiles of the same value. A tile can only be combined once
	 * per movement. Each row within the grid is checked for movement and
	 * successfully moved. This method relies on the use of an ArrayList to
	 * shift the tiles. It places non-zero tiles into an ArrayList, and combines
	 * adjacent tiles of the same value. It then adjusts the size of the
	 * ArrayList according to how many tiles of value 0 were removed from the
	 * list.
	 *
	 * Parameters: Direction direction which represents the direction in which
	 * the tiles will be moved
	 *
	 * Return: void
	 *
	 */
	private void moveRight(Direction direction) {
		// count used to determine how many zeros to be added after shift
		int newCount = 0;

		ArrayList<Integer> list;
		for (int i = 0; i < GRID_SIZE; i++) {
			int count = 0;
			// create new ArrayList everytime a new row is accessed
			list = new ArrayList<>();

			for (int j = 0; j < grid[i].length; j++) {
				// if value of grid element is 0, increment count
				if (grid[i][j] == 0) {
					count++;
				}

				// only add non-zero numbers to the ArrayList (shift)
				else if (grid[i][j] > 0) {
					list.add(Integer.valueOf(this.grid[i][j]));
				}
				newCount = count;
			}

			// start from left most element in ArrayList, iterate backwards
			for (int tile = list.size() - 1; tile >= 1; tile--) {
				// check if adjacent tile is same value
				if (list.get(tile).equals(list.get(tile - 1))) {
					// if so, double the right tile's value
					int value = Integer.valueOf(list.get(tile) * 2);
					list.set(tile, Integer.valueOf(value));

					// make the left tile 0
					list.set(tile - 1, Integer.valueOf(0));

					// increment score
					this.score = score + value;
				}
			}

			for (int removeTile = 0; removeTile < list.size(); removeTile++) {
				// remove newly made 0's from ArrayList
				if (list.get(removeTile) == 0) {
					list.remove(removeTile);
					// if you remove a 0, increment count
					newCount++;
				}
			}

			// count now represents the # of 0s you didn't add to list plus the
			// number you took away. Add this # back to beginning of ArrayList
			for (int addTile = 0; addTile < newCount; addTile++) {
				list.add(addTile, Integer.valueOf(0));
			}

			// set values of grid to values of ArrayList (one row)
			for (int index = 0; index < grid.length; index++) {
				grid[i][index] = list.get(index).intValue();
			}
		}
	}

	/*
	 * Name: moveLeft(Direction direction)
	 *
	 * Purpose: To move the tiles in the board as far left as they can go, while
	 * combining tiles of the same value. A tile can only be combined once per
	 * movement. Each row within the grid is checked for movement and
	 * successfully moved. This method relies on the use of an ArrayList to
	 * shift the tiles. It places non-zero tiles into an ArrayList, and combines
	 * adjacent tiles of the same value. It then adjusts the size of the
	 * ArrayList according to how many tiles of value 0 were removed from the
	 * list.
	 *
	 * Parameters: Direction direction which represents the direction in which
	 * the tiles will be moved
	 *
	 * Return: void
	 *
	 */
	private void moveLeft(Direction direction) {
		// count used to determine how many zeros to be added after shift
		int newCount = 0;
		ArrayList<Integer> list;
		for (int i = 0; i < GRID_SIZE; i++) {
			int count = 0;
			// create new ArrayList everytime a new row is accessed
			list = new ArrayList<>();

			for (int j = 0; j < grid[i].length; j++) {
				// if value of grid element is 0, increment count
				if (grid[i][j] == 0) {
					count++;
				}

				// only add non-zero numbers to the ArrayList (shift)
				else if (grid[i][j] > 0) {
					list.add(Integer.valueOf(this.grid[i][j]));
				}
				newCount = count;
			}

			// start from right most element in ArrayList, iterate forward
			for (int tile = 0; tile < list.size() - 1; tile++) {
				// check if adjacent tile is same value
				if (list.get(tile).equals(list.get(tile + 1))) {

					// if so, double the left tile's value
					int value = Integer.valueOf(list.get(tile) * 2);
					list.set(tile, Integer.valueOf(value));

					// make the adjacent right tile 0
					list.set(tile + 1, Integer.valueOf(0));

					// increment score
					this.score = score + value;
				}
			}

			for (int removeTile = 0; removeTile < list.size(); removeTile++) {
				// remove newly made 0's from ArrayList
				if (list.get(removeTile) == 0) {
					list.remove(removeTile);

					// if you remove a 0, increment count
					newCount++;
				}
			}

			// count now represents the # of 0s you didn't add to list plus the
			// number you took away. Add this # back to beginning of ArrayList
			for (int addTile = 0; addTile < newCount; addTile++) {
				list.add(Integer.valueOf(0));
			}

			// set values of grid to values of ArrayList (one row)
			for (int index = 0; index < grid.length; index++) {
				grid[i][index] = list.get(index).intValue();
			}
		}
	}

	/*
	 * Name: moveDown(Direction direction)
	 *
	 * Purpose: To move the tiles in the board as far down as they can go, while
	 * combining tiles of the same value. A tile can only be combined once per
	 * movement. Each row within the grid is checked for movement and
	 * successfully moved. This method relies on the use of an ArrayList to
	 * shift the tiles. It places non-zero tiles into an ArrayList, and combines
	 * adjacent tiles of the same value. It then adjusts the size of the
	 * ArrayList according to how many tiles of value 0 were removed from the
	 * list.
	 *
	 * Parameters: Direction direction which represents the direction in which
	 * the tiles will be moved
	 *
	 * Return: void
	 *
	 */
	private void moveDown(Direction direction) {
		// count used to determine how many zeros to be added after shift
		int newCount = 0;
		ArrayList<Integer> list;
		for (int j = 0; j < GRID_SIZE; j++) {
			int count = 0;
			// create new ArrayList everytime a new column is accessed
			list = new ArrayList<>();

			for (int i = 0; i < grid.length; i++) {
				// if value of grid element is 0, increment count
				if (grid[i][j] == 0) {
					count++;
				}

				// only add non-zero numbers to the ArrayList (shift)
				else if (grid[i][j] > 0) {
					list.add(Integer.valueOf(this.grid[i][j]));
				}
				newCount = count;
			}

			// start from left most element in ArrayList, iterate backwards
			for (int tile = list.size() - 1; tile >= 1; tile--) {
				// check if adjacent tile is same value
				if (list.get(tile).equals(list.get(tile - 1))) {

					// if so, double the right tile's value
					int value = Integer.valueOf(list.get(tile) * 2);
					list.set(tile, Integer.valueOf(value));

					// make the left tile 0
					list.set(tile - 1, Integer.valueOf(0));

					// increment score
					this.score = score + value;
				}
			}

			for (int removeTile = 0; removeTile < list.size(); removeTile++) {
				// remove newly made 0's from ArrayList
				if (list.get(removeTile) == 0) {
					list.remove(removeTile);

					// if you remove a 0, increment count
					newCount++;
				}
			}

			// count now represents the # of 0s you didn't add to list plus the
			// number you took away. Add this # back to beginning of ArrayList
			for (int addTile = 0; addTile < newCount; addTile++) {
				list.add(addTile, Integer.valueOf(0));
			}

			// set values of grid to values of ArrayList (one column)
			for (int index = 0; index < grid.length; index++) {
				grid[index][j] = list.get(index).intValue();
			}
		}
	}

	/*
	 * Name: moveUp(Direction direction)
	 *
	 * Purpose: To move the tiles in the board as far up as they can go, while
	 * combining tiles of the same value. A tile can only be combined once per
	 * movement. Each row within the grid is checked for movement and
	 * successfully moved. This method relies on the use of an ArrayList to
	 * shift the tiles. It places non-zero tiles into an ArrayList, and combines
	 * adjacent tiles of the same value. It then adjusts the size of the
	 * ArrayList according to how many tiles of value 0 were removed from the
	 * list.
	 *
	 * Parameters: Direction direction which represents the direction in which
	 * the tiles will be moved
	 *
	 * Return: void
	 *
	 */
	private void moveUp(Direction direction) {
		// count used to determine how many zeros to be added after shift
		int newCount = 0;
		ArrayList<Integer> list;
		for (int j = 0; j < GRID_SIZE; j++) {
			int count = 0;
			// create new ArrayList everytime a new column is accessed
			list = new ArrayList<>();

			for (int i = 0; i < grid.length; i++) {
				// if value of grid element is 0, increment count
				if (grid[i][j] == 0) {
					count++;
				}

				// only add non-zero numbers to the ArrayList (shift)
				else if (grid[i][j] > 0) {
					list.add(Integer.valueOf(this.grid[i][j]));
				}
				newCount = count;
			}

			// start from right most element in ArrayList, iterate forward
			for (int tile = 0; tile < list.size() - 1; tile++) {

				// check if adjacent tile is same value
				if (list.get(tile).equals(list.get(tile + 1))) {

					// if so, double the left tile's value
					int value = Integer.valueOf(list.get(tile) * 2);
					list.set(tile, Integer.valueOf(value));

					// make the adjacent right tile 0
					list.set(tile + 1, Integer.valueOf(0));

					// increment score
					this.score = score + value;
				}
			}

			for (int removeTile = 0; removeTile < list.size(); removeTile++) {
				// remove newly made 0's from ArrayList
				if (list.get(removeTile) == 0) {
					list.remove(removeTile);

					// if you remove a 0, increment count
					newCount++;
				}
			}

			// count now represents the # of 0s you didn't add to list plus the
			// number you took away. Add this # back to beginning of ArrayList
			for (int addTile = 0; addTile < newCount; addTile++) {
				list.add(Integer.valueOf(0));
			}

			// set values of grid to values of ArrayList (one column)
			for (int index = 0; index < grid.length; index++) {
				grid[index][j] = list.get(index).intValue();
			}
		}
	}

	/*
	 * Name: undo()
	 *
	 * Purpose: The purpose of this board is to copy the previously saved board
	 * (taken from the save() method) back into the grid instance variable. When
	 * undo() is called on the board, then board will revert back to this
	 * previously saved board.
	 *
	 * Parameters: none
	 *
	 * Return: void
	 */
	public void undo() {
		if (undoGrids.size() > 0) {
			this.setGrid(undoGrids.pop());
			this.score = this.undoScores.pop();
		}
	}

	@Override
	public String toString() {
		StringBuilder outputString = new StringBuilder();
		outputString.append(String.format("Score: %d\n", score));
		for (int row = 0; row < GRID_SIZE; row++) {
			for (int column = 0; column < GRID_SIZE; column++)
				outputString.append(grid[row][column] == 0 ? "    -" : String.format("%5d", grid[row][column]));

			outputString.append("\n");
		}
		return outputString.toString();
	}
}
