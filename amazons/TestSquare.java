package amazons;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Checks the Square class.
 * @author John Schulz
 */
public class TestSquare {
    Square a = Square.sq(82);
    Square a1 = Square.sq(88);
    Square b = Square.sq(11);
    Square b1 = Square.sq(61);
    Square b2 = Square.sq(16);
    Square b3 = Square.sq(52);
    Square c = Square.sq(99);
    Square d = Square.sq(6);
    Square d1 = Square.sq(16);

    /**
     * Test the to string method.
     */
    @Test
    public void checkStr() {
        assertEquals("c9", a.toString());
        assertEquals("b2", b.toString());
        assertEquals("j10", c.toString());
    }
    /**
     * Test the integrity of squares.
     */
    @Test
    public void testSquares() {
        assertEquals(Square.sq(2, 8).toString(), a.toString());
        assertEquals(Square.sq("2", "8").toString(), a.toString());
        assertEquals(Square.sq("c9").toString(), a.toString());
        assertEquals(Square.sq("b2").toString(), b.toString());
    }
    /**
     * Test the to isQueenMove method.
     */
    @Test
    public void checkIsQueenMove() {
        assertTrue(a.isQueenMove(a1));
        assertTrue(a1.isQueenMove(b));
        assertFalse(a.isQueenMove(b));
    }
    /**
     * Test the to queenMove method.
     */
    @Test
    public void checkQueenMove() {
        assertEquals(b.queenMove(0, 5).toString(), b1.toString());
        assertEquals(b.queenMove(1, 7).toString(), a1.toString());
        assertEquals(a1.queenMove(5, 7).toString(), b.toString());
        assertEquals(b1.queenMove(4, 5).toString(), b.toString());
        assertEquals(b.queenMove(2, 5).toString(), b2.toString());
        assertEquals(b2.queenMove(6, 5).toString(), b.toString());
        assertEquals(b2.queenMove(7, 4).toString(), b3.toString());
        assertEquals(b3.queenMove(3, 4).toString(), b2.toString());
    }

    /**
     * Test the to direction method.
     */
    @Test
    public void checkDirection() {
        assertEquals(b.direction(b1), 0);
        assertEquals(b.direction(a1), 1);
        assertEquals(a1.direction(b), 5);
        assertEquals(b1.direction(b), 4);
        assertEquals(b.direction(b2), 2);
        assertEquals(b2.direction(b), 6);
        assertEquals(b2.direction(b3), 7);
        assertEquals(b3.direction(b2), 3);
        assertEquals(d.direction(d1), 0);
    }
}
