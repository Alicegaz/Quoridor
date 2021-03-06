package quoridor;

/**
 * Created by Alice on 29.03.2017.
 */

public class Box {
    int row;
    int col;

    Player contents;

    Box[] adjacent;

    public Box(int row, int col) {
        this.row = row;
        this.col = col;
        this.adjacent = new Box[4];
    }

    Position getPosition() {
        return new Position(this.row, this.col);
    }

    public void setNeighbour(Direction direction, Box b) {
        adjacent[direction.index()] = b;
    }


    public Box getNeighbour(Direction direction) {
        return adjacent[direction.index()];
    }

    public void setPlayer(Player p) {
        this.contents = p;
    }

    public Player getPlayer() {
        return this.contents;
    }
}
