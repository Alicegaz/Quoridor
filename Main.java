package quoridor;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
public class Main {
    private static quoridor.Game game;
    public static void main (String[] argv) throws IOException{
        String[] AInames = {"Player 1", "Player 2"};
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Welcome!");
        String temp = "";
        Integer size =2;
        Player[] players;
        players = new Player[size];
        int iter;
        char simulation;
        String final1;
        int time_per_move;
        for (Integer i = 1; i<= size; i++){
            temp = "";
            while (!temp.toLowerCase().matches("(monte( carlo)?|mini(max)?|a(lpha beta)?)")){
                System.out.print("quoridor.AI difficulty (monte carlo/minimax/alpha beta): ");
                temp = in.readLine();
            }
            quoridor.Difficulty diff;
            if (temp.toLowerCase().matches("monte( carlo)?")){
                diff = quoridor.Difficulty.Easy;
                System.out.println("Select the method to choose the final move max / robust / robustmax / secure ");
                temp = in.readLine();
                String finalMove = temp;
                System.out.println("Set the number of iterations");
                temp = in.readLine();
                int iterations = Integer.parseInt(temp);
                System.out.println("Set the time per move ");
                temp = in.readLine();
                int time_per = Integer.parseInt(temp);
                players[i - 1] = new quoridor.AI('a', finalMove, iterations, time_per, diff);
            } else if (temp.toLowerCase().matches("a(lpha beta)?")){
                diff = quoridor.Difficulty.Hard;
                System.out.println("Set the depth ");
                temp = in.readLine();
                players[i - 1] = new quoridor.AI(diff, Integer.parseInt(temp));
            } else {
                diff = quoridor.Difficulty.Normal;
                System.out.println("Set the depth ");
                temp = in.readLine();
                players[i - 1] = new quoridor.AI(diff, Integer.parseInt(temp));
            }

            players[i - 1].setName(AInames[i-1]);

        }

        game = new quoridor.Game(players);// players);

        game.play();
        game.getBoard().printBoard();


        /*********************************************************/
        System.out.println("Player "+ game.getWinner().getName() + " wins!");
        System.out.println("Run 20 iterations for test? (yes/no)");
        temp = in.readLine();
        if (temp.toLowerCase().matches("y(es)"))
        {
            BufferedReader in1 = new BufferedReader(new InputStreamReader(System.in));
            quoridor.Difficulty d1;
            quoridor.Difficulty d2;
            int winnres[] = {0, 0};
            String[] AInames1 = {"Player 1", "Player 2"};
            Player[] players1;
            players1 = new Player[2];
            for (int k = 0; k < 2; k++) {
                String temp1 = "";
                System.out.println("Type monte/alpha/mini");
                temp1 = in1.readLine();
                System.out.print(temp1);
                if (temp1.toLowerCase().equals("monte")) {
                    players1[k] = new quoridor.AI('a', "max", 1, 1, quoridor.Difficulty.Easy);
                } else if (temp1.toLowerCase().equals("alpha")) {
                    d1 = quoridor.Difficulty.Hard;
                    players1[k] = new quoridor.AI(d1, 1);
                    System.out.print("hard ");
                } else{
                    d1 = quoridor.Difficulty.Normal;
                    players1[k] = new quoridor.AI(d1, 1);
                    System.out.print("norm ");
                }
                players[k].setName(AInames[k]);
            }
            for (int i = 0; i<5; i++)
            {
                quoridor.Game game2 = new quoridor.Game(players1);// players);
                game2.play();
                game2.getBoard().printBoard();
                winnres[game2.getWinner().getID()-1]+=1;
                System.out.println("Player "+ game2.getWinner().getName() + " wins!");
            }
            System.out.println("Player1 wins "+winnres[0] + " times "+ " Player2 wins "+winnres[1]);


        }

    }

    public static void test1(quoridor.Difficulty d1, quoridor.Difficulty d2)
    {

    }

    static boolean tryParseInt(String value)
    {
        try
        {
            Integer.parseInt(value);
            return true;
        } catch(NumberFormatException nfe)
        {
            return false;
        }
    }
}