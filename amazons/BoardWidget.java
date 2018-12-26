package amazons;

import ucb.gui2.Pad;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import static amazons.Piece.*;
import static amazons.Square.sq;

/**
 * A widget that displays an Amazons game.
 *
 * @author John Schulz
 */
class BoardWidget extends Pad {

    /* Parameters controlling sizes, speeds, colors, and fonts. */

    /**
     * Colors of empty squares and grid lines.
     */
    static final Color
            SPEAR_COLOR = new Color(64, 64, 64),
            LIGHT_SQUARE_COLOR = new Color(238, 207, 161),
            DARK_SQUARE_COLOR = new Color(205, 133, 63),
            GREEN = new Color(146, 244, 66);

    /**
     * Locations of images of white and black queens.
     */
    private static final String
            WHITE_QUEEN_IMAGE = "wq4.png",
            BLACK_QUEEN_IMAGE = "bq4.png",
            SPEER_IMAGE = "spear.png";

    /**
     * Size parameters.
     */
    private static final int
            SQUARE_SIDE = 30,
            BOARD_SIDE = SQUARE_SIDE * 10;

    /**
     * A graphical representation of an Amazons board that sends commands
     * derived from mouse clicks to COMMANDS.
     */
    BoardWidget(ArrayBlockingQueue<String> commands) {
        _commands = commands;
        setMouseHandler("click", this::mouseClicked);
        setPreferredSize(BOARD_SIDE, BOARD_SIDE);

        try {
            _whiteQueen = ImageIO.read(Utils.getResource(WHITE_QUEEN_IMAGE));
            _blackQueen = ImageIO.read(Utils.getResource(BLACK_QUEEN_IMAGE));
            _speer = ImageIO.read(Utils.getResource(SPEER_IMAGE));
        } catch (IOException excp) {
            System.err.println("Could not read queen images.");
            System.exit(1);
        }
        _acceptingMoves = false;
    }

    /**
     * Draw the bare board G.
     */
    private void drawGrid(Graphics2D g) {
        g.setColor(DARK_SQUARE_COLOR);
        g.fillRect(0, 0, BOARD_SIDE, BOARD_SIDE);
        g.setColor(LIGHT_SQUARE_COLOR);
        final int a1 = 300;
        final int a2 = 60;
        final int a3 = 30;
        for (int i = 0; i < a1; i += a2) {
            for (int j = 0; j < a1; j += a2) {
                g.fillRect(i, j, a3, a3);
            }
        }
        for (int i = a3; i < a1; i += a2) {
            for (int j = a3; j < a1; j += a2) {
                g.fillRect(i, j, a3, a3);
            }
        }
    }

    @Override
    public synchronized void paintComponent(Graphics2D g) {
        drawGrid(g);
        g.setColor(GREEN);
        final int a1 = 30;
        final int a2 = 100;
        for (Square item : clicks) {
            g.fillRect(cx(item.col()), cy(item.row()), a1, a1);
        }
        for (int x = 0; x < a2; x += 1) {
            Square y = Square.sq(x);
            if (_board.get(y) == WHITE) {
                drawQueen(g, y, WHITE);
            } else if (_board.get(y) == BLACK) {
                drawQueen(g, y, BLACK);
            } else if (_board.get(y) == SPEAR) {
                drawSpeer(g, y);
            }
        }
    }

    /**
     * Draw a queen for side PIECE at square S on G.
     */
    private void drawQueen(Graphics2D g, Square s, Piece piece) {
        g.drawImage(piece == WHITE ? _whiteQueen : _blackQueen,
                cx(s.col()) + 2, cy(s.row()) + 4, null);
    }

    /**
     * Draw a speer for side PIECE at square S on G.
     */
    private void drawSpeer(Graphics2D g, Square s) {
        g.drawImage(_speer,
                cx(s.col()) + 2, cy(s.row()) + 4, null);
    }

    /**
     * Handle a click on S.
     */
    private void click(Square s) {
        clicks.add(s);
        if (clicks.size() == 3) {
            Move m = Move.mv(clicks.get(0), clicks.get(1), clicks.get(2));
            String comman = m.toString();
            clicks.clear();
            _commands.add(comman);
        }
        repaint();

    }

    /**
     * Handle mouse click event E.
     */
    private synchronized void mouseClicked(String unused, MouseEvent e) {
        int xpos = e.getX(), ypos = e.getY();
        int x = xpos / SQUARE_SIDE,
                y = (BOARD_SIDE - ypos) / SQUARE_SIDE;
        if (_acceptingMoves
                && x >= 0 && x < Board.SIZE && y >= 0 && y < Board.SIZE) {
            click(sq(x, y));
        }
    }

    /**
     * Revise the displayed board according to BOARD.
     */
    synchronized void update(Board board) {
        _board.copy(board);
        repaint();
    }

    /**
     * Turn on move collection iff COLLECTING, and clear any current
     * partial selection.   When move collection is off, ignore clicks on
     * the board.
     */
    void setMoveCollection(boolean collecting) {
        _acceptingMoves = collecting;
        repaint();
    }

    /**
     * Return x-pixel coordinate of the left corners of column X
     * relative to the upper-left corner of the board.
     */
    private int cx(int x) {
        return x * SQUARE_SIDE;
    }

    /**
     * Return y-pixel coordinate of the upper corners of row Y
     * relative to the upper-left corner of the board.
     */
    private int cy(int y) {
        return (Board.SIZE - y - 1) * SQUARE_SIDE;
    }

    /**
     * Return x-pixel coordinate of the left corner of S
     * relative to the upper-left corner of the board.
     */
    private int cx(Square s) {
        return cx(s.col());
    }

    /**
     * Return y-pixel coordinate of the upper corner of S
     * relative to the upper-left corner of the board.
     */
    private int cy(Square s) {
        return cy(s.row());
    }

    /**
     * Queue on which to post move commands (from mouse clicks).
     */
    private ArrayBlockingQueue<String> _commands;
    /**
     * Board being displayed.
     */
    private final Board _board = new Board();

    /**
     * Image of white queen.
     */
    private BufferedImage _whiteQueen;
    /**
     * Image of black queen.
     */
    private BufferedImage _blackQueen;
    /**
     * Our speer image.
     */
    private BufferedImage _speer;

    /**
     * True iff accepting moves from user.
     */
    private boolean _acceptingMoves;
    /**
     * Arraylist to keep track of clicks.
     */
    private ArrayList<Square> clicks = new ArrayList<>();
}
