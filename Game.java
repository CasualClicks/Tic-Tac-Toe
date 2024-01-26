import java.util.*;
import java.lang.*;

class Game {
    private static final int MACHINE = 0;
    private static final int USER = 1;
    private static final int SIDE = 3;
    private static final char MachineMove = 'O';
    private static final char UserMove = 'X';

    public static void showBoard(char board[][]) {
        // using printf function to increase readability
        System.out.printf("\t\t\t %c | %c | %c \n", board[0][0], board[0][1], board[0][2]);
        System.out.printf("\t\t\t ------------\n");
        System.out.printf("\t\t\t %c | %c | %c \n", board[1][0], board[1][1], board[1][2]);
        System.out.printf("\t\t\t ------------\n");
        System.out.printf("\t\t\t %c | %c | %c \n", board[2][0], board[2][1], board[2][2]);
    }

    public static void Instructions() {
        System.out.println("\nChoose a cell number from 1 to 9 as below and play\n");
        System.out.println("\t\t\t 1 | 2 | 3 ");
        System.out.println("\t\t\t ---------");
        System.out.println("\t\t\t 4 | 5 | 6 ");
        System.out.println("\t\t\t ---------");
        System.out.println("\t\t\t 7 | 8 | 9 \n");
    }

    public static void Initialize(char[] board[]) {
        for (int i = 0; i < SIDE; i++)
            for (int j = 0; j < SIDE; j++)
                board[i][j] = '*';
    }

    public static void Winner(int turn) {
        if (turn == MACHINE)
            System.out.println("MACHINE has won\n");
        else
            System.out.println("USER has won\n");
    }

    public static boolean gameOver(char board[][]) {
        return (rowCrossed(board) || columnCrossed(board) || diagonalCrossed(board));
    }

    public static boolean rowCrossed(char board[][]) {
        for (int i = 0; i < SIDE; i++)
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != '*')
                return true;
        return false;
    }

    public static boolean columnCrossed(char board[][]) {
        for (int i = 0; i < SIDE; i++)
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != '*')
                return true;
        return false;
    }

    public static boolean diagonalCrossed(char board[][]) {
        // diagonal from top left to bottom right
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != '*')
            return true;
        // diagonal from top right to bottom left
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != '*')
            return true;

        return false;
    }

    // MAIN ALGORITHM
    // logic - we basically assign scores to conditions. If 'X' wins ->+10, if 'O'
    // wins->-10, else draw -> 0
    // it is a backtracking algorithm. We basically would be tracking back from the
    // very final solution and would choose that path back to root node, where score
    // is maximum.
    // here, isAI boolean value states whether it is computer's chance or not
    public static int minimax(char[][] board, int depth, boolean isAI) {
        int score = 0;
        int maxScore = 0;

        if (gameOver(board) == true) {
            if (isAI == true)
                return -10;
            if (isAI == false)
                return +10;
        } else {
            if (depth < 9) {
                if (isAI == true) {
                    maxScore = Integer.MIN_VALUE;
                    for (int i = 0; i < SIDE; i++) {
                        for (int j = 0; j < SIDE; j++) {
                            if (board[i][j] == '*') {
                                board[i][j] = MachineMove;
                                score = minimax(board, depth + 1, false);
                                board[i][j] = '*';
                                if (score > maxScore)
                                    maxScore = score;
                            }
                        }
                    }
                    return maxScore; // Fix here
                } else {
                    maxScore = Integer.MAX_VALUE;
                    for (int i = 0; i < SIDE; i++) {
                        for (int j = 0; j < SIDE; j++) {
                            if (board[i][j] == '*') {
                                board[i][j] = UserMove;
                                score = minimax(board, depth + 1, true);
                                board[i][j] = '*';
                                if (score < maxScore)
                                    maxScore = score;
                            }
                        }
                    }
                    return maxScore;
                }
            } else {
                return 0;
            }
        }
        return 0;
    }

    // this funciton would be responsible for calculating machine's best move, after
    // checking minimax algorithm's score on each empty cell where machine can make
    // it's move
    public static int bestMove(char board[][], int moveIndex) {
        int x = -1;
        int y = -1;
        int score = 0;
        int bestScore = Integer.MIN_VALUE;
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                // logic - if machine sees an empty space, it would make it's move there. now,
                // based on minimax algorithm, it would find a score associated with that move.
                // Machine would do so for each empty position and store that posiiton where it
                // gets maximum score.
                if (board[i][j] == '*') {
                    board[i][j] = MachineMove;
                    score = minimax(board, moveIndex + 1, false);
                    board[i][j] = '*'; // changing that posiiton back to original 'empty' space
                    if (score > bestScore) {
                        bestScore = score;
                        x = i;
                        y = j;
                    }
                }
            }
        }
        return x * 3 + y;
    }

    public static void TicTacToe(int turn) {
        // Initializing a Scanner class object for taking inputs
        Scanner in = new Scanner(System.in);

        // initializing a 9X9 board
        char board[][] = new char[SIDE][SIDE];
        int moveIndex = 0, x = 0, y = 0;

        Initialize(board);
        Instructions();

        // until game is over or moveIndex goes over the bounds
        while (gameOver(board) == false && moveIndex != SIDE * SIDE) {
            int n; // this temp variable would be used to store the posiiton where the move is
                   // played.
            if (turn == MACHINE) {
                // this function would be responsible to calculate the best move that machine
                // could make, on the basis of minimax algorithm
                n = bestMove(board, moveIndex);
                x = n / SIDE;
                y = n % SIDE;
                board[x][y] = MachineMove;
                System.out.println("\nMachine marked " + MachineMove + " in cell " + (n + 1));
                showBoard(board);
                moveIndex++;
                turn = USER;
            } else if (turn == USER) {
                // printing the open positions, where the user can play it's move
                System.out.print("\nYou can insert in the following positions: ");
                for (int i = 0; i < SIDE; i++)
                    for (int j = 0; j < SIDE; j++)
                        if (board[i][j] == '*')
                            System.out.print(((i * 3 + j) + 1) + " "); // converting index to posiiton
                System.out.print("\nEnter the position: ");

                try {
                    n = in.nextInt();
                    n--; // because we are using 0-based indexing and user input is in 1-based indexing
                    x = n / SIDE; // calculating row on the basis of input
                    y = n % SIDE; // calculating column on the basis of input

                    // if entry is valid
                    if (n < 9 && n >= 0 && board[x][y] == '*') {
                        board[x][y] = UserMove;
                        System.out.println("\nUser marked " + UserMove + " in cell " + (n + 1));
                        showBoard(board);
                        moveIndex++;
                        turn = MACHINE;
                    }
                    // else if user gives input location which is already occupied
                    else if (n < 9 && n >= 0 && board[x][y] != '*') {
                        System.out.println("\nAlready occupied posiiton. Please select a valid position");
                    }
                    // else if user inputs cell value out of range
                    else if (n < 0 || n >= 9) {
                        System.out.println("\nCell Range Invalid / Cell range out of bounds.");
                    }
                } catch (InputMismatchException ex) {
                    System.out.println("\nPlease enter valid Input.");
                    in.nextLine();
                }
            }
        }

        // if the game is not over, but i have moved out of cell range (all cells
        // filled)
        if (gameOver(board) == false && moveIndex == SIDE * SIDE)
            System.out.println("\nIt's a draw !!");
        // if gammeOver(board)==true
        else {
            turn = (turn == USER) ? MACHINE : USER; // this is done because if a party wins, we won't know that until
                                                    // next iteration where the turn is now given to opposite party
            Winner(turn);
        }
    }

    public static void main(String[] args) {

        // Calling Scanner class to take inputs
        Scanner in = new Scanner(System.in);

        // Using print streams for UI
        System.out.println("\n----------------------------------------------------------------\n");
        System.out.println("\t\t\t Tic-Tac-Toe\n");
        System.out.println("----------------------------------------------------------------\n");

        char want_to_continue = 'y';
        while (want_to_continue == 'y') {
            char choice;
            System.out.print("Would you like to start first(y/n): ");
            choice = Character.toLowerCase(in.nextLine().charAt(0));

            switch (choice) {
                case 'n':
                    // System.out.println("\nPlaying tic tac toe machine first :)");
                    TicTacToe(MACHINE);
                    break;
                case 'y':
                    // System.out.println("\nPlaying tic tac toe :)");
                    TicTacToe(USER);
                    break;
                default:
                    System.out.println("\nInvalid Choice\n");
            }

            System.out.print("\nDo you want to try again(y/n): ");
            want_to_continue = Character.toLowerCase(in.nextLine().charAt(0));
            System.out.println();

        }
        in.close();
    }
}