package quoridor;

import java.util.*;
import quoridor.*;
/**
 * Created by Alice on 01.04.2017.
 */

public class AI extends Player {

    private Difficulty dif;
    int itermax;
    int timeLimit;
    int steps;
    char simulation;
    String finalMove;
    int iterations;
    private int        depth;
    private Random     rand = new Random();
    double weights[] = {0.6, 1.0};
    String bestMove = new String();

    public void setDepth(int d)
    {
        depth = d;
    }

    public int getDepth()
    {
        return depth;
    }

    public Difficulty getDiff() {
        return dif;
    }

    public void setDif(Difficulty dif) {
        this.dif = dif;
    }

    AI(Difficulty diff, int d) {
        this.dif = diff;
        depth = d;
    }

    AI (char simulation, String finalMove, int iterations, int time_per_move, Difficulty diff)
    {
        this.simulation = simulation;
        this.finalMove = finalMove;
        this.iterations = iterations;
        this.timeLimit = time_per_move;
        this.dif = diff;
    }

    public int getShortestPath(Board board, int pl) {
        Queue<Position> q = new LinkedList<Position>();
        LinkedList<Position> visited = new LinkedList<Position>();
        Position current = board.positionOf(pl);
        //System.out.println("current "+current+" "+pl);
        boolean finished = false;

        q.add(current);
        visited.add(current);
        //System.out.println("neighbours ");
        //System.out.print("path ");
        while (!q.isEmpty() && !finished) {
            current = q.remove();

            for (Position p : board.validMoves(current)) {
                if (!visited.contains(p)) {
                    p.parent = current;
                    visited.add(p);
                    q.add(p);
                    if (finished(p, pl)) {
                        current = p;
                        finished = true;
                        //System.out.println(p.toString()+" "+pl+" finished");
                        break;
                    }
                }
            }
        }
        Position c= current;
        int i = 0;
        while (c.parent!=null)
        {
            //System.out.print(c.toString()+" ");
            c = c.parent;
            i++;
        }
        //System.out.println("path length "+i);

        return i+1;
    }

    public boolean finished(Position p, Integer pl) {
        if (pl == 1 && p.getRow() == 0) {
            return true;
        } else if (pl == 2 && p.getRow() == 8) {
            return true;
        } else if (pl == 3 && p.getCol() == 0) {
            return true;
        } else if (pl == 4 && p.getCol() == 8) { return true; }
        return false;
    }

    public String getMoveNormal(Board board) {
        List<Integer> players = board.getPlayers();
        while (players.get(0) != this.getID()) {
            players.add(players.remove(0));
        }
        //depth = 1;
        /*String max_score = String.valueOf(negamax(board, this.depth, players.remove(0)))+" "+bestMove;*/
        String max_score = MinMax(board, true, players, depth);
        return max_score;
    }

    public String getMoveHard(Board board) {
        List<Integer> players = board.getPlayers();
        while (players.get(0) != this.getID()) {
            players.add(players.remove(0));
        }
        //depth = 1;
        String str = alphaBeta(players, true,
                board, depth, -1000000, +1000000, 100000000);
        /**System.out.println("move with the score "+board.get_players_object()[this.getID()-1].getName()+" "+str.split(" ")[0]);**/
        return str;

    }


    public String getMoveEasy(Board b)
    {
        //Node node = new Node();
        Node root_node = new Node(null, null, b, simulation, 0);
        //System.out.print("f");
        //node = root_node;
//        System.out.println("Untried moves");
//        for (Position v : root_node.untriedMoves)
//        {
//            System.out.print(v.toString() + " ");
//        }

        Node root_node1 = iter(root_node);
//        if (root_node1.childNodes != null)
//            System.out.print("f"+root_node1.childNodes.get(0).move.toString()+ " ");
        //return root_node.

        /****** End of MCTS iterations ********/
        TreeUtils treeHelper = new TreeUtils(root_node1);
        treeHelper.analyseAverageBranchingFactor();
        String result = " ";//= root_node1.robustChild().toString();
        if (finalMove.equals("max")) {
            result = root_node1.maxChild().toString();
        }
        else if (finalMove.equals("robust"))
        {
            result = root_node1.robustChild().toString();
        }
        else if (finalMove.equals("robustmax"))
        {
            result = root_node1.robustMaxChild().toString();
        }
        else if (finalMove == "secure")
        {
            result = root_node1.secureChild().toString();
        }

        System.out.println("\nFinal move of player " + root_node1.player + " is:" + result);

        return result;
    }

//    public String getMoveEasy(Board b) {
//
//
//        return "0"+" "/*+p.toString()*/;
//    }

    public Node iter(Node root_node)
    {
        Board root_board = root_node.board;
        int current_max_depth = 0;
        Board board = root_node.board;
        int curr_player = root_node.board.currentPlayer();
        long startTime = System.nanoTime();
        long endTime = System.nanoTime();
        int rootPlayer = root_node.board.currentPlayer();
        int iterations_counter = 0;
        Node node;
        while (((endTime - startTime)/1000/1000000) <= timeLimit)
        {
            Board state = root_board.clone();
            /*******************/

            node = root_node;
            iterations_counter+=1;

            /***** Selection *****/
            if ( (node.childNodes!=null) ) {
                while (node.untriedMoves.size() == 0 && (node.childNodes.size() > 0)) {
                    node = node.UCT();
                    board = node.board;
                    curr_player = node.player;
                    //System.out.println("in selection while "+node.move.toString());
                }
                //System.out.print("in selection after while "+node.move.toString());
            }
            /***** Expansion *****/
            if (node.untriedMoves.size() != 0)
            {
                Random generator = new Random();
                int random_index = generator.nextInt(node.untriedMoves.size());
                Position m = node.untriedMoves.get(random_index);
                Board clone_board = node.board.clone();

                state = clone_board.makeMove(m.toString());

                node = node.addChild(m, state, this.steps);
                System.out.println("child added " + m.toString());
                board = state;
                curr_player = state.currentPlayer();
            }

            /***** Simulation *****/

            int roll_palyer = curr_player;
            Board roll_board = board;
            List<Position> roll_move = generateMoves(roll_board);
            /*TODO add  more options for simulation types */
            while (!(finished(roll_board.positionOf(curr_player), curr_player)))
            {
                Random generator = new Random();
                int random_index = generator.nextInt(roll_move.size());
                roll_board = roll_board.makeMove(roll_move.get(random_index).toString());
                roll_palyer = roll_board.currentPlayer();

                 /*TODO add simulation option selection */
                roll_move = generateMoves(roll_board);
            }

            //set depth counter
            int depthCounter = -1;


            /***** Backpropagation ******/
            while (node!=null)
            {
                depthCounter += 1;
                int result = 0;
                if (roll_board.getWinner().getID() == rootPlayer)
                {
                    result = 1;
                }
                node.updateUCT(result);
                node = node.parentNode;

            }
            if (depthCounter > current_max_depth)
                current_max_depth = depthCounter;

            endTime = System.nanoTime();
            System.out.println("Iterations: " + iterations_counter);
            //   root_node = node;

        }
        //root_node = node;
        System.out.println("Iterations: " + iterations_counter);
        return root_node;
    }
    @Override
    public String nextMove(Board b) {
        String retval = "";
        if (this.dif == Difficulty.Easy) {
            retval = getMoveEasy(b).toString();
        } else if (this.dif == Difficulty.Normal) {
            retval = getMoveNormal(b);
        } else {
            retval = getMoveHard(b);
        }
        //b.printBoard();
        return retval;
    }

    public String nextMove(Board b, int st)
    {
        String retval = "";
        steps = st;
        if (this.dif == Difficulty.Easy)
        {
            retval = getMoveEasy(b).toString();
        }
        return retval;
    }

    public double score(Board state, int player) {
        List<Integer> others = state.getPlayers();
        others.remove(player - 1);
        //System.out.println("position of player "+state.positionOf(player));
        int min = getShortestPath(state, others.get(0));
        int s = getShortestPath(state, player);
        /**System.out.println("shortest path "+s);*/
        Player[] players_objects = state.get_players_object();
        int current_walls = players_objects[player-1].getNumWalls();
        int other_walls = players_objects[others.get(0)-1].getNumWalls();
        int walls_dif = current_walls - other_walls;
        //player 1 wins s - min, else if min - s player 2 wins
        return (min - s)*1.0/* + 1.5*walls_dif*/;
    }

    public static void ShuffleList(List<Position> s)
    {
        int n = s.size();
        Random generator = new Random();
        generator.nextInt();
        for (int i = 0; i<n; i++)
        {
            int change = i + generator.nextInt(n - i);
            swap(s, i, change);
        }
    }

    public static void swap(List<Position> s, int i, int change)
    {
        Position helper = s.get(i);
        s.set(i, s.get(change));
        s.set(change, helper);
    }

    public List<Position> generateMoves(Board state) {
        int player = state.currentPlayer();
        List<Position> moves = state.validMoves(state.positionOf(player));
        if (state.remainingWalls(player) > 0) {
            for (char i = '1'; i < '9'; i++) {
                for (char j = 'a'; j < 'i'; j++) {
                    Position horstate = new Position(String.valueOf(j)
                            + String.valueOf(i) + "h");
                    Position verstate = new Position(String.valueOf(j)
                            + String.valueOf(i) + "v");
                    if (state.isValidMove(horstate.toString())) {
                        moves.add(horstate);
                    }
                    if (state.isValidMove(verstate.toString())) {
                        moves.add(verstate);
                    }
                }
            }
        }

        ShuffleList(moves);
        return moves;
    }



    public String MinMax(Board b, boolean maximizing, List<Integer> players_list, int level)
    {
        String ret_val = "";
        double positive_inf = Double.POSITIVE_INFINITY;
        double negative_inf = positive_inf*(-1);
        double best_value;
        Integer p = players_list.remove(0);
        players_list.add(p);
        List<Position> moves = generateMoves(b);

        if (level == 0)
            return String.valueOf(score(b, p))+" "+" ";

        if (maximizing)
        {
            best_value = negative_inf;
            ret_val = String.valueOf(negative_inf)+" "+" ";
            for (Position child : moves)
            {
                players_list.add(players_list.remove(0));
                String ret = MinMax(b.makeMove(child.toString()), false, players_list, level - 1);
                double v = Double.parseDouble(ret.split(" ")[0]);
                if (best_value < v)
                {
                    best_value = v;
                    ret_val = ret.split(" ")[0]+" "+child.toString();
                }
            }
            return ret_val;
        }
        else
        {
            best_value = positive_inf;
            ret_val = String.valueOf(best_value)+" "+" ";
            for (Position child : moves)
            {
                players_list.add(players_list.remove(0));
                String ret = MinMax(b.makeMove(child.toString()), true, players_list, level - 1);
                double v = Double.parseDouble(ret.split(" ")[0]);
                if (best_value > v)
                {
                    best_value = v;
                    ret_val = ret.split(" ")[0]+" "+child.toString();
                }
            }
            return ret_val;
        }

    }

    public  int heuristics (Board b, int player)
    {
        int another = (player+player)%3;
        return  (3*(getShortestPath(b, player) - 3*getShortestPath(b, another))+(b.get_players_object()[another -1].getNumWalls() - b.get_players_object()[player-1].getNumWalls()));
    }

    public int negamax (Board b, int depth, int player)
    {
        int best = Integer.MIN_VALUE;
        int val;
        if (depth == 0)
        {
            return heuristics(b, player);
        }
        List<Position> moves = generateMoves(b);
        for (Position move : moves)
        {
            //System.out.print(move.toString()+" - ");
            //Board clone = b.clone();
            val = -negamax(b.makeMove(move.toString()), depth - 1, player);
            //System.out.print(val+" ,");
            if (val > best)
            {
                best = val;
                bestMove = move.toString();
            }
        }
        return best;
    }

    public String alphaBeta(List<Integer> players, boolean max, Board state,
                            Integer level, double alpha, double beta, double shortest_p) {
        String ret_val = "";
        Integer p = players.remove(0);
        players.add(p);
        double score = 0;

        if (level == 0) {
            String s = String.valueOf(score(state, p))+" "+" ";
            return s;
        } else {
            List<Position> moves = generateMoves(state);
            /*********************FOR DEBUG******************/
//            System.out.println("generated moves: ");
//            for (Position mov: moves)
//            {
//                System.out.print(mov.toString()+" ");
//            }

            if (max) {
                int i = 0;
                for (Position next : moves) {
                    score = Double.parseDouble(alphaBeta(players, false,
                            state.makeMove(next.toString()), level - 1, alpha,
                            beta, shortest_p).split(" ")[0]);
                    if (score > alpha) {
//                        System.out.println();
//                        System.out.print(" picked");
//                        System.out.println();
                        alpha = score;
                        ret_val = String.valueOf(alpha)+" "+next.toString();
                    }

                    if (beta <= alpha) {
                        break;

                    }
                    i++;
                }
                return ret_val;
            } else {
                int i = 0;
                for (Position next : moves) {
                    i++;
                    score = Double.parseDouble(alphaBeta(players, true,
                            state.makeMove(next.toString()), level - 1, alpha,
                            beta, shortest_p).split(" ")[0]);
                    //int s = getShortestPath(state, p);
                    /*******************************************************************/
                    if (score < beta) {
                        beta = score;
                        ret_val = String.valueOf(beta)+" "+next.toString();
                    }

                    if (beta <= alpha) {
                        break;
                    }

                }
                return ret_val;
            }
        }
    }

}
