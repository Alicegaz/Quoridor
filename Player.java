
package quoridor;

import java.util.List;

public abstract class Player {


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Player other = (Player) obj;
        if (id != other.id) return false;
        return true;
    }

    private quoridor.Direction direction;
    protected String  name;
    private int       walls;
    private int       id;
    private boolean   initialised = false;

    public Player(String name) {
        this.name = name;
    }

    public Player() {
    }

    public final void initialise(int id, quoridor.Direction d, int walls) {
        if (!initialised) {
            this.id = id;
            this.direction = d;
            this.walls = walls;
            this.initialised = true;
            this.onCreation();
        }
    }


    public final quoridor.Direction getEnd() {
        return this.direction;
    }


    public final Integer getID() {
        return id;
    }


    public final Integer getNumWalls() {
        return walls;
    }


    public final void useWall() {
        walls--;
    }

    public final String getMove(quoridor.Game g) {
        String move = this.nextMove(g.getBoard());
        //System.out.println("fff"+move.toString());
        while (!g.isValidMove(move.split(" ")[1]) || (move.split(" ")[1].length() == 3 && getNumWalls() <= 0)) {
            onFailure(g.getBoard());
            move = this.nextMove(g.getBoard());
        }

        if (move.split(" ")[1].length() == 3) {
            useWall();
        }
        if (move.split(" ")[1] == "undo" && g.getBoard().lastMove().length() == 3) {
            this.walls++;
        }

        return move;
    }


    protected void onCreation() {
    }


    protected void onFailure(quoridor.Board b) {
    }

    public String getName() {
        return this.name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public final String getMove(quoridor.Game g, int st)
    {
        String move = this.nextMove(g.getBoard(), st);
        //System.out.println("fff"+move.toString());
        while (!g.isValidMove(move) || (move.length() == 3 && getNumWalls() <= 0)) {
            onFailure(g.getBoard());
            move = this.nextMove(g.getBoard(), st);
        }

        if (move.length() == 3) {
            useWall();
        }
        if (move.equals("undo") && g.getBoard().lastMove().length() == 3) {
            this.walls++;
        }
        return move;
    }

    public abstract String nextMove(quoridor.Board b, int st);

    public abstract String nextMove(quoridor.Board b);

    public abstract List<Position> generateMoves(quoridor.Board s);
    public abstract quoridor.Difficulty getDiff();
}
