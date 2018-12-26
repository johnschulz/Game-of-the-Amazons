
package amazons;

import org.junit.Test;
import ucb.junit.textui;

import java.util.Iterator;

import static amazons.Piece.*;
import static org.junit.Assert.*;

/**
 * The suite of all JUnit tests for the amazons package.
 *
 * @author John Schulz
 */
public class UnitTest {

    /**
     * Run the JUnit tests in this package. Add xxxTest.class entries to
     * the arguments of runClasses to run other JUnit tests.
     */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /**
     * Tests basic correctness of put and get on the initialized board.
     */
    @Test
    public void testBasicPutGet() {
        Board b = new Board();
        b.put(BLACK, Square.sq(3, 5));
        assertEquals(b.get(3, 5), BLACK);
        b.put(WHITE, Square.sq(9, 9));
        assertEquals(b.get(9, 9), WHITE);
        b.put(EMPTY, Square.sq(3, 5));
        assertEquals(b.get(3, 5), EMPTY);
    }

    /**
     * Tests proper identification of legal/illegal queen moves.
     */
    @Test
    public void testIsQueenMove() {
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(1, 5)));
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(2, 7)));
        assertFalse(Square.sq(0, 0).isQueenMove(Square.sq(5, 1)));
        assertTrue(Square.sq(1, 1).isQueenMove(Square.sq(9, 9)));
        assertTrue(Square.sq(2, 7).isQueenMove(Square.sq(8, 7)));
        assertTrue(Square.sq(3, 0).isQueenMove(Square.sq(3, 4)));
        assertTrue(Square.sq(7, 9).isQueenMove(Square.sq(0, 2)));
    }

    /**
     * Tests toString for initial board state and a smiling board state. :)
     */
    @Test
    public void testToString() {
        Board b = new Board();
        assertEquals(INIT_BOARD_STATE, b.toString());
        makeSmile(b);
        assertEquals(SMILE, b.toString());
    }

    private void makeSmile(Board b) {
        b.put(EMPTY, Square.sq(0, 3));
        b.put(EMPTY, Square.sq(0, 6));
        b.put(EMPTY, Square.sq(9, 3));
        b.put(EMPTY, Square.sq(9, 6));
        b.put(EMPTY, Square.sq(3, 0));
        b.put(EMPTY, Square.sq(3, 9));
        b.put(EMPTY, Square.sq(6, 0));
        b.put(EMPTY, Square.sq(6, 9));
        for (int col = 1; col < 4; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(2, 7));
        for (int col = 6; col < 9; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(7, 7));
        for (int lip = 3; lip < 7; lip += 1) {
            b.put(WHITE, Square.sq(lip, 2));
        }
        b.put(WHITE, Square.sq(2, 3));
        b.put(WHITE, Square.sq(7, 3));
    }

    @Test
    public void testSmile() {
        Board b = new Board();
        makeSmile(b);
        assertEquals(b.toString(), SMILE);
    }

    static final String INIT_BOARD_STATE =
            "   - - - B - - B - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   B - - - - - - - - B\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   W - - - - - - - - W\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - W - - W - - -\n";

    static final String SMILE =
            "   - - - - - - - - - -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - S - S - - S - S -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - W - - - - W - -\n"
                    + "   - - - W W W W - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n";


    @Test
    public void testOneMove() {
        Board b = new Board();
        assertTrue(b.isLegal(Square.sq(3), Square.sq(63), Square.sq(66)));
        assertFalse(b.isLegal(Square.sq(13), Square.sq(63), Square.sq(66)));
        assertFalse(b.isLegal(Square.sq(93), Square.sq(82), Square.sq(37)));
        assertFalse(b.isLegal(Square.sq(3), Square.sq(6), Square.sq(66)));
        b.makeMove(Square.sq(3), Square.sq(63), Square.sq(66));
        b.makeMove(Square.sq(93), Square.sq(82), Square.sq(37));
        assertFalse(b.isLegal(Square.sq(3), Square.sq(63), Square.sq(66)));
        assertFalse(b.isLegal(Square.sq(6), Square.sq(16), Square.sq(66)));
        assertTrue(b.isLegal(Square.sq(6), Square.sq(15), Square.sq(65)));
        assertFalse(b.isLegal(Square.sq(6), Square.sq(17), Square.sq(67)));
        assertTrue(b.isLegal(Square.sq(6), Square.sq(17), Square.sq(35)));
        assertTrue(b.isLegal(Square.sq(6), Square.sq(9), Square.sq(19)));
        assertEquals(b.toString(), ONE_MOVE);
    }

    @Test
    public void testOneMove1() {
        Board b = new Board();
        Move m = Move.mv("d1-d7(g7)");
        b.makeMove(m);
        b.changeTurn();
        Move m1 = Move.mv("d10-c9(h4)");
        b.makeMove(m1);
        assertEquals(b.toString(), ONE_MOVE);
    }

    static final String ONE_MOVE =
            "   - - - - - - B - - -\n"
                    + "   - - B - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   B - - W - - S - - B\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   W - - - - - - S - W\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - W - - -\n";

    @Test
    public void testIterator1() {
        Board b = new Board();
        b.put(SPEAR, Square.sq(53));
        Iterator<Square> a = b.reachableFrom(Square.sq(3), null);
        assertEquals(Square.sq(13), a.next());
        assertEquals(Square.sq(23), a.next());
        assertEquals(Square.sq(33), a.next());
        assertEquals(Square.sq(43), a.next());
        assertEquals(Square.sq(14), a.next());
        assertEquals(Square.sq(25), a.next());
        assertEquals(Square.sq(36), a.next());
        a.next();
        a.next();
        assertEquals(Square.sq(4), a.next());
        assertEquals(Square.sq(5), a.next());
        assertEquals(Square.sq(2), a.next());
        assertEquals(Square.sq(1), a.next());
        assertEquals(Square.sq(0), a.next());
        assertEquals(Square.sq(12), a.next());
        assertEquals(Square.sq(21), a.next());
        Iterator d = b.reachableFrom(Square.sq(93), null);
        assertEquals(Square.sq(94), d.next());
        assertEquals(Square.sq(95), d.next());
        assertEquals(Square.sq(84), d.next());
        assertEquals(Square.sq(75), d.next());
        d.next();
        d.next();
        d.next();
        assertEquals(Square.sq(83), d.next());
        d.next();
        d.next();
        assertEquals(Square.sq(82), d.next());
        d.next();
        assertEquals(Square.sq(92), d.next());
        assertEquals(Square.sq(91), d.next());
        assertEquals(Square.sq(90), d.next());
    }

    @Test
    public void testCopy() {
        Board b = new Board();
        b.put(SPEAR, Square.sq(53));
        Board c = new Board();
        c.copy(b);
        assertEquals(c.toString(), b.toString());
    }

    @Test
    public void testLegalMoves() {
        Board b = new Board();
        assertTrue(b.isLegal(Square.sq(3), Square.sq(14), Square.sq(3)));
    }

}
