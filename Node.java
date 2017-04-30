package quoridor;

import javafx.geometry.Pos;

import java.util.LinkedList;
import java.util.List;
import java.util.Collections;

/**
 * Created by Alice on 06.04.2017.
 */


public class Node {

    Node parentNode;
    quoridor.Board board;
    char simulation_type;
    int step;
    int player;
    Position move;
    List<Node> childNodes;
    List<Position> untriedMoves;
    double wins;
    double visits;
    double value;
    double payoffs;
    double urgency;
    double fairness;
    int depth;
    double uct_value;

    Node(){}

    public Node(Position m, Node parent, quoridor.Board b, char simulation, int stepNumber)
    {
        this.board = b;
        this.player = b.currentPlayer();
        this.move = m;
        this.parentNode = parent;
        this.childNodes = new LinkedList<>();
        this.simulation_type = simulation;
        /* TODO add simulation type */
        this.untriedMoves = generateMoves();
        for (Position m2 : untriedMoves)
        {
            //System.out.print(m2.toString());
        }
        this.wins = 0;
        this.visits = 0;
        this.value = 0;
        this.payoffs = 0;
        this.urgency = 0;
        this.fairness = 0;
        this.depth = 0;
        this.uct_value = 0;
        this.step = stepNumber;
    }


    public List<Position> generateMoves() {
        int player = this.board.currentPlayer();
        List<Position> moves = this.board.validMoves(this.board.positionOf(player));
        //       System.out.println("valid moves ");
//        for (Position m : moves)
//        {
//            System.out.print(m.toString()+" ");
//        }
        System.out.println();
        if (this.board.remainingWalls(player) > 0) {
            for (char i = '1'; i < '9'; i++) {
                for (char j = 'a'; j < 'i'; j++) {
                    Position horstate = new Position(String.valueOf(j)
                            + String.valueOf(i) + "h");
                    Position verstate = new Position(String.valueOf(j)
                            + String.valueOf(i) + "v");
                    if (this.board.isValidMove(horstate.toString())) {
                        moves.add(horstate);
                    }
                    if (this.board.isValidMove(verstate.toString())) {
                        moves.add(verstate);
                    }
                }
            }
        }
//        if (moves.size() > 2) {
//            moves.remove(rand.nextInt(moves.size()));
//        }

        quoridor.AI.ShuffleList(moves);
        return moves;
    }

    Node addChild(Position m, quoridor.Board s, int step)
    {
        Node n = new Node(m, this, s, this.simulation_type, step);
        n.depth = this.depth +1;
        this.untriedMoves.remove(m);
        this.childNodes.add(n);
        return n;
    }


    public int compareTo(Node node1, Node node2)
    {
        int constant = 2;
        if ((node1.wins / node1.visits +Math.sqrt(constant * Math.log(this.visits)/node1.visits)) <
                (node2.wins / node2.visits + Math.sqrt(constant * Math.log(this.visits)/node2.visits)))
            return 1;
        else if ((node1.wins / node1.visits +Math.sqrt(constant * Math.log(this.visits)/node1.visits)) >
                (node2.wins / node2.visits + Math.sqrt(constant * Math.log(this.visits)/node2.visits)))
            return -1;
        else return 0;
    }

    public int compareToVal(Node node1, Node node2)
    {
        if (node1.value > node1.value)
        {
            return 1;
        }
        else if (node1.value < node2.value)
            return -1;
        else return 0;
    }

    public int compareToVisits(Node node1, Node node2)
    {
        if (node1.visits < node2.visits)
            return 1;
        else if (node1.visits > node2.visits)
            return -1;
        else return 0;
    }

    public int compareToMax(Node node1, Node node2)
    {
        if ((node1.visits + node1.value) < (node2.visits + node2.value))
            return 1;
        else if ((node1.visits + node1.value) < (node2.visits + node2.value))
            return -1;
        else
            return 0;

    }

    /* TODO add the modified comparable method for secureChild method */
    public int compareToSec(Node node1, Node node2)
    {
        if (node1.visits < node2.visits)
            return 1;
        else
        if (node1.visits > node2.visits)
            return -1;
        else return 0;
    }

    Node UCT()
    {
        /**** TODO update select constant ******/
        int constant = 2;
        this.childNodes.forEach((node) -> {node.uct_value = node.wins / node.visits + Math.sqrt(constant * Math.log(this.visits)/node.visits);});
//        this.childNodes.sort(
//                (node1, node2) ->
//                        (node1.wins / node1.visits +Math.sqrt(constant * Math.log(this.visits)/node1.visits)) <
//                (node2.wins / node2.visits + Math.sqrt(constant * Math.log(this.visits)/node2.visits))
//        )
        Collections.sort(this.childNodes, (node1, node2) -> compareTo(node1,node2));
        return this.childNodes.get(childNodes.size()-1);
    }

    public void updateUCT(int res)
    {
        if (res > 0)
        {
            this.wins+= res;
        }
        this.visits += 1;
        this.payoffs += res;
        if (this.childNodes.size() != 0)
        {
            double total = 0;
            for (Node child: childNodes)
            {
                total += child.value;
            }
            this.value = total / this.childNodes.size();
        }
        else
            this.value = (2 * this.wins - this.visits) / this.visits;
    }

    public Position maxChild()
    {
        Collections.sort(this.childNodes, (node1, node2) -> compareToVal(node1,node2));
        return this.childNodes.get(0).move;
    }

    public Position robustChild()
    {
        Collections.sort(this.childNodes, (node1, node2) -> compareToVisits(node1, node2));
        return this.childNodes.get(this.childNodes.size()-1).move;
    }

    public Position robustMaxChild()
    {
        Collections.sort(this.childNodes, (node1, node2) -> compareToMax(node1, node2));
        return this.childNodes.get(this.childNodes.size()-1).move;
    }

    public Position secureChild()
    {
        Collections.sort(this.childNodes, (node1, node2) -> compareToSec(node1, node2));
        return this.childNodes.get(this.childNodes.size()-1).move;
    }
}
