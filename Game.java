
package quoridor;


import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import quoridor.*;

public class Game {

    LinkedList<String> future = new LinkedList<String>();
    quoridor.Board board;
    Player[]           players;


    public Game(Player[] players) {
        this.players = players;
        this.board = new quoridor.Board(players);
    }

    public boolean isValidMove(String move) {
        return (move.equals("redo") && future.size() > 0)
                || board.isValidMove(move);
    }

    private void redo() {
        String command = future.removeLast();
        if (command.length() == 3) {
            board = board.makeMove(command);
        } else {
            board = board.makeMove(command.split(" ")[1]);
        }
    }

    public quoridor.Board getBoard() {
        return board.clone();
    }

    public boolean finished() {
        boolean finished = false;
        for (Player current : players) {
            Position position = board.positionOf(current);
            if (current.getEnd() == quoridor.Direction.UP) {
                finished = finished || position.getRow() == 0;
            } else if (current.getEnd() == quoridor.Direction.DOWN) {
                finished = finished || position.getRow() == 8;
            } else if (current.getEnd() == quoridor.Direction.LEFT) {
                finished = finished || position.getCol() == 0;
            } else if (current.getEnd() == quoridor.Direction.RIGHT) {
                finished = finished || position.getCol() == 8;
            }
        }
        return finished;
    }

    public Player getWinner() {
        for (Player current : players) {
            Position position = board.positionOf(current);
            if (current.getEnd() == quoridor.Direction.UP) {
                if (position.getRow() == 0) { return current; }
            } else if (current.getEnd() == quoridor.Direction.DOWN) {
                if (position.getRow() == 8) { return current; }
            } else if (current.getEnd() == quoridor.Direction.LEFT) {
                if (position.getCol() == 0) { return current; }
            } else if (current.getEnd() == quoridor.Direction.RIGHT) {
                if (position.getCol() == 8) { return current; }
            }
        }
        return null;
    }

    /**
     * Play a game until finished.
     */
    public void play() {
        int steps = 0;
        while (!finished()) {
            players[board.currentPlayer() - 1].getName();
            long startTime = System.nanoTime();
            String m1;
            if (players[board.currentPlayer() - 1].getDiff() == quoridor.Difficulty.Easy)
            {
                m1 = players[board.currentPlayer() - 1].getMove(this, steps + 1);
            }
            else {
                m1 = players[board.currentPlayer() - 1].getMove(this);
            }

            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            String next;
            if (players[board.currentPlayer() - 1].getDiff() == quoridor.Difficulty.Easy)
            {
                next = m1;
            }
            else
                next = m1.split(" ")[1];
            //System.out.println("current "+players[board.currentPlayer() - 1].getName());
            String sc = m1.split(" ")[0];
            if (isValidMove(next)) {
                if (next.equals("redo")) {
                    System.out.println("####################################");
                    redo();
                } else if (next.equals("undo")) {
                    System.out.println("#####################################");
                    future.add(board.lastMove());
                    //Singleton_class.map_taken.put_item(next, Double.parseDouble(sc));
                    board = board.makeMove(next);
                } else {
                    while (!future.isEmpty()) { future.remove(); }
                    //Singleton_class.map_taken.put_item(next, Double.parseDouble(sc));
                    //System.out.println("hext"+next.toString());
                    board = board.makeMove(next);
                    //System.out.println("other "+players[board.currentPlayer()-1].getName());
                    board.printBoard();
                }
            }
            steps+=1;
        }
    }

}
