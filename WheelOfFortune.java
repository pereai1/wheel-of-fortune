import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.*;
import java.util.*;

/**
 * A class representing a WheelOfFortune object
 *
 */
public class WheelOfFortune
{
    private Clue clue;
    private Scanner in;
    private Player[] players;
    private int cp;
    private Wheel wheel;
    
    public WheelOfFortune() throws FileNotFoundException {
        clue = new Clue();
        players = new Player[4];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
        }
        cp = 0;
        wheel = new Wheel();
    }

    public Clue getClue() {
        return this.clue;
    }

    public Wheel getWheel() {
        return this.wheel;
    }

    public void nextPlayer() {
        this.cp++;
        this.cp %= this.players.length;
    }
    public int getCP() {
        return this.cp;
    }
    public void setCurrentPlayer(int index) {
        this.cp = index % this.players.length;
    }
    public int numPlayers() {
        return this.players.length;
    }
    public void bankrupt() {
        this.players[this.cp].setBal(0);
        this.cp++;
        this.cp %= this.players.length;
    }
    public String[] getPlayerString() {
        String[] toString = new String[this.players.length];
        for (int i = 0; i < this.players.length; i++) {
            toString[i] = "P̳l̳a̳y̳e̳r̳ " + (i + 1) + 
                        ": \nB̳a̳l̳a̳n̳c̳e̳: " + this.players[i].getBal() + 
                        " \nB̳a̳n̳k̳: " + this.players[i].getBank();
        }
        return toString;
    }

    public boolean buyVowel(char c) {
        this.players[this.cp].addBal(-500);
        return (this.clue.check(c) > 0);
    }

    public boolean playerGuess(char c, int value) {
        int hits = this.clue.check(c);// this returns -1 if its a miss
        if (hits < 0) {
            return false;
        }
        this.players[this.cp].addBal(hits * value);
        return true;
        //System.out.println((hits * value) + " added.");
    }

    public boolean submitSolve(String s) {
        // check to see if s is the correct string
        return this.clue.solve(s);
    }

    public String getNext(int round) {
        this.players[this.cp].addBank(this.players[this.cp].getBal());
        for (int i = 0; i < this.players.length; i++) {
            this.players[i].setBal(0);
        }
        this.clue.newRound();
        this.wheel.newWheel(round);
        return this.clue.getPhrase();
    }

    public boolean isEmpty() {
        // if 
        return false;
    }

    private final String FILE_PATH = "data_file.csv";
    public void updateStandings() {
        // store player name, score
        ArrayList<Player> standings = new ArrayList<Player>();
        try {
            FileReader reader = new FileReader(new File(FILE_PATH));
            BufferedReader bR = new BufferedReader(reader);

            String line;

            while ((line = bR.readLine()) != null) {
                Player p = new Player();
                p.setBank(Integer.parseInt(line.substring(line.indexOf(",")  + 2)));
                standings.add(p);
            }
            reader.close();

            for (int i = 0; i < players.length; i++) {
                standings.add(players[i]);
            }
            Collections.sort(standings, new Comparator<Player>(){
                public int compare(Player o1, Player o2){
                    return o2.getBank() - o1.getBank();
                }
            });
            new FileWriter(FILE_PATH, false).close();
            FileWriter writer = new FileWriter(FILE_PATH, false);
            for (int i = 0; i < standings.size() && i < 5; i++) {
                System.out.println("Player " + i + ", " + standings.get(i).getBank() + "\n");
                writer.write("Player " + i + ", " + standings.get(i).getBank() + "\n");
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
