import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class SenetGUI extends JFrame {
    private SenetState state = new SenetState();
    private GameEngine engine = new GameEngine();
    private JButton[] grid = new JButton[30];
    private JLabel lblStatus, lblDice, lblBOut, lblWOut;
    private JButton btnToss, btnComputer;

    private int distance = 0;
    private boolean canMove = false;
    private int selectedIdx = -1;
    private int targetIdx = -1;

    private JRadioButton p1Human, p1Comp, p2Human, p2Comp;

    public SenetGUI() {
    setTitle("Senet Algorithm Project ");
    setSize(1150, 800);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLayout(new BorderLayout(10, 10));

    add(createTopPanel(), BorderLayout.NORTH);

    add(createBoardPanel(), BorderLayout.CENTER);

    add(createBottomPanel(), BorderLayout.SOUTH);


    ActionListener updateAction = e -> updateTurnStatus();
    p1Human.addActionListener(updateAction); p1Comp.addActionListener(updateAction);
    p2Human.addActionListener(updateAction); p2Comp.addActionListener(updateAction);

    updateTurnStatus();
    refreshUI();
    
    setVisible(true);
}

    public static void main(String[] args) {
        new SenetGUI();
    }


    private JPanel createBoardPanel() {
    JPanel board = new JPanel(new GridLayout(3, 10, 5, 5));
    board.setBackground(new Color(101, 67, 33)); 
    board.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    for (int row = 0; row < 3; row++) {
        if (row == 0) { 
            for (int i = 0; i <= 9; i++) createSquare(board, i);
        } else if (row == 1) { 
            for (int i = 19; i >= 10; i--) createSquare(board, i);
        } else { 
            for (int i = 20; i <= 29; i++) createSquare(board, i);
        }
    }
    return board;
    }

    private void createSquare(JPanel panel, int i) {
        grid[i] = new JButton(String.valueOf(i + 1));
        grid[i].setPreferredSize(new Dimension(70, 70));
        grid[i].setFont(new Font("Arial", Font.BOLD, 16));
        grid[i].addActionListener(e -> handleSquareClick(i));
        
        if (i == 25) {
            grid[i].setBackground(Color.GREEN);
            grid[i].setText("☀");
        } else if (i == 26) { 
            grid[i].setBackground(Color.CYAN);
            grid[i].setText("≈");
        } else if (i == 14) { 
            grid[i].setBackground(Color.ORANGE);
        } else if (i >= 27) { 
            grid[i].setBackground(Color.YELLOW);
        } else {
            grid[i].setBackground(new Color(245, 222, 179)); 
        }
        
        panel.add(grid[i]);
    }
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        lblStatus = new JLabel("Turn: BLACK", 0);
        lblDice = new JLabel("Dice: ", 0);
        infoPanel.add(lblStatus); infoPanel.add(lblDice);

        JPanel scorePanel = new JPanel(new GridLayout(2, 1));
        lblBOut = new JLabel("Black Out: 0", 0);
        lblWOut = new JLabel("White Out: 0", 0);
        scorePanel.add(lblBOut); scorePanel.add(lblWOut);

        JPanel settingsPanel = new JPanel(new GridLayout(2, 3));
        p1Human = new JRadioButton("Human", true); p1Comp = new JRadioButton("Computer", false);
        p2Human = new JRadioButton("Human", true); p2Comp = new JRadioButton("Computer", false);
        ButtonGroup g1 = new ButtonGroup(); g1.add(p1Human); g1.add(p1Comp);
        ButtonGroup g2 = new ButtonGroup(); g2.add(p2Human); g2.add(p2Comp);

        settingsPanel.add(new JLabel("P1 (Black):")); settingsPanel.add(p1Human); settingsPanel.add(p1Comp);
        settingsPanel.add(new JLabel("P2 (White):")); settingsPanel.add(p2Human); settingsPanel.add(p2Comp);

        topPanel.add(infoPanel, BorderLayout.WEST);
        topPanel.add(scorePanel, BorderLayout.CENTER);
        topPanel.add(settingsPanel, BorderLayout.EAST);
        
        return topPanel;
    }
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        btnToss = new JButton("Dice (Toss)");
        btnToss.addActionListener(e -> handleToss());
        btnComputer = new JButton("Execute AI Move");
        btnComputer.addActionListener(e -> runComputerAlgorithm());
        bottomPanel.add(btnToss);
        bottomPanel.add(btnComputer);
        return bottomPanel;
    }

    private void refreshUI() {
        lblBOut.setText("Black Out: " + state.blackPiecesOut);
        lblWOut.setText("White Out: " + state.whitePiecesOut);
        for (int i = 0; i < 30; i++) {
            int val = state.board[i];
            resetSquareColor(i);
            if (val == 1) {
                grid[i].setText("B"); grid[i].setForeground(Color.WHITE);
                grid[i].setBackground(i == selectedIdx ? Color.GRAY : Color.BLACK);
            } else if (val == 2) {
                grid[i].setText("W"); grid[i].setForeground(Color.BLACK);
                grid[i].setBackground(i == selectedIdx ? Color.LIGHT_GRAY : Color.WHITE);
            } else {
                grid[i].setText(String.valueOf(i + 1));
                grid[i].setForeground(Color.BLACK);
            }
            if (i == targetIdx) {
                grid[i].setBackground(Color.RED);
                grid[i].setText("OK");
            }
        }
        if (targetIdx == -2) targetIdx = selectedIdx;
    }

    private void resetSquareColor(int i) {
        if (i == 25) grid[i].setBackground(Color.GREEN);      
        else if (i == 26) grid[i].setBackground(Color.CYAN);  
        else if (i == 14) grid[i].setBackground(Color.ORANGE);  
        else if (i >= 27) grid[i].setBackground(Color.YELLOW);  
        else grid[i].setBackground(new Color(245, 222, 179));   
    }
    //======================================================================================
    private void handleSquareClick(int i) {
        if (isCurrentPlayerComputer()) return; 
        if (!canMove) return;                  

        if (selectedIdx != -1 && i == targetIdx) {
            executeConfirmedMove(); 
            return;                 
        }

        if (state.board[i] == state.currentPlayer) {
            if (engine.isValidMove(state, i, distance)) {
                selectedIdx = i;           
                targetIdx = calculateTarget(i); 
                refreshUI();              
            }
        }
    }
    private int calculateTarget(int i) {
        int target = i + distance;
        return (target >= 30) ? -2 : target; 
    }
    //====================================================
    //======================================================================================
    //هون منطق التحريك والفوز 
    private boolean isCurrentPlayerComputer() {
    if (state.currentPlayer == 1) {
        return p1Comp.isSelected(); 
    } 
    else {
        return p2Comp.isSelected();
        }
    }
    //----------------------------------
    private void checkForVictory() {
        int winner = engine.getWinner(state);

        if (winner != 0) {
            String winnerName = (winner == 1) ? "BLACK" : "WHITE";
            JOptionPane.showMessageDialog(this, "The winner is: " + winnerName);
            System.exit(0); 
        }
    }
    //----------------------------------
    private void executeConfirmedMove() {
        if (engine.movePiece(state, selectedIdx, distance)) {
            refreshUI();
            if (state.isGameOver()) {
                JOptionPane.showMessageDialog(this, "Winner");
                System.exit(0);
            }
            checkForVictory();
            finishTurn();
        }
    }
    //----------------------------------
    private void finishTurn() {
        state.switchPlayer();
        canMove = false;
        selectedIdx = -1;
        targetIdx = -1;
        updateTurnStatus();
        refreshUI();
    }
    //----------------------------------
    private void updateTurnStatus() {
    boolean isComp = isCurrentPlayerComputer();
    
    if (isComp) {
        btnToss.setEnabled(false);
        btnComputer.setEnabled(true);
        btnComputer.setBackground(Color.ORANGE);
    } else {
        btnToss.setEnabled(true);
        btnComputer.setEnabled(false);
        btnComputer.setBackground(null);
    }
}
    //-----------------------------------
    private void handleToss() {
    distance = Sticks.toss();
    
    String color = (state.currentPlayer == 1) ? "BLACK" : "WHITE";
    lblStatus.setText("Turn: " + color + " (Human)");
    lblDice.setText("Dice Value: " + distance);
    
    canMove = true;
    btnToss.setEnabled(false);

    if (engine.getAllPossibleMoves(state, distance).isEmpty()) {
        engine.checkStuckPenalty(state, distance);
        JOptionPane.showMessageDialog(this, "No possible moves! Stuck penalty applied.");
        finishTurn();
    }
}
    //------------------------------------
    
    // تابع تشغيل الخوارزمية 
    private AI_player aiLogic = new AI_player(); 

    private void runComputerAlgorithm() {
        distance = Sticks.toss();
        String color = (state.currentPlayer == 1) ? "BLACK" : "WHITE";
        lblStatus.setText("Turn: " + color + " (Computer)");
        lblDice.setText("AI Dice: " + distance);

        List<Integer> possibleMoves = engine.getAllPossibleMoves(state, distance);

        if (possibleMoves.isEmpty()) {
            engine.checkStuckPenalty(state, distance);
            JOptionPane.showMessageDialog(this, "Computer has no moves!");
        } else {
            
            int bestMoveFrom = aiLogic.runExpectiminimax(state, possibleMoves, distance);
            engine.movePiece(state, bestMoveFrom, distance);
        }

        refreshUI();
        checkForVictory();
        finishTurn();
    }
}

