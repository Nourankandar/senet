import java.util.ArrayList;
import java.util.List;

public class GameEngine {

    public boolean movePiece(SenetState state, int fromIndex, int steps) {
        if (!isValidMove(state, fromIndex, steps)) return false;

        int[] exits = {SenetState.HOUSE_OF_THREE, SenetState.HOUSE_OF_TWO};
        for (int h : exits) {
            if (state.board[h] == state.currentPlayer && fromIndex != h) {
                moveToRebirth(state, h);
                return true;
            }
        }

        int target = fromIndex + steps;
        if (fromIndex == SenetState.HOUSE_OF_THREE && steps == 3) {
            exitPiece(state, fromIndex);
            return true;
        }
        else if (fromIndex == SenetState.HOUSE_OF_TWO && steps == 2) {
            exitPiece(state, fromIndex);
            return true;
        }
        else if (target == SenetState.HOUSE_OF_WATER) {
            state.board[fromIndex] = 0;
            moveToRebirth(state, -1);
            return true;
        }
        else if (target >= 30) {
                exitPiece(state, fromIndex);
                return true;
            }
        else {
            int oldPiece = state.board[target];
            if (oldPiece != 0 && oldPiece != state.currentPlayer) {
            state.board[target] = state.currentPlayer;
            state.board[fromIndex] = oldPiece;
            return true;
            } 
            else {
                state.board[target] = state.currentPlayer;
                state.board[fromIndex] = 0;
                return true;
            
            }
        }
        
    }
    public boolean isValidMove(SenetState state, int from, int distance) {
        //مافيه يحرك قطعة مو الو 
        if (state.board[from] != state.currentPlayer)
            return false;

        int to = from + distance;

        if (from == SenetState.HOUSE_OF_THREE && distance != 3) return false;
        if (from == SenetState.HOUSE_OF_TWO && distance != 2) return false;
        // if (to == SenetState.HOUSE_OF_WATER) return false;
        // هاد بيت السعادة لازم نمر فوقه مافينا نتخطاه
        if (from < SenetState.HOUSE_OF_HAPPINESS && to > SenetState.HOUSE_OF_HAPPINESS) return false;

        if (to < 30 && state.board[to] == state.currentPlayer) return false;

        return true;
    }
    //هاد اذا ماعندو حركات متاحة وفي حجر ب 2 او 3 بيرجع للبعث
    public void checkStuckPenalty(SenetState state, int distance) {
        if (getAllPossibleMoves(state, distance).isEmpty()) {
            if (state.board[SenetState.HOUSE_OF_THREE] == state.currentPlayer)
                moveToRebirth(state, SenetState.HOUSE_OF_THREE);
            if (state.board[SenetState.HOUSE_OF_TWO] == state.currentPlayer)
                moveToRebirth(state, SenetState.HOUSE_OF_TWO);
        }
    }

    private void moveToRebirth(SenetState state, int idx) {
        if (idx != -1) state.board[idx] = 0 ;
        System.out.println(state.board[SenetState.HOUSE_OF_WATER]);
        int p = SenetState.HOUSE_OF_REBIRTH;
        while (p >= 0 && state.board[p] != 0) p--;
        if (p >= 0) state.board[p] = state.currentPlayer;
        else {
            for (int i = 0; i < 30; i++) if (state.board[i] == 0) { state.board[i] = state.currentPlayer; break; }
        }
    }
    //هاد ليعد الاحجار اللي بتطلع
    private void exitPiece(SenetState state, int idx) {
        state.board[idx] = 0;
        if (state.currentPlayer == 1) state.blackPiecesOut++;
        else state.whitePiecesOut++;
    }
    
    public List<Integer> getAllPossibleMoves(SenetState state, int distance) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            if (isValidMove(state, i, distance)) {
                list.add(i);
            }
        }
        // print(list, distance);
        return list;
    }

    public int getWinner(SenetState state) {
        if (state.blackPiecesOut == 7) {
            return 1;
        }
        if (state.whitePiecesOut == 7) {
            return 2;
        }
        return 0;
    }

    public void print( List<Integer> list, int distance) {
        System.out.println("\n-------------------------------------------");
        if (list.isEmpty()) {
            System.out.println("can't move for : " + distance);
        } else {
            System.out.println("moves for  [" + distance + "]:");
            for (int startIdx : list) {
                int targetIdx = startIdx + distance;
                String destination;
                if (targetIdx >= 30) {
                    destination = "Outside 'win' ";
                } else if (targetIdx+1 == SenetState.HOUSE_OF_WATER +1) {
                    destination = "to 27 house of water";
                } else if (targetIdx+1 == SenetState.HOUSE_OF_THREE +1) {
                    destination = "to 28 house of three";
                } else if (targetIdx+1 == SenetState.HOUSE_OF_HAPPINESS +1) {
                    destination = " to 26 house of happiness";
                } else if (targetIdx+1 == SenetState.HOUSE_OF_HORUS +1) {
                    destination = " to 30 house of horus";
                } else if (targetIdx+1 == SenetState.HOUSE_OF_TWO +1) {
                    destination = " to 29 house of two";

                } else {
                    destination = " to " + (targetIdx + 1);
                }
                System.out.println("from " + (startIdx + 1) + " " + destination);
            }
        }
        System.out.println("-------------------------------------------");

    }
}