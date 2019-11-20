package com.packetpublishing.tddjava.ch05connect4;

import java.io.PrintStream;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by lj1218.
 * Date: 2019/11/19
 */
public class Connect4TDD {

    private static final int ROWS = 6;

    private static final int COLUMNS = 7;

    private static final int DISCS_TO_WIN = 4;

    private static final String EMPTY = " ";

    private static final String RED = "R";

    private static final String GREEN = "G";

    private static final String DELIMITER = "|";

    private String[][] board = new String[ROWS][COLUMNS];

    private String currentPlayer = RED;

    private PrintStream outputChannel;

    private Map<String, Pattern> winPatterns;

    private String winner = "";

    public Connect4TDD(PrintStream output) {
        outputChannel = output;
        for (String[] row : board) {
            Arrays.fill(row, EMPTY);
        }
        winPatterns = new HashMap<>();
        winPatterns.put(RED, Pattern.compile(".*" + RED + "{" + DISCS_TO_WIN + "}.*"));
        winPatterns.put(GREEN, Pattern.compile(".*" + GREEN + "{" + DISCS_TO_WIN + "}.*"));
    }

    public String getCurrentPlayer() {
        outputChannel.printf("Player %s turn%n", currentPlayer);
        return currentPlayer;
    }

    private void printBoard() {
        for (int row = ROWS - 1; row >= 0; row--) {
            StringJoiner sj = new StringJoiner(DELIMITER, DELIMITER, DELIMITER);
            Stream.of(board[row]).forEachOrdered(sj::add);
            outputChannel.println(sj.toString());
        }
    }

    private void switchPlayer() {
        if (RED.equals(currentPlayer)) {
            currentPlayer = GREEN;
        } else {
            currentPlayer = RED;
        }
    }

    public int getNumberOfDiscs() {
        return IntStream.range(0, COLUMNS)
                .map(this::getNumberOfDiscInColumn)
                .sum();
    }

    private int getNumberOfDiscInColumn(int column) {
        return (int) IntStream.range(0, ROWS)
                .filter(row -> !EMPTY.equals(board[row][column]))
                .count();
    }

    public int putDiscInColumn(int column) {
        checkColumn(column);
        int row = getNumberOfDiscInColumn(column);
        checkPositionToInsert(row, column);
        board[row][column] = currentPlayer;
        printBoard();
        checkWinner(row, column);
        switchPlayer();
        return row;
    }

    private void checkColumn(int column) {
        if (column < 0 || column >= COLUMNS) {
            throw new RuntimeException("Invalid column " + column);
        }
    }

    private void checkPositionToInsert(int row, int column) {
        if (row == ROWS) {
            throw new RuntimeException("No more room in column " + column);
        }
    }

    public boolean isFinished() {
        return !winner.isEmpty() || getNumberOfDiscs() == ROWS * COLUMNS;
    }

    public String getWinner() {
        return winner;
    }

    private void checkWinner(int row, int column) {
        if (winner.isEmpty()) {
            if (!checkWinnerVertical(row, column) && !checkWinnerHorizontal(row, column)) {
                checkWinnerDiagonal(row, column);
            }

            if (!winner.isEmpty()) {
                outputChannel.println(currentPlayer + " wins");
            }
        }
    }

    private boolean checkWinnerVertical(int row, int column) {
        String colour = board[row][column];
        String vertical = IntStream.range(0, ROWS).mapToObj(r -> board[r][column])
                .reduce(String::concat).orElse("");
        if (winPatterns.get(colour).matcher(vertical).matches()) {
            winner = colour;
            return true;
        }
        return false;
    }

    private boolean checkWinnerHorizontal(int row, int column) {
        String colour = board[row][column];
        String horizontal = Arrays.stream(board[row]).reduce(String::concat).orElse("");
        if (winPatterns.get(colour).matcher(horizontal).matches()) {
            winner = colour;
            return true;
        }
        return false;
    }

    private void checkWinnerDiagonal(int row, int column) {
        String colour = board[row][column];
        int startOffset = Math.min(row, column);
        int myRow = row - startOffset;
        int myCol = column - startOffset;
        StringJoiner sj = new StringJoiner("");
        do {
            sj.add(board[myRow++][myCol++]);
        } while (myRow < ROWS && myCol < COLUMNS);
        if (winPatterns.get(colour).matcher(sj.toString()).matches()) {
            winner = colour;
            return;
        }

        startOffset = Math.min(ROWS - 1 - row, column);
        myRow = row + startOffset;
        myCol = column - startOffset;
        sj = new StringJoiner("");
        do {
            sj.add(board[myRow--][myCol++]);
        } while (myRow >= 0 && myCol < COLUMNS);
        if (winPatterns.get(colour).matcher(sj.toString()).matches()) {
            winner = colour;
        }
    }

    public static void main(String[] args) {
        Connect4TDD game = new Connect4TDD(System.out);
        Scanner scanner = new Scanner(System.in);
        while (!game.isFinished()) {
            System.out.println("Where do the next disc should be placed?");
            game.putDiscInColumn(scanner.nextInt());
        }
    }
}
