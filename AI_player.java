import java.util.List;
import java.util.Map;
import java.util.Random;

public class AI_player {
    private GameEngine engine= new GameEngine();
    private Random random = new Random();
    
    public int RandomMove(SenetState state, List<Integer> possibleMoves,int distance) {
        int randomIndex = random.nextInt(possibleMoves.size());
        int chosenMove = possibleMoves.get(randomIndex);
        return chosenMove;
    }
    public int decideBestMove(SenetState state, List<Integer> possibleMoves, int tossResult) {
        int bestMove = possibleMoves.get(0); 
        double maxScore = -Double.MAX_VALUE;

        for (int move : possibleMoves) {
            int target = move + tossResult;
            double currentScore = 0;
            if (target >= 30 || (move == 27 && tossResult == 3) || (move == 28 && tossResult == 2) || (move == 29)) {
                currentScore += 100000; 
            }

            if (target == 25 || (move == 25 && tossResult == 5)) {
                currentScore += 50000;
            }
            currentScore += (target * 1000);

            if (target < 30 && state.board[target] != 0 && state.board[target] != state.currentPlayer) {
                currentScore += 500; 
            }

            if (target == 26) {
                currentScore -= 80000; 
            }
            if (currentScore > maxScore) {
                maxScore = currentScore;
                bestMove = move;
            }
        }
        return bestMove; 
    }
    public int runExpectiminimax(SenetState state,List<Integer> possibleMoves ,int tossResult) {
        
        int bestMove = possibleMoves.get(0);
        double maxScore = -Double.MAX_VALUE;
        Map<Integer, Double> probabilities = Sticks.getProbabilities();
        int nodesExplored = 0;
        for (int move : possibleMoves) {
            int target = move + tossResult;
            if (target >= 30 || (move == 27 && tossResult == 3) || (move == 28 && tossResult == 2) || (move == 29)) return move;
            if (target == 25 || (move == 25 && tossResult == 5)) return move;
        }
        for (int move : possibleMoves) {
            nodesExplored++;
            SenetState tempState = state.copy();
            engine.movePiece(tempState, move, tossResult);
            double expectedForThisPiece = 0;
            
            for (int i = 1; i <= 5; i++) {
                nodesExplored++;
                tempState.switchPlayer();
                double worst = Double.MAX_VALUE;
                double score=0;
                List<Integer> Moves = engine.getAllPossibleMoves(tempState, i);
                if (Moves.isEmpty()) {
                    worst = Heuristic(tempState); 
                    expectedForThisPiece += probabilities.get(i) * worst;
                    tempState.switchPlayer();
                    continue;
                    }
                
                for(int tMove :Moves){
                    nodesExplored++;
                    SenetState tempState2 = tempState.copy();
                    engine.movePiece(tempState2, tMove, i);
                    score = Heuristic(tempState2);
                    if (score < worst) {
                        worst = score;
                    }
                }
                expectedForThisPiece += probabilities.get(i) * worst;
            }
            if (expectedForThisPiece > maxScore) {
                maxScore = expectedForThisPiece;
                bestMove = move;
            }
        }
        System.out.println(" Nodes explored: " + nodesExplored + "Score: " + bestMove);
        return bestMove;
    }

    
    public double Heuristic(SenetState state) {
        double score = 0;

        score -= state.blackPiecesOut * 2000000; 
        score += state.whitePiecesOut * 2000000; 

        for (int i = 0; i < 30; i++) {
            int piece = state.board[i];
            if (piece == 0) continue;

            double positionWeight;
            if (i < 10) { 
                positionWeight = i * 100;       
            } else if (i < 20) { 
                positionWeight = i * 1000;       
            } else { 
                positionWeight = i * 10000;     
            }

            if (piece == 2) { 
                score += positionWeight;

                if (i == SenetState.HOUSE_OF_HAPPINESS) score += 50000;
                if (i == SenetState.HOUSE_OF_THREE || i == SenetState.HOUSE_OF_TWO) score += 10000; 
                if (i == SenetState.HOUSE_OF_HORUS) score += 60000;

                if (i == SenetState.HOUSE_OF_WATER) score -= 150000;

            } else { 
                score -= positionWeight;
                if (i == SenetState.HOUSE_OF_HAPPINESS) score -= 50000;
                if (i == SenetState.HOUSE_OF_THREE || i == SenetState.HOUSE_OF_TWO) score-=10000;
                if (i == SenetState.HOUSE_OF_HORUS) score -= 60000;
                if (i == SenetState.HOUSE_OF_WATER) score += 150000;
                
            }
        }

        if (state.blackPiecesOut == 7) return -Double.MAX_VALUE;
        if (state.whitePiecesOut == 7) return +Double.MAX_VALUE;

        return score;

    }
}
