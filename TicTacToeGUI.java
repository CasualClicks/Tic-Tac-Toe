import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TicTacToeGUI extends JFrame {
    private static final int MACHINE = 0;
    private static final int USER = 1;
    private static final int SIDE = 3;
    private static final char MachineMove = 'O';
    private static final char UserMove = 'X';

    private JButton[][] buttons;
    private char[][] board;
    private int moveIndex;
    private int currentPlayer;

    public TicTacToeGUI() {
        super("Tic Tac Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setLayout(new GridLayout(SIDE, SIDE));

        initializeBoard();
        initializeButtons();
        askPlayerToPlayFirst();
        startGame();
    }

    private void initializeBoard() {
        board = new char[SIDE][SIDE];
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                board[i][j] = '*';
            }
        }
    }

    private void initializeButtons() {
        buttons = new JButton[SIDE][SIDE];
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(new Font("Arial", Font.PLAIN, 40));
                buttons[i][j].addActionListener(new ButtonClickListener(i, j));
                add(buttons[i][j]);
            }
        }
    }

    private void askPlayerToPlayFirst() {
        int choice = JOptionPane.showOptionDialog(
                this,
                "Do you want to play first?",
                "Player's Choice",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[] { "Yes", "No" },
                "Yes");

        currentPlayer = (choice == JOptionPane.YES_OPTION) ? USER : MACHINE;
    }

    private void startGame() {
        moveIndex = 0;
        if (currentPlayer == MACHINE) {
            machineMove();
        }
    }

    private class ButtonClickListener implements ActionListener {
        private int row, col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttons[row][col].getText().equals("")) {
                if (currentPlayer == USER) {
                    buttons[row][col].setText(String.valueOf(UserMove));
                    board[row][col] = UserMove;
                }

                if (checkForWinner()) {
                    JOptionPane.showMessageDialog(null, (currentPlayer == USER ? "User" : "Machine") + " wins!");
                    resetGame();
                } else if (isBoardFull()) {
                    JOptionPane.showMessageDialog(null, "It's a draw!");
                    resetGame();
                } else {
                    currentPlayer = (currentPlayer == USER) ? MACHINE : USER;
                    if (currentPlayer == MACHINE) {
                        machineMove();
                    }
                }
            }
        }
    }

    private boolean checkForWinner() {
        return (rowCrossed() || columnCrossed() || diagonalCrossed());
    }

    private boolean rowCrossed() {
        for (int i = 0; i < SIDE; i++)
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != '*')
                return true;
        return false;
    }

    private boolean columnCrossed() {
        for (int i = 0; i < SIDE; i++)
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != '*')
                return true;
        return false;
    }

    private boolean diagonalCrossed() {
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != '*')
            return true;
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != '*')
            return true;
        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (board[i][j] == '*') {
                    return false;
                }
            }
        }
        return true;
    }

    private void machineMove() {
        int[] bestMove = minimax(board, MACHINE);
        int row = bestMove[0];
        int col = bestMove[1];

        buttons[row][col].setText(String.valueOf(MachineMove));
        board[row][col] = MachineMove;

        if (checkForWinner()) {
            JOptionPane.showMessageDialog(null, "Machine wins!");
            resetGame();
        } else if (isBoardFull()) {
            JOptionPane.showMessageDialog(null, "It's a draw!");
            resetGame();
        } else {
            currentPlayer = USER;
        }
    }

    private int[] minimax(char[][] board, int player) {
        int[] bestMove = new int[] { -1, -1 };
        int bestScore = (player == MACHINE) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (board[i][j] == '*') {
                    board[i][j] = (player == MACHINE) ? MachineMove : UserMove;

                    int score = minimaxHelper(board, 0, false);

                    board[i][j] = '*';

                    if ((player == MACHINE && score > bestScore) || (player == USER && score < bestScore)) {
                        bestScore = score;
                        bestMove[0] = i;
                        bestMove[1] = j;
                    }
                }
            }
        }

        return bestMove;
    }

    private int minimaxHelper(char[][] board, int depth, boolean isMaximizing) {
        if (checkForWinner()) {
            return (isMaximizing) ? -10 : 10;
        }

        if (isBoardFull()) {
            return 0;
        }

        int score;

        if (isMaximizing) {
            int maxScore = Integer.MIN_VALUE;
            for (int i = 0; i < SIDE; i++) {
                for (int j = 0; j < SIDE; j++) {
                    if (board[i][j] == '*') {
                        board[i][j] = MachineMove;
                        maxScore = Math.max(maxScore, minimaxHelper(board, depth + 1, false));
                        board[i][j] = '*';
                    }
                }
            }
            score = maxScore;
        } else {
            int minScore = Integer.MAX_VALUE;
            for (int i = 0; i < SIDE; i++) {
                for (int j = 0; j < SIDE; j++) {
                    if (board[i][j] == '*') {
                        board[i][j] = UserMove;
                        minScore = Math.min(minScore, minimaxHelper(board, depth + 1, true));
                        board[i][j] = '*';
                    }
                }
            }
            score = minScore;
        }

        return score;
    }

    private void resetGame() {
        initializeBoard();
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                buttons[i][j].setText("");
            }
        }

        askPlayerToPlayFirst();
        startGame();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TicTacToeGUI().setVisible(true));
    }
}
