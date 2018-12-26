package amazons;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the boards set up.
 * @author John Schulz
 */
public class TestBoard {
    /**
     * Board for testing.
     */
    private Board m = new Board();

    /**
     * Test the get method.
     */
    @Test
    public void testGetPiece() {
        Piece n = Piece.WHITE;
        Piece h = m.get(3, 0);
        assertEquals(n, h);
    }

    /**
     * Test the put method.
     */
    @Test
    public void testPut() {
        Piece n = Piece.WHITE;
        m.put(n, Square.sq(4));
    }
    /**
     * Test the isLegal method.
     */
    @Test
    public void testIsLegal() {
        m = new Board();
        assertTrue(m.isLegal(Square.sq(3)));
        assertFalse(m.isLegal(Square.sq(93)));
    }
    /**
     * Test the isLegalMove method.
     */
    @Test
    public void testIsLegalMove() {
        m = new Board();
        assertTrue(m.isLegal(Square.sq(3), Square.sq(23)));
        assertFalse(m.isLegal(Square.sq(3), Square.sq(24)));
        m.put(Piece.WHITE, Square.sq(13));
        assertFalse(m.isLegal(Square.sq(3), Square.sq(23)));
        assertFalse(m.isLegal(Square.sq(3), Square.sq(9)));
        assertTrue(m.isLegal(Square.sq(3), Square.sq(5)));
        assertTrue(m.isLegal(Square.sq(3), Square.sq(25)));
        assertTrue(m.isLegal(Square.sq(3), Square.sq(21)));
        m.put(Piece.WHITE, Square.sq(12));
        assertFalse(m.isLegal(Square.sq(3), Square.sq(21)));
    }
    /**
     * Test the toString method.
     */
    @Test
    public void testToString() {
        Board n = new Board();
        String check =
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
        assertEquals(n.toString(), check);
    }
}
