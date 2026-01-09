import java.util.Arrays;

public class SenetState {
    public int[] board = new int[30];
    public int currentPlayer = 1; 
    public int blackPiecesOut = 0;
    public int whitePiecesOut = 0;

    public static final int HOUSE_OF_REBIRTH = 14;   
    public static final int HOUSE_OF_HAPPINESS = 25; 
    public static final int HOUSE_OF_WATER = 26;     
    public static final int HOUSE_OF_THREE = 27;    
    public static final int HOUSE_OF_TWO = 28;      
    public static final int HOUSE_OF_HORUS = 29;     

    public SenetState() {
        setupBoard();
    }

    private void setupBoard() {
        for (int i = 0; i < 14; i++) {
            board[i] = (i % 2 == 0) ? 2 : 1;
        }
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }

    public boolean isGameOver() {
        
        return blackPiecesOut == 7 || whitePiecesOut == 7;
    }

    public SenetState copy() {
        SenetState newState = new SenetState();
        newState.board = this.board.clone();
        newState.currentPlayer = this.currentPlayer;
        newState.blackPiecesOut = this.blackPiecesOut;
        newState.whitePiecesOut = this.whitePiecesOut;
        return newState;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Current Player: ").append(currentPlayer == 1 ? "Black" : "White").append("\n");
        sb.append("Board: ").append(Arrays.toString(board)).append("\n");
        sb.append("Out -> Black: ").append(blackPiecesOut).append(", White: ").append(whitePiecesOut);
        return sb.toString();
    }
}