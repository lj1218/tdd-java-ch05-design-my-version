package com.packetpublishing.tddjava.ch05connect4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.*;

/**
 * Created by lj1218.
 * Date: 2019/11/19
 */
public class Conect4TDDSpec {

    private static final int ROWS = 6;

    private static final int COLUMNS = 7;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private Connect4TDD tested;

    private OutputStream output;

    @Before
    public void beforeEachTest() {
        output = new ByteArrayOutputStream();
        tested = new Connect4TDD(new PrintStream(output));
    }

    /*
     * The board is composed by 7 horizontal and 6 vertical empty positions
     */

    @Test
    public void whenTheGameIsStartedTheBoardIsEmpty() {
        assertThat(tested.getNumberOfDiscs(), is(0));
    }

    /*
     * Players introduce discs on the top of the columns.
     * Introduced disc drops down the board if the column is empty.
     * Future discs introduced in the same column will stack over previous ones
     */

    @Test
    public void whenDiscOutsideBoardThenRuntimeException() {
        int column = -1;
        exception.expect(RuntimeException.class);
        exception.expectMessage("Invalid column " + column);
        tested.putDiscInColumn(column);
    }

    @Test
    public void whenDiscOutsideBoardThenRuntimeException2() {
        int column = COLUMNS;
        exception.expect(RuntimeException.class);
        exception.expectMessage("Invalid column " + column);
        tested.putDiscInColumn(column);
    }

    @Test
    public void whenFirstDiscInsertedInColumnThenPositionIsZero() {
        int column = 1;
        assertThat(tested.putDiscInColumn(column), is(0));
    }

    @Test
    public void whenSecondDiscInsertedInColumnThenPositionIsOne() {
        int column = 1;
        tested.putDiscInColumn(column);
        assertThat(tested.putDiscInColumn(column), is(1));
    }

    @Test
    public void whenDiscsInsertedThenNumberOfDiscsIncreases() {
        int column = 1;
        tested.putDiscInColumn(column);
        assertThat(tested.getNumberOfDiscs(), is(1));
    }

    @Test
    public void whenNoMoreRoomInColumnThenRuntimeException() {
        int column = 1;
        int maxDiscsInColumn = 6; // the number of rows
        for (int times = 0; times < maxDiscsInColumn; ++times) {
            tested.putDiscInColumn(column);
        }
        exception.expect(RuntimeException.class);
        exception.expectMessage("No more room in column " + column);
        tested.putDiscInColumn(column);
    }

    /*
     * It is a two-person game so there is one colour for each player.
     * One player uses red ('R'), the other one uses green ('G').
     * Players alternate turns, inserting one disc every time
     */

    @Test
    public void whenFirstPlayerPlaysThenDiscColorIsRed() {
        assertThat(tested.getCurrentPlayer(), is("R"));
    }

    @Test
    public void whenSecondPlayerPlaysThenDiscColorIsRed() {
        int column = 1;
        tested.putDiscInColumn(column);
        assertThat(tested.getCurrentPlayer(), is("G"));
    }

    /*
     * We want feedback when either, event or error occur within the game.
     * The output shows the status of the board on every move
     */

    @Test
    public void whenAskedForCurrentPlayerTheOutputNotice() {
        tested.getCurrentPlayer();
        assertThat(output.toString(), containsString("Player R turn"));
    }

    @Test
    public void whenADiscIsIntroducedTheBoardIsPrinted() {
        int column = 1;
        tested.putDiscInColumn(column);
        assertThat(output.toString(), containsString("| |R| | | | | |"));
    }

    /*
     * When no more discs can be inserted, the game finishes and it is considered a draw
     */

    @Test
    public void whenTheGameStartsItIsNotFinished() {
        assertFalse("The game must not be finished", tested.isFinished());
    }

    @Test
    public void whenNoDiscCanBeIntroducedTheGamesIsFinished() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                tested.putDiscInColumn(col);
            }
        }
        assertTrue("The game must be finished", tested.isFinished());
    }

    /*
     * If a player inserts a disc and connects more than 3 discs of his colour
     * in a straight vertical line then that player wins
     */

    @Test
    public void when4VerticalDiscsAreConnectedThenPlayerWins() {
        for (int i = 0; i < 3; i++) {
            tested.putDiscInColumn(1); // R
            tested.putDiscInColumn(2); // G
        }

//        assertThat(tested.getWinner(), is(""));
        assertThat(tested.getWinner(), isEmptyString());
        tested.putDiscInColumn(1); // R
        assertThat(tested.getWinner(), is("R"));
    }

    /*
     * If a player inserts a disc and connects more than 3 discs of his colour
     * in a straight horizontal line then that player wins
     */

    @Test
    public void when4HorizontalDiscsAreConnectedThenPlayerWins() {
        int column;
        for (column = 0; column < 3; column++) {
            tested.putDiscInColumn(column); // R
            tested.putDiscInColumn(column); // G
        }

//        assertThat(tested.getWinner(), is(""));
        assertThat(tested.getWinner(), isEmptyString());
        tested.putDiscInColumn(column); // R
        assertThat(tested.getWinner(), is("R"));
    }

    /*
     * If a player inserts a disc and connects more than 3 discs of his colour
     * in a straight diagonal line then that player wins
     */

    @Test
    public void when4Diagonal1DiscsAreConnectedThenThatPlayerWins() {
        int[] gamePlay = new int[]{1, 2, 2, 3, 4, 3, 3, 4, 4, 5, 4};
        for (int col : gamePlay) {
            tested.putDiscInColumn(col);
        }
        assertThat(tested.getWinner(), is("R"));
    }

    @Test
    public void when4Diagonal2DiscsAreConnectedThenThatPlayerWins() {
        int[] gamePlay = new int[]{3, 4, 2, 3, 2, 2, 1, 1, 1, 1};
        for (int col : gamePlay) {
            tested.putDiscInColumn(col);
        }
        assertThat(tested.getWinner(), is("G"));
    }
}
