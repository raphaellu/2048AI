import java.util.ArrayList;
import java.util.Random;

public class Expand {

    public static void main(String[] args) {

        Random generator = new Random(9);
        Board board = new Board(generator, 3);
        expand(board, Direction.UP);
    }

    public static ArrayList<Board> expand(Board s, Direction direction){
        ArrayList<Board> boards = new ArrayList<>();
        System.out.println("Original state");
        System.out.println(s.toString());

        System.out.println("After move:");
        s.move(direction);
        System.out.println(s.toString());

        System.out.println("Possible new states:\n");
        ArrayList<Cell> emptyCells = s.getEmptyCells();
        int i = 1;
        for (Cell cell : emptyCells){
            System.out.println("State: " + i + "\n----------------");
            Board temp = new Board(new Random(0), s.GRID_SIZE);
            temp.setGrid(s.getGrid());
            temp.addTile(cell.getX(), cell.getY());
            System.out.println(temp.toString());
            boards.add(temp);
            i++;
        }
        return boards;
    }
}
