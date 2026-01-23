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
        public int runExpectiminimaxGeneric(SenetState state, List<Integer> possibleMoves, int tossResult, int depth) {
            int aiPlayer = state.currentPlayer;
            int bestMove = possibleMoves.get(0);
            double maxScore = -Double.MAX_VALUE;
            
            for (int move : possibleMoves) {
                int target = move + tossResult;
                if (target >= 30 || (move == 27 && tossResult == 3) || (move == 28 && tossResult == 2) || (move == 29))
                    return move;
            }

            for (int move : possibleMoves) {
                SenetState tempState = state.copy();
                engine.movePiece(tempState, move, tossResult);
                
                double score = expectiminimax(tempState, depth - 1, false, aiPlayer);
                
                if (score > maxScore) {
                    maxScore = score;
                    bestMove = move;
                }
            }
            return bestMove;
        }

        private double expectiminimax(SenetState state, int depth, boolean isMax, int aiPlayer) {
            if (depth <= 0 || state.isGameOver()) {
                return Heuristic(state, aiPlayer);
            }
            
            return chanceNode(state, depth - 1, isMax, aiPlayer);
        }

        private double chanceNode(SenetState state, int depth, boolean nextIsMax, int aiPlayer) {
            double expectedValue = 0;
            Map<Integer, Double> probs = Sticks.getProbabilities();
            
            SenetState nextTurnState = state.copy();
            nextTurnState.switchPlayer(); 

            for (int toss = 1; toss <= 5; toss++) {
                List<Integer> moves = engine.getAllPossibleMoves(nextTurnState, toss);
                double val;
                
                if (moves.isEmpty()) {
                    val = Heuristic(nextTurnState, aiPlayer);
                } else {
                    if (nextIsMax) {
                        val = -Double.MAX_VALUE;
                        for (int m : moves) {
                            SenetState nextState = nextTurnState.copy();
                            engine.movePiece(nextState, m, toss);
                            val = Math.max(val, expectiminimax(nextState, depth, false, aiPlayer));
                        }
                    } else {
                        val = Double.MAX_VALUE;
                        for (int m : moves) {
                            SenetState nextState = nextTurnState.copy();
                            engine.movePiece(nextState, m, toss);
                            val = Math.min(val, expectiminimax(nextState, depth, true, aiPlayer));
                        }
                    }
                }
                expectedValue += probs.get(toss) * val;
            }
            return expectedValue;
        }
    public int runExpectiminimax3(SenetState state, List<Integer> possibleMoves, int tossResult) {
        int aiPlayer = state.currentPlayer;
        int bestMove = possibleMoves.get(0);
        double maxScore = -Double.MAX_VALUE;
        Map<Integer, Double> probabilities = Sticks.getProbabilities();
        int nodesExplored = 0;

        System.out.println("\n===== Starting Expectiminimax Search (Depth 3) =====");
        for (int move : possibleMoves) {
            int target = move + tossResult;
            if (target >= 30 || (move == 27 && tossResult == 3) || (move == 28 && tossResult == 2) || (move == 29))
                return move;
            if (target == 25||  (move == 25 && tossResult == 5))
                return move;
        }
        //Max Node
        for (int move : possibleMoves) {
            nodesExplored++;
            System.out.println("\n[MAX Node] Analyzing Move from Square: " + (move + 1));
            SenetState tempState = state.copy();
            engine.movePiece(tempState, move, tossResult);
            double expectedForThisPiece = 0;
            //Chance Node
            for (int i = 1; i <= 5; i++) {
                nodesExplored++;
                System.out.println("   [CHANCE Node] Processing Toss: " + i + " | Probability: " + probabilities.get(i));
                tempState.switchPlayer();
                double worst = Double.MAX_VALUE;
                List<Integer> oppMoves = engine.getAllPossibleMoves(tempState, i);
                //Min Node
                if (oppMoves.isEmpty()) {
                    worst = Heuristic(tempState,aiPlayer);
                    System.out.println("      - [MIN Node] Opponent is stuck. Processed Heuristic: " + worst);
                    expectedForThisPiece += probabilities.get(i) * worst;
                    tempState.switchPlayer();
                    continue;
                }
                for (int oppMove : oppMoves) {
                    nodesExplored++;
                    System.out.println("      [MIN Node] Evaluating Opponent Move from Square: " + (oppMove + 1));
                    SenetState tempState2 = tempState.copy();
                    engine.movePiece(tempState2, oppMove, i);
                    double bestScore = -Double.MAX_VALUE;
                    //chance Node
                    for (int j = 1; j <= 5; j++) {
                        List<Integer> aiMoves = engine.getAllPossibleMoves(tempState2, j);
                        //Max Node
                        if (aiMoves.isEmpty()) {
                            bestScore = Math.max(bestScore, Heuristic(tempState2,aiPlayer));
                            continue;
                        }
                        for (int aiMove : aiMoves) {
                            SenetState tempState3 = tempState2.copy();
                            engine.movePiece(tempState3, aiMove, j);
                            bestScore = Math.max(bestScore, Heuristic(tempState3,aiPlayer));
                        }
                    }

                    if (bestScore < worst) {
                        worst = bestScore;
                    }
                }
                System.out.println("      => Returned Value from MIN (Worst Case for AI): " + worst);
                expectedForThisPiece += probabilities.get(i) * worst;
                tempState.switchPlayer();
            }
            System.out.println("   >>> Final Expected Value for Move " + (move + 1) + ": " + expectedForThisPiece);
            if (expectedForThisPiece > maxScore) {
                maxScore = expectedForThisPiece;
                bestMove = move;
            }
        }
        System.out.println("\n------------------------------------------");
        System.out.println("Total Nodes Explored: " + nodesExplored);
        System.out.println("Best Heuristic Value (Chosen Move): " + maxScore);
        System.out.println("Final Decision: Move piece from Square " + (bestMove + 1));
        System.out.println("------------------------------------------\n");
        return bestMove;
    }

    public int runExpectiminimax(SenetState state,List<Integer> possibleMoves ,int tossResult) {
        int aiPlayer = state.currentPlayer;
        int bestMove = possibleMoves.get(0);
        double maxScore = -Double.MAX_VALUE;
        Map<Integer, Double> probabilities = Sticks.getProbabilities();
        int nodesExplored = 0;
        System.out.println("\n=====Algorithm with depth 2=====");
        for (int move : possibleMoves) {
            int target = move + tossResult;
            if (target >= 30 || (move == 27 && tossResult == 3) || (move == 28 && tossResult == 2) || (move == 29)) return move;
            if (target == 25 || (move == 25 && tossResult == 5)) return move;
        }
        for (int move : possibleMoves) {
            System.out.println("\n[MAX Node] Evaluating Move from Square: " + (move + 1));
            nodesExplored++;
            SenetState tempState = state.copy();
            engine.movePiece(tempState, move, tossResult);
            double expectedForThisPiece = 0;
            
            for (int i = 1; i <= 5; i++) {
                
                nodesExplored++;
                System.out.println("   [CHANCE Node] Processing Toss: " + i + " | Probability: " + probabilities.get(i));
                tempState.switchPlayer();
                double worst = Double.MAX_VALUE;
                double score=0;
                List<Integer> Moves = engine.getAllPossibleMoves(tempState, i);
                if (Moves.isEmpty()) {
                    
                    worst = Heuristic(tempState,aiPlayer); 
                    System.out.println("      - [MIN Node] Opponent has no moves. Processed Heuristic: " + worst);
                    expectedForThisPiece += probabilities.get(i) * worst;
                    tempState.switchPlayer();
                    continue;
                    }
                for(int tMove :Moves){
                    nodesExplored++;
                    SenetState tempState2 = tempState.copy();
                    engine.movePiece(tempState2, tMove, i);
                    score = Heuristic(tempState2,aiPlayer);
                    System.out.println("      - [MIN Node] Opponent Move from " + (tMove + 1) + " | Processed Score: " + score);
                    if (score < worst) {
                        worst = score;
                    }
                }
                System.out.println("      => Returned Value from MIN (Worst Case): " + worst);
                expectedForThisPiece += probabilities.get(i) * worst;
                tempState.switchPlayer();
            }
            System.out.println("   >>> Returned Final Expected Value for Piece " + (move + 1) + ": " + expectedForThisPiece);
            if (expectedForThisPiece > maxScore) {
                maxScore = expectedForThisPiece;
                bestMove = move;
            }
        }
        System.out.println("\n------------------------------------------");
        System.out.println("Total Nodes Explored: " + nodesExplored);
        System.out.println("Best Heuristic Value (Max Score): " + maxScore);
        System.out.println("Final Chosen Move: " + (bestMove + 1));
        System.out.println("------------------------------------------\n");
        return bestMove;
    }

    
    public double Heuristic(SenetState state,int aiPlayer) {
        double score = 0;
        
        int opponent = (aiPlayer == 1) ? 2 : 1;
        int myOut, oppOut;
        if (aiPlayer == 1) {
            myOut = state.blackPiecesOut;
            oppOut = state.whitePiecesOut;
        } else {
            myOut = state.whitePiecesOut;
            oppOut = state.blackPiecesOut;
}
        score += myOut * 2000000; 
        score -= oppOut * 2000000;

        for (int i = 0; i < 30; i++) {
            int piece = state.board[i];
            if (piece == 0) continue;
            double positionWeight = Math.pow(i + 1, 3);
            if (piece == aiPlayer) { 
                if (i >= 25 && i!= SenetState.HOUSE_OF_WATER) { 
                    score += 20000000.0; 
                } else if (i >= 20) { 
                    score += 5000000.0; 
                } else if (i >= 15) { 
                    score += 1000000.0; 
                } else if (i >= 10) { 
                    score += 100000.0;
                }
                score += positionWeight;
                if (i < 10) score -= 500000;
                if (i < 20) score -= 250000;
                if (i == SenetState.HOUSE_OF_HAPPINESS) score += 5000000;
                if (i == SenetState.HOUSE_OF_THREE || i == SenetState.HOUSE_OF_TWO) score += 100000; 
                if (i == SenetState.HOUSE_OF_HORUS) score += 60000000;
                if (i == SenetState.HOUSE_OF_WATER) score -= 1500000000;

            } else if (piece == opponent) { 
                score -= positionWeight;
                if (i < 10) score += 5000;
                if (i < 20) score += 2500;
                if (i == SenetState.HOUSE_OF_HAPPINESS) score -= 5000000;
                if (i == SenetState.HOUSE_OF_THREE || i == SenetState.HOUSE_OF_TWO) score-=100000;
                if (i == SenetState.HOUSE_OF_HORUS) score -= 60000000 ;
                if (i == SenetState.HOUSE_OF_WATER) score += 150000000;
                
            }
        }
        if (myOut == 7) return Double.MAX_VALUE;
        if (oppOut == 7) return -Double.MAX_VALUE;
        return score;

    }
}
