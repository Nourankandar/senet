import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.List;

public class SenetGUI extends JFrame {
    private SenetState state = new SenetState();
    private GameEngine engine = new GameEngine();
    private JButton[] grid = new JButton[30];
    private JLabel lblStatus, lblDice, lblBOut, lblWOut;
    private JButton btnToss, btnComputer,btnRestart;

    private int distance = 0;
    private boolean canMove = false;
    private int selectedIdx = -1;
    private int targetIdx = -1;

    private JRadioButton p1Human, p1Comp, p2Human, p2Comp;
    private JComboBox<String> p1AlgoChoice;
    private JComboBox<String> p2AlgoChoice;
    
    private final String[] ALGORITHMS = {"Random", "decideBestMove", "Expectiminimax 2", "Expectiminimax 3", "Expectiminimax 4"};
    
    //ألوان الخلفية والمربعات
    private final Color BOARD_BG = new Color(109, 54, 36);
    private final Color SQUARE_BG = new Color(165, 135, 84);
    private final Color PANEL_BG = new Color(210, 195, 160);

    //ألوان المربعات الخاصة
    private final Color SPECIAL_WATER = new Color(42, 105, 140);
    private final Color SPECIAL_REBIRTH = new Color(184, 115, 51);
    private final Color SPECIAL_HAPPINESS = new Color(85, 140, 85);
    private final Color SPECIAL_EXIT = new Color(201, 168, 73);

    // ألوان القطع
    private final Color PIECE_BLACK_COLOR = new Color(40, 40, 40);
    private final Color PIECE_WHITE_COLOR = new Color(245, 245, 235);
    private final Color HIGHLIGHT_SELECTED = new Color(135, 175, 225);
    private final Color HIGHLIGHT_TARGET = new Color(180, 60, 60);

    //الخطوط
    private final Font EGYPTIAN_FONT_L = new Font("Serif", Font.BOLD, 40);
    private final Font EGYPTIAN_FONT_M = new Font("SansSerif", Font.BOLD, 20);
    private final Font EGYPTIAN_FONT_S = new Font("SansSerif", Font.PLAIN, 20);


    public SenetGUI() {
        setTitle("Senet Algorithm Project");
        setSize(1150, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BOARD_BG);

       //هون الأيقونة
        //هون الأيقونة
        try {
            InputStream imgStream = getClass().getResourceAsStream("/senet_icon.jpg");
            if (imgStream != null) {
                Image icon = ImageIO.read(imgStream);
                this.setIconImage(icon);
            } else {
                System.out.println("لم يتم العثور على ملف الصورة! تأكد من وجوده داخل مجلد src");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //اللوحات
        add(createTopPanel(), BorderLayout.NORTH);

        add(createBoardPanel(), BorderLayout.CENTER);

        add(createBottomPanel(), BorderLayout.SOUTH);


        ActionListener updateAction = e -> updateTurnStatus();
        p1Human.addActionListener(updateAction); p1Comp.addActionListener(updateAction);
        p2Human.addActionListener(updateAction); p2Comp.addActionListener(updateAction);

        updateTurnStatus();
        refreshUI();

        setLocationRelativeTo(null); 
        setVisible(true);
    }

    public static void main(String[] args) {

        new SenetGUI();
    }


    private JPanel createBoardPanel() {
        JPanel boardWrapper = new JPanel(new BorderLayout());
        boardWrapper.setBackground(BOARD_BG);
        //إطار حول اللوحة
        boardWrapper.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(15, 20, 15, 20),
                BorderFactory.createLoweredBevelBorder()
        ));

        JPanel board = new JPanel(new GridLayout(3, 10, 3, 3));
        board.setBackground(BOARD_BG);
        board.setBorder(BorderFactory.createLineBorder(new Color(60, 30, 10), 5));

        for (int row = 0; row < 3; row++) {
            if (row == 0) {
                for (int i = 0; i <= 9; i++) createSquare(board, i);
            } else if (row == 1) {
                for (int i = 19; i >= 10; i--) createSquare(board, i);
            } else {
                for (int i = 20; i <= 29; i++) createSquare(board, i);
            }
        }
        boardWrapper.add(board, BorderLayout.CENTER);
        
        return boardWrapper;
    }

    private void createSquare(JPanel panel, int i) {
        grid[i] = new JButton(String.valueOf(i + 1));
        grid[i].setPreferredSize(new Dimension(80, 80));
        grid[i].setFont(EGYPTIAN_FONT_M);
        grid[i].setFocusPainted(false);
        grid[i].setBorder(BorderFactory.createRaisedBevelBorder());

        grid[i].addActionListener(e -> handleSquareClick(i));

        //تطبيق الألوان ع المربعات
        if (i == 25) {
            grid[i].setBackground(SPECIAL_HAPPINESS);
            grid[i].setForeground(Color.WHITE);
            grid[i].setText("☀");
        } else if (i == 26) {
            grid[i].setBackground(SPECIAL_WATER);
            grid[i].setForeground(Color.WHITE);
            grid[i].setText("≈");
        } else if (i == 14) {
            grid[i].setBackground(SPECIAL_REBIRTH);
            grid[i].setForeground(Color.WHITE);
        } else if (i >= 27) {
            grid[i].setBackground(SPECIAL_EXIT);
        } else {
            grid[i].setBackground(SQUARE_BG);
        }

        panel.add(grid[i]);
    }

    private JPanel createTopPanel() {
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PANEL_BG);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,3,0, BOARD_BG.darker()),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);
        lblStatus = new JLabel("Turn: BLACK", JLabel.LEFT);
        lblStatus.setFont(EGYPTIAN_FONT_M);
        lblStatus.setForeground(BOARD_BG.darker());

        lblDice = new JLabel("Dice: -", JLabel.LEFT);
        lblDice.setFont(EGYPTIAN_FONT_M);
        lblDice.setForeground(SPECIAL_REBIRTH.darker());
        infoPanel.add(lblStatus); infoPanel.add(lblDice);

        JPanel scorePanel = new JPanel(new GridLayout(2, 1));
        scorePanel.setOpaque(false);
        lblBOut = new JLabel("Black Out: 0", JLabel.CENTER);
        lblBOut.setFont(EGYPTIAN_FONT_S);
        lblWOut = new JLabel("White Out: 0", JLabel.CENTER);
        lblWOut.setFont(EGYPTIAN_FONT_S);
        scorePanel.add(lblBOut); scorePanel.add(lblWOut);

        JPanel settingsPanel = new JPanel(new GridLayout(2,6,5,5));
        settingsPanel.setOpaque(false);
        settingsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BOARD_BG), "Players Setup", 0, 0, EGYPTIAN_FONT_S, BOARD_BG));

        p1Human = new JRadioButton("Human", true); p1Comp = new JRadioButton("Computer", false);
        p2Human = new JRadioButton("Human", true); p2Comp = new JRadioButton("Computer", false);
        p1Human.setOpaque(false); p1Comp.setOpaque(false); p2Human.setOpaque(false); p2Comp.setOpaque(false);

        p1AlgoChoice = new JComboBox<>(ALGORITHMS);
        p2AlgoChoice = new JComboBox<>(ALGORITHMS);
        p1AlgoChoice.setEnabled(false);
        p2AlgoChoice.setEnabled(false);
        p1AlgoChoice.setFont(new Font("Arial", Font.PLAIN, 10)); 
        p2AlgoChoice.setFont(new Font("Arial", Font.PLAIN, 10));

        p1Comp.addActionListener(e -> p1AlgoChoice.setEnabled(true));
        p1Human.addActionListener(e -> p1AlgoChoice.setEnabled(false));

        p2Comp.addActionListener(e -> p2AlgoChoice.setEnabled(true));
        p2Human.addActionListener(e -> p2AlgoChoice.setEnabled(false));

        ButtonGroup g1 = new ButtonGroup(); g1.add(p1Human); g1.add(p1Comp);
        ButtonGroup g2 = new ButtonGroup(); g2.add(p2Human); g2.add(p2Comp);

        settingsPanel.add(new JLabel("P1 (Black):")); settingsPanel.add(p1Human); settingsPanel.add(p1Comp);settingsPanel.add(p1AlgoChoice);
        settingsPanel.add(new JLabel("P2 (White):")); settingsPanel.add(p2Human); settingsPanel.add(p2Comp);settingsPanel.add(p2AlgoChoice);

        topPanel.add(infoPanel, BorderLayout.WEST);
        topPanel.add(scorePanel, BorderLayout.CENTER);
        topPanel.add(settingsPanel, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottomPanel.setBackground(PANEL_BG);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(3,0,0,0, BOARD_BG.darker()));

        btnToss = new JButton("Toss Sticks");
        styleButton(btnToss, SPECIAL_REBIRTH);
        btnToss.addActionListener(e -> handleToss());
        btnComputer = new JButton("Execute AI Move");
        btnRestart = new JButton("Restart Game");
        styleButton(btnRestart, new Color(139, 0, 0)); 
        btnRestart.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to restart?", "Restart", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                performRestart();
            }
        });
        bottomPanel.add(btnRestart);
        styleButton(btnComputer, SPECIAL_WATER);
        btnComputer.addActionListener(e -> runComputerAlgorithm());
        bottomPanel.add(btnToss);
        bottomPanel.add(btnComputer);
        return bottomPanel;
    }

   //تنسيق الأزرار
    private void styleButton(JButton btn, Color baseColor) {
        btn.setFont(EGYPTIAN_FONT_M);
        btn.setPreferredSize(new Dimension(180, 50));
        btn.setBackground(baseColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createRaisedBevelBorder());
    }


    private void refreshUI() {
        lblBOut.setText("Black Out: " + state.blackPiecesOut);
        lblWOut.setText("White Out: " + state.whitePiecesOut);


        for (int i = 0; i < 30; i++) {
            int val = state.board[i];
            resetSquareColor(i);
            grid[i].setFont(EGYPTIAN_FONT_L);

            if (val == 1) {
                grid[i].setText("●");
                grid[i].setForeground(PIECE_BLACK_COLOR);
                if (i == selectedIdx) grid[i].setBackground(HIGHLIGHT_SELECTED);

            } else if (val == 2) {
                grid[i].setText("●");
                grid[i].setForeground(PIECE_WHITE_COLOR);
                if (i == selectedIdx) grid[i].setBackground(HIGHLIGHT_SELECTED);

            } else  {
                grid[i].setFont(EGYPTIAN_FONT_M);
                if (i == 25) { grid[i].setText("☀"); grid[i].setForeground(Color.WHITE); }
                else if (i == 26) { grid[i].setText("≈"); grid[i].setForeground(Color.WHITE); }
                else {
                    grid[i].setText(String.valueOf(i + 1));
                    grid[i].setForeground(Color.DARK_GRAY);
                }
                resetSquareColor(i);
            }
            if (i == targetIdx) {
                grid[i].setBackground(HIGHLIGHT_TARGET);
                grid[i].setForeground(Color.WHITE);
                grid[i].setFont(EGYPTIAN_FONT_M);
                grid[i].setText("OK");
            }
        }
        if (targetIdx == -2) targetIdx = selectedIdx;
    }

    private void resetSquareColor(int i) {
        if (i == 25) grid[i].setBackground(SPECIAL_HAPPINESS);
        else if (i == 26) grid[i].setBackground(SPECIAL_WATER);
        else if (i == 14) grid[i].setBackground(SPECIAL_REBIRTH);
        else if (i >= 27) grid[i].setBackground(SPECIAL_EXIT);
        else grid[i].setBackground(SQUARE_BG);
    }

    private void performRestart() {
    state.reset(); 
    distance = 0;
    canMove = false;
    selectedIdx = -1;
    targetIdx = -1;
    lblDice.setText("Dice: -");
    updateTurnStatus();
    refreshUI();
}
    // =============================================================================================

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
            int choice = JOptionPane.showConfirmDialog(this, 
            "The winner is: " + winnerName + "\nDo you want to play again?", 
            "Game Over", 
            JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
            performRestart();
            } else {
                System.exit(0); 
            }
        }
    }


    //----------------------------------
    private void executeConfirmedMove() {
        if (engine.movePiece(state, selectedIdx, distance)) {
            refreshUI();
            checkForVictory();
            if (!state.isGameOver()) {
                finishTurn();
            }
            
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
            btnComputer.setBackground(SPECIAL_EXIT);
        } else {
            btnToss.setEnabled(true);
            btnComputer.setEnabled(false);
            btnComputer.setBackground(SPECIAL_WATER);
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
        refreshUI();
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
                int bestMoveFrom;
                String selectedAlgo = (state.currentPlayer == 1) 
                              ? (String) p1AlgoChoice.getSelectedItem() 
                              : (String) p2AlgoChoice.getSelectedItem();

                switch (selectedAlgo) {
                    case "Random":
                        bestMoveFrom = aiLogic.RandomMove(state, possibleMoves, distance);
                        break;
                    case "decideBestMove":
                        bestMoveFrom = aiLogic.decideBestMove(state, possibleMoves, distance);
                        break;
                    case "Expectiminimax 2":
                        bestMoveFrom = aiLogic.runExpectiminimax(state, possibleMoves, distance);
                        break;
                    case "Expectiminimax 3":
                        bestMoveFrom = aiLogic.runExpectiminimax3(state, possibleMoves, distance);
                        break;
                    case "Expectiminimax 4":
                        bestMoveFrom = aiLogic.runExpectiminimaxAll(state, possibleMoves, distance, 4);
                        break;
                    default:
                        bestMoveFrom = possibleMoves.get(0); 
                }
                engine.movePiece(state, bestMoveFrom, distance);
            }

            refreshUI();
            checkForVictory();
            finishTurn();
           
    }
}
