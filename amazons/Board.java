package amazons;


import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

import static amazons.Piece.*;


/**
 * The state of an Amazons Game.
 *
 * @author John Schulz
 */
class Board {

    /**
     * Definitions of direction for queenMove.  DIR[k] = (dcol, drow)
     * means that to going one step from (col, row) in direction k,
     * brings us to (col + dcol, row + drow).
     */
    private static final int[][] DIR = {
            {0, 1}, {1, 1}, {1, 0}, {1, -1},
            {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}
    };

    /**
     * The number of squares on a side of the board.
     */
    static final int SIZE = 10;

    /**
     * Initializes a game board with SIZE squares on a side in the
     * initial position.
     */
    Board() {
        init();
    }

    /**
     * Initializes a copy of MODEL.
     */
    Board(Board model) {
        copy(model);
    }

    /**
     * Copies MODEL into me.
     */
    void copy(Board model) {
        if (model == this) {
            return;
        }
        _turn = model.turn();
        _winner = model.winner();
        _numMoves = model.numMoves();
        _pieces = new Piece[SIZE * SIZE];
        path = model.path;
        for (int x = 0; x < 100; x += 1) {
            _pieces[x] = model._pieces[x];
        }
    }

    /**
     * Clears the board to the initial position.
     */
    void init() {
        path = new Stack<>();
        _numMoves = 0;
        _turn = WHITE;
        _winner = null;
        _pieces = new Piece[SIZE * SIZE];
        for (int index = 0; index < _pieces.length; index += 1) {
            final int a1 = 3;
            final int a2 = 6;
            final int a3 = 30;
            final int a4 = 39;
            final int a5 = 60;
            final int a6 = 69;
            final int a7 = 93;
            final int a8 = 96;
            if (index == a1 || index == a2 || index == a3 || index == a4) {
                _pieces[index] = Piece.WHITE;
            } else if (index == a5 || index == a6
                    || index == a7 || index == a8) {
                _pieces[index] = Piece.BLACK;
            } else {
                _pieces[index] = Piece.EMPTY;
            }
        }

    }

    /**
     * Return the Piece whose move it is (WHITE or BLACK).
     */
    Piece turn() {
        return _turn;
    }

    /**
     * Return the number of moves (that have not been undone) for this
     * board.
     */
    int numMoves() {
        return _numMoves;
    }

    /**
     * Return the winner in the current position, or null if the game is
     * not yet finished.
     */
    Piece winner() {
        Iterator legalWhite = legalMoves(WHITE);
        Iterator legalBlack = legalMoves(BLACK);
        if (!legalWhite.hasNext()) {
            _winner = BLACK;
        }
        if (!legalBlack.hasNext()) {
            _winner = WHITE;
        }
        return _winner;
    }

    /**
     * Return the contents the square at S.
     */
    final Piece get(Square s) {
        return _pieces[s.index()];
    }

    /**
     * Return the contents of the square at (COL, ROW), where
     * 0 <= COL, ROW <= 9.
     */
    final Piece get(int col, int row) {
        return _pieces[row * 10 + col];
    }

    /**
     * Return the contents of the square at COL ROW.
     */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /**
     * Set square S to P.
     */
    final void put(Piece p, Square s) {
        _pieces[s.index()] = p;
    }

    /**
     * Set square (COL, ROW) to P.
     */
    final void put(Piece p, int col, int row) {
        _pieces[row * 10 + col] = p;
        _winner = EMPTY;
    }

    /**
     * Set square COL ROW to P.
     */
    final void put(Piece p, char col, char row) {
        put(p, col - 'a', row - '1');
    }

    /**
     * Return true iff FROM - TO is an unblocked queen move on the current
     * board, ignoring the contents of ASEMPTY, if it is encountered.
     * For this to be true, FROM-TO must be a queen move and the
     * squares along it, other than FROM and ASEMPTY, must be
     * empty. ASEMPTY may be null, in which case it has no effect.
     */
    boolean isUnblockedMove(Square from, Square to, Square asEmpty) {

        return isLegal(from) && isLegal(from, to);
    }

    /**
     * Return true iff FROM is a valid starting square for a move.
     */
    boolean isLegal(Square from) {
        return _pieces[from.index()] == _turn;
    }

    /**
     * Return true iff FROM-TO is a valid first part of move, ignoring
     * spear throwing.
     */
    boolean isLegal(Square from, Square to) {
        int dir = 0;
        try {
            dir = from.direction(to);
        } catch (AssertionError x) {
            return false;
        }
        int row = from.row();
        int col = from.col();
        int sRow = to.row();
        int sCol = to.col();
        int dx = DIR[dir][0];
        int dy = DIR[dir][1];
        boolean good = true;
        while (row != sRow || col != sCol) {
            row += dy;
            col += dx;
            Piece curPiece = _pieces[row * 10 + col];
            if (curPiece != Piece.EMPTY) {
                good = false;
            }
        }
        return good;
    }

    /**
     * Return true iff FROM-TO(SPEAR) is a legal move in the current
     * position.
     */
    boolean isLegal(Square from, Square to, Square spear) {
        if (from == spear) {
            return isLegal(from) && (isLegal(from, to));
        } else if (from.direction(to)
                == oppositeDirection(to.direction(spear))) {
            return isLegal(from) && isLegal(from, to) && isLegal(from, spear);
        } else {
            return isLegal(from) && isLegal(from, to) && isLegal(to, spear);
        }
    }

    /**
     * Return true iff MOVE is a legal move in the current
     * position.
     */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to(), move.spear());
    }

    /**
     * Move FROM-TO(SPEAR), assuming this is a legal move.
     */
    void makeMove(Square from, Square to, Square spear) {
        Move ourMove = Move.mv(from, to, spear);
        path.push(ourMove);
        Piece holdPiece = _pieces[from.index()];
        _pieces[from.index()] = Piece.EMPTY;
        _pieces[to.index()] = holdPiece;
        _pieces[spear.index()] = Piece.SPEAR;
        _numMoves += 1;
        changeTurn();
    }

    /**
     * Move according to MOVE, assuming it is a legal move.
     */
    void makeMove(Move move) {
        makeMove(move.from(), move.to(), move.spear());
    }

    /**
     * Undo one move.  Has no effect on the initial board.
     */
    void undo() {
        if (!path.empty()) {
            Move un = path.pop();
            Square from = un.from();
            Square to = un.to();
            Square spear = un.spear();
            Piece color = BLACK;
            if (turn().toName().equals("Black")) {
                color = WHITE;
            } else {
                color = BLACK;
            }
            _pieces[spear.index()] = EMPTY;
            _pieces[from.index()] = color;
            _pieces[to.index()] = EMPTY;
            changeTurn();
        }

    }

    /**
     * Return an Iterator over the Squares that are reachable by an
     * unblocked queen move from FROM. Does not pay attention to  what
     * piece (if any) is on FROM, nor to whether the game is finished.
     * Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     * feature is useful when looking for Moves, because after moving a
     * piece, one wants to treat the Square it came from as empty for
     * purposes of spear throwing.)
     */
    Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /**
     * Return an Iterator over all legal moves on the current board.
     */
    Iterator<Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /**
     * Return an Iterator over all legal moves on the current board for
     * SIDE (regardless of whose turn it is).
     */
    Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    /**
     * An iterator used by reachableFrom.
     */
    private class ReachableFromIterator implements Iterator<Square> {

        /**
         * Iterator of all squares reachable by queen move from FROM,
         * treating ASEMPTY as empty.
         */
        ReachableFromIterator(Square from, Square asEmpty) {
            _from = from;
            _dir = -1;
            _steps = 0;
            _asEmpty = asEmpty;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _dir < 8;
        }

        @Override
        public Square next() {
            if (!hasNext()) {
                return null;
            } else {
                Square ret = _from.queenMove(_dir, _steps);
                toNext();
                return ret;
            }
        }

        /**
         * Advance _dir and _steps, so that the next valid Square is
         * _steps steps in direction _dir from _from.
         */
        private void toNext() {
            while (_dir != 8) {
                _steps += 1;
                Square a = _from.queenMove(_dir, _steps);
                if (a == null || (get(a) != EMPTY && a != _asEmpty)) {
                    _dir += 1;
                    _steps = 0;
                } else {
                    break;
                }
            }

        }

        /**
         * Starting square.
         */
        private Square _from;
        /**
         * Current direction.
         */
        private int _dir;
        /**
         * Current distance.
         */
        private int _steps;
        /**
         * Square treated as empty.
         */
        private Square _asEmpty;
    }

    /**
     * An iterator used by legalMoves.
     */
    private class LegalMoveIterator implements Iterator<Move> {

        /**
         * All legal moves for SIDE (WHITE or BLACK).
         */
        LegalMoveIterator(Piece side) {
            _startingSquares = Square.iterator();
            _spearThrows = NO_SQUARES;
            _pieceMoves = NO_SQUARES;
            _fromPiece = side;
            _count = -1;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _count < 100;
        }

        @Override
        public Move next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Move ret = Move.mv(_start, _nextSquare, _spearThrows.next());
            toNext();
            return ret;
        }

        /**
         * Advance so that the next valid Move is
         * _start-_nextSquare(sp), where sp is the next value of
         * _spearThrows.
         */
        private void toNext() {
            if (_spearThrows.hasNext()) {
                return;
            }
            if (!_pieceMoves.hasNext()) {
                _count += 1;
                while (_count < 100) {
                    Square a = Square.sq(_count);
                    if (get(a) == _fromPiece) {
                        _start = a;
                        _pieceMoves = reachableFrom(_start, null);
                        toNext();
                        break;
                    }
                    _count += 1;
                }
            } else {
                if (!hasNext()) {
                    return;
                }
                _nextSquare = _pieceMoves.next();
                _spearThrows = reachableFrom(_nextSquare, _start);
            }
        }

        /**
         * Keeps the count of our baord.
         */
        private int _count;
        /**
         * Color of side whose moves we are iterating.
         */
        private Piece _fromPiece;
        /**
         * Current starting square.
         */
        private Square _start;
        /**
         * Remaining starting squares to consider.
         */
        private Iterator<Square> _startingSquares;
        /**
         * Current piece's new position.
         */
        private Square _nextSquare;
        /**
         * Remaining moves from _start to consider.
         */
        private Iterator<Square> _pieceMoves;
        /**
         * Remaining spear throws from _piece to consider.
         */
        private Iterator<Square> _spearThrows;
    }

    @Override
    public String toString() {
        String ret = "";
        for (int y = SIZE - 1; y >= 0; y -= 1) {
            ret += "   ";
            for (int x = 0; x < SIZE - 1; x += 1) {
                ret += _pieces[y * 10 + x].toString() + " ";
            }
            ret += _pieces[y * 10 + SIZE - 1].toString() + "\n";
        }
        return ret;
    }

    /**
     * An empty iterator for initialization.
     */
    private static final Iterator<Square> NO_SQUARES =
            Collections.emptyIterator();

    /**
     * Piece whose turn it is (BLACK or WHITE).
     */
    private Piece _turn;
    /**
     * Cached value of winner on this board, or EMPTY if it has not been
     * computed.
     */
    private Piece _winner;

    /**
     * The amount of moves.
     */
    private int _numMoves;

    /**
     * The board set up with pieces.
     */
    private Piece[] _pieces;

    /**
     * @return the set up of pieces for the board.
     */
    public Piece[] getPieces() {
        return _pieces;
    }

    /**
     * The stack to use the method undo.
     */
    private Stack<Move> path;

    /**
     * Changes the turn for this board.
     */
    public void changeTurn() {
        if (_turn == Piece.WHITE) {
            _turn = Piece.BLACK;
        } else {
            _turn = Piece.WHITE;
        }
    }

    /**
     * @param i is the current direction.
     * @return the opposite direction.
     */
    public int oppositeDirection(int i) {
        if (i == 0) {
            return 4;
        }
        if (i == 1) {
            return 5;
        }
        if (i == 2) {
            return 6;
        }
        if (i == 3) {
            return 7;
        }
        if (i == 4) {
            return 0;
        }
        if (i == 5) {
            return 1;
        }
        if (i == 6) {
            return 2;
        } else {
            return 3;
        }
    }
}
