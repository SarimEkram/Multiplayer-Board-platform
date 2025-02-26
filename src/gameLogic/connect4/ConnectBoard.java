package gameLogic.connect4;

public class ConnectBoard {
    private static int row;
    private static int column;
    private static int[][] board;

    public ConnectBoard(int row, int column){
        this.row = row;
        this.column = column;
        board = createBoard();
    }

    /**
     * This function creates a Board using the row and columns inputted by the user.
     * This will be called in the gui controller class after the user decides to play connect 4 and selecting the size.
     * @return a 2D array filled with an integer value which will be considered for an empty spot.
     */
    public static int[][] createBoard(){
        return null;
    }

    /**
     * This function counts the number of rows present in the board. This can be used in other functions of connect4 class
     * @param board a 2D array board of size rows dimension 1 and columns dimension 2
     * @return the length of the board which is the length of the number of rows.
     */
    public static int rowCount(int[][] board){
        return row;
    }

    /**
     * This function counts the number of columns present in the board. This can be used in other functions of connect4 class
     * @param board a 2D array board of size rows dimension 1 and columns dimension 2
     * @return the length of the column which is the length of the number of columns.
     */
    public static int columnCount(int[][] board){
        return column;
    }

    /**
     * This function counts the number of columns present in the board. This can be used in other functions of connect4 class
     * @return the board 2D array
     */
    public static int[][] getBoard(){
        return board;
    }

}
