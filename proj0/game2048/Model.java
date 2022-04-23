package game2048;

import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author TODO: YOUR NAME HERE
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */

    /** returns true if there exists an empty tile in the extreme "side" else false*/
    public boolean move_extreme(int col, int row, Side side){
        if (side == Side.NORTH){
            if (board.tile(col, board.size() - 1) == null){
                return true;
            }
            return false;
        }
        return false;
    }

    /** returns the relevant index of the tile closest to the side being moved to */
    public int closest_adjacent(int c, int r, Side side){
        if (side == Side.NORTH){
            for (int row = r + 1; row < board.size(); row++){
                if (board.tile(c, row) != null){
                    return row;
                }
            }
        }

        return 1;
    }

    public boolean tilt(Side side) {
        boolean changed;
        changed = false;

        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.

        board.setViewingPerspective(side);
        side = Side.NORTH;
        /** Implement move north */
        for (int col = 0; col < board.size(); col ++){
            boolean extreme_north = true;
            int merge_count = 0;
            boolean merged = false;
            for (int row = board.size() - 2; row >= 0; row--){
                Tile t = board.tile(col, row);
                boolean flag = false;
                if (t != null) {
                    // check if extreme move to the north is possible
                    if (extreme_north & move_extreme(col, row, side)) {
                        board.move(col, board.size() - 1, t);
                        changed = true;
                        extreme_north = false;
                        continue;
                    }

                    int closest_tile_row = closest_adjacent(col, row, side);
                    if (t.value() == board.tile(col, closest_tile_row).value()) {
                        if (!merged) {
                            board.move(col, closest_tile_row, t);
                            score += t.value() * 2;
                            changed = true;
                            merged = true;
                            flag = true;
                        } else {
                            if (merge_count % 2 == 0) {
                                board.move(col, closest_tile_row, t);
                                score += t.value() * 2;
                                changed = true;
                                flag = true;
                            } else {
                                board.move(col, closest_tile_row - 1, t);
                                changed = true;
                                flag = true;
                            }
                        }
                    }
                    if (!flag) {
                        board.move(col, closest_tile_row - 1, t);
                        changed = true;
                    }
                    if (merged) {
                        merge_count++;
                    }
                }
            }
//            }
        }

        board.setViewingPerspective(Side.NORTH);

        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function.
        for (int i = 0; i < b.size(); i++){
            for (int j = 0; j < b.size(); j++){
                if (b.tile(i, j) == null){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        // TODO: Fill in this function
        for (int i = 0; i < b.size(); i++){
            for (int j = 0; j < b.size(); j++){
                if (b.tile(i, j) != null){
                    if (b.tile(i, j).value() == MAX_PIECE){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */

    /** helper functions to check the 4 adjacent tiles */
    public static boolean adjacent_north(Board b, int c, int r){
        int row = r + 1;
        if (row == b.size()){return false;}
        if (b.tile(c, r).value() == b.tile(c, row).value()){
            return true;
        }
        return false;
    }

    public static boolean adjacent_south(Board b, int c, int r){
        int row = r - 1;
        if (row < 0){return false;}
        if (b.tile(c, r).value() == b.tile(c, row).value()){
            return true;
        }

        return false;
    }

    public static boolean adjacent_west(Board b, int c, int r){
        int col = c - 1;
        if (col < 0){return false;}
        if (b.tile(c, r).value() == b.tile(col, r).value()){
            return true;
        }

        return false;
    }

    public static boolean adjacent_east(Board b, int c, int r){
        int col = c + 1;
        if (col == b.size()){return false;}
        if (b.tile(c, r).value() == b.tile(col, r).value()){
            return true;
        }

        return false;
    }
    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.
        if (Model.emptySpaceExists(b)){return true;}

        for (int col = 0; col < b.size(); col++){
            for (int row = 0; row < b.size(); row++){
                if (adjacent_north(b, col, row) || adjacent_south(b, col, row) || adjacent_east(b, col, row) || adjacent_west(b, col, row)){
                    return true;
                }
            }
        }

        return false;
    }


    @Override
    /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}