
package quoridor;


import java.util.List;
import java.util.LinkedList;
import java.util.Queue;
import quoridor.Position.Wall;
import quoridor.*;
/**
 * Created by Alice on 01.04.2017.
 */

public class Board {

    //there are 20 walls, 40 used because each wall is of size 2, in fact 40 here means 20 :)
    private final int    walls   = 40;
    private final int    size    = 9;
    private int          current = 0;
    private Box[][]      boxes;
    private Player[]     players;
    private List<String> history;

    /**
     * Construct the graph of boxes.
     */

    public Board(Player[] players) {
        this.history = new LinkedList<String>();
        this.players = players;
        init_box_graph();
        Direction[] defaults = { Direction.UP, Direction.DOWN, Direction.LEFT,
                Direction.RIGHT };
        Position[] defaultpos = { new Position(8, 4), new Position(0, 4),
                new Position(4, 8), new Position(4, 0) };
        if (players.length != 2 && players.length != 4) { throw new IllegalArgumentException(
                "Only 2 or 4 players are supported."); }
        for (int i = 0; i < players.length; i++) {
            players[i].initialise(i + 1, defaults[i], walls / players.length);
            Position m = defaultpos[i];
            boxes[m.getRow()][m.getCol()].setPlayer(players[i]);
        }
    }


    private Board(Player[] players, List<String> history) {
        this.history = new LinkedList<String>();
        this.players = players;
        init_box_graph();
        Position[] defaultpos = { new Position(8, 4), new Position(0, 4),
                new Position(4, 8), new Position(4, 0) };
        for (int i = 0; i < players.length; i++) {
            Position m = defaultpos[i];
            boxes[m.getRow()][m.getCol()].setPlayer(players[i]);
        }
        for (String move : history) {
            Position p = new Position(move.length() == 3 ? move
                    : move.split(" ")[1]);
            this.move(p);
        }
    }

    public Player getWinner() {
        for (Player current : players) {
            Position position = positionOf(current);
            if (current.getEnd() == Direction.UP) {
                if (position.getRow() == 0) { return current; }
            } else if (current.getEnd() == Direction.DOWN) {
                if (position.getRow() == 8) { return current; }
            } else if (current.getEnd() == Direction.LEFT) {
                if (position.getCol() == 0) { return current; }
            } else if (current.getEnd() == Direction.RIGHT) {
                if (position.getCol() == 8) { return current; }
            }
        }
        return null;
    }

    /**
     * Print the current state of the board.
     */
    public void printBoard() {
        for (Player current : players) {
            System.out.println("Player " + current.getID().toString() + " ("
                    + current.getName() + ") has "
                    + ((current.getNumWalls()+1)/2) + " walls remaining.");
        }
        System.out.println(" [4m a b c d e f g h i [24m");
        for (int i = 0; i < boxes.length; i++) {
            System.out.print(i + 1);
            System.out.print("|");
            for (Box cell : boxes[i]) {
                String cellname;
                if (cell.getPlayer() == null) {
                    cellname = " ";
                } else {
                    cellname = cell.getPlayer().getID().toString();
                    if (cell.getPlayer().getID() == currentPlayer()) {
                        cellname = "[1m" + cellname + "[0m";
                    }
                }
                if (cell.getNeighbour(Direction.DOWN) != null && i != 8) {

                    cellname = "[24m" + cellname;
                } else {

                    cellname = "[4m" + cellname;
                    if (cell.getPlayer() != null
                            && cell.getPlayer().getID() == currentPlayer()) {
                        cellname += "[4m";
                    }
                }
                if (cell.getNeighbour(Direction.RIGHT) == null) {
                    cellname += "|";
                } else if (i != 8) {
                    cellname += ".";
                } else {
                    cellname += " ";
                }
                System.out.print(cellname);
            }
            System.out.println("[24m");
        }
        System.out.println("It is " + players[current].getName() + "'s turn.");

    }


    private void init_box_graph() {
        // Make the boxes
        boxes = new Box[size][size];
        for (int i = 0; i < boxes.length; i++) {
            for (int j = 0; j < boxes.length; j++) {
                boxes[i][j] = new Box(i, j);
            }
        }

        for (int i = 1; i < boxes.length; i++) {
            for (int j = 0; j < boxes.length; j++) {
                boxes[i][j].setNeighbour(Direction.UP, boxes[i - 1][j]);
            }
        }
        for (int i = 0; i < boxes.length - 1; i++) {
            for (int j = 0; j < boxes.length; j++) {
                boxes[i][j].setNeighbour(Direction.DOWN, boxes[i + 1][j]);
            }
        }
        for (int i = 0; i < boxes.length; i++) {
            for (int j = 1; j < boxes.length; j++) {
                boxes[i][j].setNeighbour(Direction.LEFT, boxes[i][j - 1]);
            }
        }
        for (int i = 0; i < boxes.length; i++) {
            for (int j = 0; j < boxes.length - 1; j++) {
                boxes[i][j].setNeighbour(Direction.RIGHT, boxes[i][j + 1]);
            }
        }
    }


    public List<Integer> getPlayers() {
        List<Integer> ps = new LinkedList<Integer>();
        for (Player p : players) {
            ps.add(p.getID());
        }
        return ps;
    }




    public int currentPlayer() {
        return players[current].getID();
    }

    public String lastMove() {
        return history.get(history.size() - 1);
    }


    public Position positionOf(Player p) {
        for (int i = 0; i < boxes.length; i++) {
            for (int j = 0; j < boxes.length; j++) {
                if (p.equals(boxes[i][j].getPlayer())) { return new Position(i,
                        j); }
            }
        }
        return null;
    }

    public int remainingWalls(int player) {
        return players[player - 1].getNumWalls();
    }

    public Position positionOf(int id) {
        //System.out.println(id);
        return positionOf(players[id - 1]);
    }


    public Integer playerAt(Position p) {
        Player pl = boxes[p.getRow()][p.getCol()].contents;
        if (pl != null) {
            return pl.getID();
        } else {
            return null;
        }
    }


    public boolean wallExists(Position p, Direction d) {
        return boxes[p.getRow()][p.getCol()].getNeighbour(d) == null;
    }


    public List<Position> neighboursOf(Position p) {
        List<Position> neighbours = new LinkedList<Position>();
        for (Direction d : Direction.values()) {
            if (boxes[p.getRow()][p.getCol()].getNeighbour(d) != null) {
                neighbours.add(boxes[p.getRow()][p.getCol()].getNeighbour(d)
                        .getPosition());
            }
        }
        return neighbours;
    }

    public boolean flood() {
        LinkedList<Box> visited = new LinkedList<Box>();
        Queue<Box> q = new LinkedList<Box>();
        q.add(boxes[0][0]);
        visited.add(boxes[0][0]);
        Box current;
        Box temp;
        while (!q.isEmpty()) {
            current = q.remove();
            for (Direction dir : Direction.values()) {
                temp = current.getNeighbour(dir);
                if (temp != null && !visited.contains(temp)) {
                    visited.add(temp);
                    q.add(temp);
                }
            }
        }
        return visited.size() == size * size;
    }


    public List<Position> validMoves(Position pm) {
//        LinkedList<Position> adjacent = new LinkedList<Position>();
//        LinkedList<Position> moves = new LinkedList<Position>();
//        LinkedList<Direction> dirs = new LinkedList<Direction>();
//        Box current = boxes[pm.getRow()][pm.getCol()];
//        Box neighbour;
//        Direction d;
//        for (Direction dir : Direction.values()) {
//            neighbour = current.getNeighbour(dir);
//            if (neighbour != null) {
//                adjacent.add(neighbour.getPosition());
//                dirs.add(dir);
//            }
//        }
//
//        for (Position p : adjacent) {
//            d = dirs.removeFirst();
//            if (playerAt(p) != null) {
//                for (Position pos: jump(p, d, false)) {
//                    moves.add(pos);
//                }
//            } else {
//                moves.add(p);
//            }
//        }
        LinkedList<Position> moves = new LinkedList<Position>();
        //LinkedList<game.Position> dirs = new LinkedList<game.Direction>();
        Box current = boxes[pm.getRow()][pm.getCol()];
        Box neighbour;
        for (Direction dir : Direction.values())
        {

            if (current.getNeighbour(dir)!=null) {
                neighbour = current.getNeighbour(dir);
                if (neighbour != null) {
                    if (playerAt(neighbour.getPosition()) != null) {
                        for (Position pos : jump(neighbour.getPosition(), dir, false)) {
                            moves.add(pos);
                        }
                    } else {
                        moves.add(neighbour.getPosition());
                    }
                }
            }
        }
//        System.out.println("in board valid moves ");
//        for (Position m: moves) {
//            System.out.print(m.toString()+" ");
//        }
        return moves;
    }


    private Position[] jump(Position p, Direction d, boolean giveUp) {
        if (!wallExists(p, d) && playerAt(p.adjacentSquare(d)) == null) {
            Position[] rval = new Position[1];
            rval[0] = p.adjacentSquare(d);
            return rval;
        } else if (giveUp) {
            Position[] rval = new Position[0];
            return rval;
        } else {
            LinkedList<Position> moves = new LinkedList<Position>();
            for (Direction dir : Direction.values()) {
                if (!dir.equals(d) && !dir.equals(d.reverse())) {
                    for (Position pos : jump(p, dir, true)) {
                        moves.add(pos);
                    }
                }
            }
            Position rval[] = new Position[moves.size()];
            return moves.toArray(rval);
        }
    }


    private boolean isValidMove(Position m) {
        boolean validity = true;
        if (m.isWall() && players[current].getNumWalls() >= 0) {
            Box northwest = boxes[m.getRow()][m.getCol()];
            Box northeast = boxes[m.getRow()][m.getCol() + 1];
            Box southwest = boxes[m.getRow() + 1][m.getCol()];
            if (m.getOrientation() == Wall.Vertical) {
                validity = validity
                        && northwest.getNeighbour(Direction.RIGHT) != null;
                validity = validity
                        && southwest.getNeighbour(Direction.RIGHT) != null;
                validity = validity
                        && !(northwest.getNeighbour(Direction.DOWN) == null && northeast
                        .getNeighbour(Direction.DOWN) == null);

            } else if (m.getOrientation() == Wall.Horizontal) {
                validity = validity
                        && northwest.getNeighbour(Direction.DOWN) != null;
                validity = validity
                        && northeast.getNeighbour(Direction.DOWN) != null;
                validity = validity
                        && !(northwest.getNeighbour(Direction.RIGHT) == null && southwest
                        .getNeighbour(Direction.RIGHT) == null);
            } else {
                validity = false;
            }
            if (validity) {
                // Test if we can flood the board
                Board test = clone();
                test.place_wall(m);
                if (!test.flood()) {
                    //System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    validity = false;
                }
            }
        } else {
            List<Position> adjacent = validMoves(positionOf(players[current]));
            //validity = validity && adjacent.contains(m);
            for (Position p: adjacent)
            {
                if (m.toString().equals(p.toString()));
                validity = validity && true;
                //System.out.print(p.to_string()+" ");
            }
        }
        return validity;
    }

    public boolean isValidMove(String m) {
        try {
            return isValidMove(new Position(m));
        } catch (IllegalArgumentException e) {
            return m.equals("undo") && history.size() > 0;
        }
    }

    public Player[] get_players_object()
    {
        return players;
    }


    public Board makeMove(String move) {
        if (move.equals("undo") && history.size() > 0) {
            String undo = history.remove(history.size() - 1);
            Board retval = clone();
            if (undo.length() == 3) {
                retval.remove_wall(new Position(undo));
            } else {
                retval.placeMove(new Position(undo.split(" ")[0]));
            }
            current = (current - 1 + players.length) % players.length;
            return retval;
        } else {
            Position m;
            try {
                m = new Position(move);
            } catch (IllegalArgumentException e) {
                return null;
            }
            if (isValidMove(m)) {
                Board retval = clone();
                retval.move(m);
                return retval;
            }
        }
        return null;
    }


    private void move(Position m) {
        if (m.isWall()) {
            history.add(m.toString());
            place_wall(m);
        } else {
            history.add(positionOf(players[current]).toString() + " "
                    + m.toString());
            placeMove(m);
        }
        current = (current + 1) % players.length;
    }


    private void placeMove(Position m) {
        Player p = players[current];
        Position from = positionOf(p);
        boxes[from.getRow()][from.getCol()].setPlayer(null);
        boxes[m.getRow()][m.getCol()].setPlayer(p);
    }


    private void place_wall(Position position) {
        // TODO: Make neater
        // The position specified is the northwest corner of the wall.
        Box northwest = boxes[position.getRow()][position.getCol()];
        Box northeast = boxes[position.getRow()][position.getCol() + 1];
        Box southwest = boxes[position.getRow() + 1][position.getCol()];
        Box southeast = boxes[position.getRow() + 1][position.getCol() + 1];

        if (position.getOrientation() == Wall.Vertical) {
            northwest.setNeighbour(Direction.RIGHT, null);
            northeast.setNeighbour(Direction.LEFT, null);
            southwest.setNeighbour(Direction.RIGHT, null);
            southeast.setNeighbour(Direction.LEFT, null);
        } else {
            northwest.setNeighbour(Direction.DOWN, null);
            northeast.setNeighbour(Direction.DOWN, null);
            southwest.setNeighbour(Direction.UP, null);
            southeast.setNeighbour(Direction.UP, null);
        }
    }


    private void remove_wall(Position position) {
        int row = position.getRow();
        int col = position.getCol();
        if (position.getOrientation() == Wall.Horizontal) {
            boxes[row][col].setNeighbour(Direction.DOWN, boxes[row + 1][col]);
            boxes[row + 1][col].setNeighbour(Direction.UP, boxes[row][col]);
            boxes[row][col + 1].setNeighbour(Direction.DOWN,
                    boxes[row + 1][col + 1]);
            boxes[row + 1][col + 1].setNeighbour(Direction.UP,
                    boxes[row][col + 1]);
        } else {
            boxes[row][col].setNeighbour(Direction.RIGHT, boxes[row][col+1]);
            boxes[row ][col+1].setNeighbour(Direction.LEFT, boxes[row][col]);
            boxes[row+1][col].setNeighbour(Direction.RIGHT,
                    boxes[row + 1][col + 1]);
            boxes[row + 1][col + 1].setNeighbour(Direction.LEFT,
                    boxes[row+1][col]);
        }
    }

    public Board clone() {
        return new Board(players, history);
    }
}
