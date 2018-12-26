package amazons;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static amazons.Utils.*;

/** Represents a position on an Amazons board.  Positions are numbered
 *  from 0 (lower-left corner) to 99 (upper-right corner).  Squares
 *  are immutable and unique: there is precisely one square created for
 *  each distinct position.  Clients create squares using the factory method
 *  sq, not the constructor.  Because there is a unique Square object for each
 *  position, you can freely use the cheap == operator (rather than the
 *  .equals method) to compare Squares, and the program does not waste time
 *  creating the same square over and over again.
 *  @author John Schulz
 */
final class Square {

    /** The regular expression for a square designation (e.g.,
     *  a3). For convenience, it is in parentheses to make it a
     *  group.  This subpattern is intended to be incorporated into
     *  other pattern that contain square designations (such as
     *  patterns for moves). */
    static final String SQ = "([a-j](?:[1-9]|10))";

    /** Return my row position, where 0 is the bottom row. */
    int row() {
        return _row;
    }

    /** Return my column position, where 0 is the leftmost column. */
    int col() {
        return _col;
    }

    /** Return my index position (0-99).  0 represents square a1, and 99
     *  is square j10. */
    int index() {
        return _index;
    }

    /** Return true iff THIS - TO is a valid queen move. */
    boolean isQueenMove(Square to) {
        double check = Math.abs(((double) this.row() - to.row())
                / (this.col() - to.col()));
        return (this != to) && (this.row() == to.row()
                || this.col() == to.col() || check == 1.0);
    }

    /** Definitions of direction for queenMove.  DIR[k] = (dcol, drow)
     *  means that to going one step from (col, row) in direction k,
     *  brings us to (col + dcol, row + drow). */
    private static final int[][] DIR = {
        { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 },
        { 0, -1 }, { -1, -1 }, { -1, 0 }, { -1, 1 }
    };

    /** Return the Square that is STEPS>0 squares away from me in direction
     *  DIR, or null if there is no such square.
     *  DIR = 0 for north, 1 for northeast, 2 for east, etc., up to 7 for
     *  northwest. If DIR has another value, return null. Thus, unless the
     *  result is null the resulting square is a queen move away from me. */
    Square queenMove(int dir, int steps) {
        int row = this.row();
        int col = this.col();
        if (dir > 7 || dir < 0) {
            return null;
        } else {
            try {
                int dy = DIR[dir][0];
                int dx = DIR[dir][1];
                for (int i = 0; i < steps; i += 1) {
                    row += dx;
                    col += dy;
                }
                Square a = sq(col, row);
            } catch (IllegalArgumentException x) {
                return null;
            }
            return sq(col, row);
        }

    }

    /**
     * Check if it is a legal move.
     * @param dir the direction of the move.
     * @param s the square were moving from.
     * @return true or false.
     */
    boolean legalQueenMove(int dir, Square s) {
        int row = this.row();
        int col = this.col();
        int sRow = s.row();
        int sCol = s.col();
        int dx = DIR[dir][0];
        int dy = DIR[dir][1];
        boolean good = true;
        while (row != sRow || col != sCol) {
            row += dy;
            col += dx;
        }
        return good;
    }

    /** Return the direction (an int as defined in the documentation
     *  for queenMove) of the queen move THIS-TO. */
    int direction(Square to) {
        assert isQueenMove(to);
        double check = ((double) this.row() - to.row())
                / (this.col() - to.col());
        int height = to.row() - this.row();
        if (height >= 1) {
            if (check == 1.0) {
                return 1;
            } else if (this.col() == to.col()) {
                return 0;
            } else {
                return 7;
            }
        } else if (height < 0) {
            if (check == 1.0) {
                return 5;
            } else if (this.col() == to.col()) {
                return 4;
            } else {
                return 3;
            }
        } else {
            if (to.col() > this.col()) {
                return 2;
            } else {
                return 6;
            }
        }
    }

    @Override
    public String toString() {
        return _str;
    }

    /** Return true iff COL ROW is a legal square. */
    static boolean exists(int col, int row) {
        return row >= 0 && col >= 0 && row < Board.SIZE && col < Board.SIZE;
    }

    /** Return the (unique) Square denoting COL ROW. */
    static Square sq(int col, int row) {
        if (!exists(row, col)) {
            throw error("row or column out of bounds");
        }
        return SQUARES[row * 10 + col];
    }

    /** Return the (unique) Square denoting the position with index INDEX. */
    static Square sq(int index) {
        return SQUARES[index];
    }

    /** Return the (unique) Square denoting the position COL ROW, where
     *  COL ROW is the standard text format for a square (e.g., a4). */
    static Square sq(String col, String row) {
        final int a1 = 97;
        String c = String.valueOf((char) (Integer.parseInt(col) + a1));
        String str = String.format("%s%s", c, Integer.parseInt(row) + 1);
        return sq(str);
    }

    /** Return the (unique) Square denoting the position in POSN, in the
     *  standard text format for a square (e.g. a4). POSN must be a
     *  valid square designation. */
    static Square sq(String posn) {
        assert posn.matches(SQ);
        int row = Integer.parseInt(String.valueOf(posn.substring(1))) - 1;
        int col = Character.getNumericValue(posn.charAt(0)) - 10;
        return sq(col, row);
    }

    /** Return an iterator over all Squares. */
    static Iterator<Square> iterator() {
        return SQUARE_LIST.iterator();
    }

    /** Return the Square with index INDEX. */
    private Square(int index) {
        _index = index;
        final int a1 = 10;
        final int a2 = 97;
        _row = _index / a1;
        _col = _index % a1;
        String c = String.valueOf((char) (_col + a2));
        _str = String.format("%s%s", c, _row + 1);
    }

    /** The cache of all created squares, by index. */
    private static final Square[] SQUARES =
        new Square[Board.SIZE * Board.SIZE];

    /** SQUARES viewed as a List. */
    private static final List<Square> SQUARE_LIST = Arrays.asList(SQUARES);

    static {
        for (int i = Board.SIZE * Board.SIZE - 1; i >= 0; i -= 1) {
            SQUARES[i] = new Square(i);
        }
    }

    /**
     * Accessor method.
     * @return the square array.
     */
    private static Square[] getSquares() {
        return SQUARES;
    }

    /** My index position. */
    private final int _index;

    /** My row and column (redundant, since these are determined by _index). */
    private final int _row, _col;

    /** My String denotation. */
    private final String _str;

}
