

public class Main {
    public static void main(String[] args) {
        new SenetGUI();


//        // 1. Initialize core objects
//        SenetState state = new SenetState();
//        GameEngine engine = new GameEngine();
//        Scanner scanner = new Scanner(System.in);
//
//        System.out.println("--- Welcome to the Game of Senet ---");
//        System.out.println("Player 1: Black (1) | Player 2: White (2)");
//
//        // 2. Main Game Loop
//        while (!state.isGameOver()) {
//            System.out.println("\n======================================");
//            System.out.println(state.toString());
//
//            // A. Toss the sticks
//            System.out.println("Press Enter to toss the sticks...");
//            scanner.nextLine();
//            int steps = Sticks.toss();
//            System.out.println("Toss Result: " + steps);
//
//            // B. Get possible moves for this toss
//            List<Integer> possibleMoves = engine.getAllPossibleMoves(state, steps);
//
//            if (possibleMoves.isEmpty()) {
//                System.out.println("No legal moves available! Skipping turn...");
//            } else {
//                // C. Display possible moves to the player
//                System.out.print("Possible moves from squares: ");
//                for (int move : possibleMoves) {
//                    System.out.print((move + 1) + " "); // +1 for human-readable display (1-30)
//                }
//
//                // D. Handle player input
//                int choice = -1;
//                while (true) {
//                    System.out.print("\nSelect a square to move from: ");
//                    try {
//                        String input = scanner.nextLine();
//                        choice = Integer.parseInt(input) - 1;
//
//                        if (possibleMoves.contains(choice)) {
//                            break;
//                        }
//                        System.out.println("Invalid move. Please choose from the available squares.");
//                    } catch (NumberFormatException e) {
//                        System.out.println("Invalid input. Please enter a valid square number.");
//                    }
//                }
//
//                // E. Execute the move
//                engine.movePiece(state, choice, steps);
//                System.out.println("Move executed successfully!");
//            }
//
//            // F. Switch Player Turn
//            state.switchPlayer();
//        }
//
//        // 3. Declare Winner
//        System.out.println("\n**************************************");
//        System.out.println("GAME OVER!");
//        if (state.blackPiecesOut == 5) {
//            System.out.println("The Winner is Player 1 (Black)!");
//        } else {
//            System.out.println("The Winner is Player 2 (White)!");
//        }
//
//        scanner.close();
    }
}