package amazons;

import java.util.ArrayList;
import java.util.Iterator;


import static amazons.Piece.*;

/** A Player that automatically generates moves.
 *  @author John Schulz
 */
class AI extends Player {

    /** A position magnitude indicating a win (for white if positive, black
     *  if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move move = findMove();
        _controller.reportMove(move);
        return move.toString();
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        if (_myPiece == WHITE) {
            findMove(b, maxDepth(b), true, 1, -INFTY, INFTY);
        } else {
            findMove(b, maxDepth(b), true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {
        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        } else {
            if (sense == -1) {
                Iterator<Move> a = board.legalMoves(BLACK);
                int best = beta;
                Board right = new Board(board);
                while (a.hasNext()) {
                    Move check1 = a.next();
                    right.makeMove(check1);
                    int response = findMove(right, depth - 1,
                            false, sense * -1, alpha, beta);
                    right.undo();
                    if (response <= best) {
                        best = staticScore(right);
                        if (saveMove) {
                            _lastFoundMove = check1;
                        }
                        beta = Math.min(beta, response);
                        if (beta <= alpha) {
                            break;
                        }
                    }
                }

                return best;
            } else if (sense == 1) {
                Iterator<Move> a = board.legalMoves(WHITE);
                int best = alpha;
                Board right = new Board(board);
                while (a.hasNext()) {
                    Move check1 = a.next();
                    right.makeMove(check1);
                    int response = findMove(right, depth - 1,
                            false, sense * -1, alpha, beta);
                    right.undo();
                    if (response >= best) {
                        best = staticScore(right);
                        if (saveMove) {
                            _lastFoundMove = check1;
                        }
                        alpha = Math.min(beta, response);
                        if (beta <= alpha) {
                            break;
                        }
                    }
                }

                return best;
            }
        }
        return 0;
    }

    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private int maxDepth(Board board) {
        ArrayList<Square> blacks = new ArrayList<>();
        ArrayList<Square> whites = new ArrayList<>();
        for (int x = 0; x < 100; x += 1) {
            Square y = Square.sq(x);
            if (board.get(y) == BLACK) {
                blacks.add(y);
            } else if (board.get(y) == WHITE) {
                whites.add(y);
            }
        }
        int trapped = 0;
        if (board.turn() == WHITE) {
            for (Square item : whites) {
                Iterator<Square> w = board.reachableFrom(item, null);
                if (!w.hasNext()) {
                    trapped += 1;
                }
            }
        } else {
            for (Square item : whites) {
                Iterator<Square> w = board.reachableFrom(item, null);
                if (!w.hasNext()) {
                    trapped += 1;
                }
            }
        }
        int N = board.numMoves();
        final int a1 = 50;
        final int a2 = 35;
        if (trapped == 3 || N > a1) {
            return 3;
        } else if (trapped == 2 || N > a2) {
            return 2;
        } else {
            return 1;
        }
    }


    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        Piece winner = board.winner();
        if (winner == BLACK) {
            return -WINNING_VALUE;
        } else if (winner == WHITE) {
            return WINNING_VALUE;
        } else {
            ArrayList<Square> blacks = new ArrayList<>();
            ArrayList<Square> whites = new ArrayList<>();
            for (int x = 0; x < 100; x += 1) {
                Square y = Square.sq(x);
                if (board.get(y) == BLACK) {
                    blacks.add(y);
                } else if (board.get(y) == WHITE) {
                    whites.add(y);
                }
            }
            int whiteTrapped = 0;
            int blackTrapped = 0;
            int blackMoves = 0;
            int whiteMoves = 0;
            for (Square item : blacks) {
                Iterator<Square> b = board.reachableFrom(item, null);
                if (!b.hasNext()) {
                    blackTrapped += 1;
                }
                while (b.hasNext()) {
                    b.next();
                    blackMoves += 1;
                }
            }
            for (Square item : whites) {
                Iterator<Square> w = board.reachableFrom(item, null);
                if (!w.hasNext()) {
                    whiteTrapped += 1;
                }
                while (w.hasNext()) {
                    w.next();
                    whiteMoves += 1;
                }
            }
            int totalScore = whiteMoves - blackMoves;
            if (whiteTrapped > blackTrapped) {
                totalScore += 100;
            } else {
                totalScore -= 100;
            }
            return totalScore;
        }

    }
}
