import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.image.BufferedImage;
import java.applet.Applet;
import java.awt.Font;
import java.awt.Graphics;


public class WoFGUI extends JFrame  {
    
     

    // private final String setPlainText = "\033[0;0m";
     //private final String setBoldText = "\033[0;1m";

	/**
	 * The main method to create run our Wheel of fortune GUI. Very compact, 
	 * 	so it doesn't need its own class.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
        WoFGUI wof = new WoFGUI();
        wof.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        wof.setSize(1088, 900);
        wof.setLocationRelativeTo(null);
        wof.setVisible(true);
    }

 
	
	
	
	/**************************************************************************************************************/
	/**************************************************************************************************************/
	/**************************************************************************************************************/
	/**************************************************************************************************************/

		
	/**
	 * 
	 * An ActionListener implementation for buttons representing alphabet characters.
	 * When one of these buttons is clicked, it will check if the movie title contains it. If it does,
	 * 	all occurrences of that letter will be displayed on the screen. If not, another body part
	 * 	of Hang Man will appear.
	 *
	 */
	private class CharacterListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			char label = ((JButton) (e.getSource())).getText().charAt(0);
    		
			// identify the button
			int index = (int) (label) - ((int) 'A');
			buttons[index].setEnabled(false); // disable this character's button for further use

            if (buyingVowel) {
                if (!wf.buyVowel(label)) {
                    wf.nextPlayer();
                }
                buyingVowel = false;
            } else {
                if (!wf.playerGuess(label, spinValue)) {
                    wf.nextPlayer();
                }
            }

            displayedText = wf.getClue().getDisplayPhrase();
            spun = false;
            spin.setEnabled(true);
            buyVowel.setEnabled(true);
            solutionField.setEnabled(true);
			repaint();
		}
	}

    /**
     * 
     * An ActionListener implementation for the PlayAgain button. Button only enabled after the player either wins
     *  or loses a round. When pressed, everything resets with a new movie title.
     *
     *
     */
    private class SolutionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String sol = solutionField.getText();
            if (wf.submitSolve(sol)) {
                // // round is over
                // // bank currentPlayers balance
                // getNewClue();
                // initDisplayedText();
                // roundNumber++;

                // for (JButton b: buttons) {
                //     b.setEnabled(true);
                // }
                solutionField.setText("");
                displayedText = wf.getClue().getDisplayPhrase();
                repaint();
            } else {
                solutionField.setText("");// here we need to make it so that if its a toss-up, 
                wf.nextPlayer();
                repaint();
            }
            // solutionField.setText("");
            // repaint();
        }
        
    }

    /**
     * 
     * An ActionListener implementation for the Buzzer buttons for the tossup rounds. only enabled when
     * it is a tossup round. when one is pressed everything is disabled until the player that buzzed in
     * enters their solution.
     * 
     */
    private class BuzzerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // "Player x"
            //  01234567
            tossUpRound = false;
            int label = Integer.parseInt(((JButton) (e.getSource())).getText().charAt(7) + "") - 1;
            wf.setCurrentPlayer(label);

            for (JButton b: buttons) {
                b.setEnabled(false);
            }

            for (JButton b: buzzers) {
                b.setEnabled(false);
            }
            spin.setEnabled(false);
            buyVowel.setEnabled(false);
            solutionField.setEnabled(true);

            repaint();
        }
        
    }

    /**
     * 
     * An ActionListener implementation for the spin button. This spins the wheel and returns a random value on it
     * this disables the 
     *
     */
    private class SpinListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // disable solve and buy vowel buttons
            spinValue = wf.getWheel().spinWheel();
            wheelMessages = "";
            if (spinValue == 2) {
                // bankrupt current player
                
                wheelMessages += "𝙋𝙡𝙖𝙮𝙚𝙧 " + (wf.getCP() + 1) + "  𝙜𝙤𝙚𝙨 𝙗𝙖𝙣𝙠𝙧𝙪𝙥𝙩 .";
                wf.bankrupt();
            } else if (spinValue == 1) {
                // current player loses turn
                wheelMessages += "\"𝙋𝙡𝙖𝙮𝙚𝙧\" " + (wf.getCP() + 1) + " 𝗹𝗼𝘀𝗲𝘀 𝗮 𝘁𝘂𝗿𝗻.";
                wf.nextPlayer();
            } else {
                solutionField.setEnabled(false);
                buyVowel.setEnabled(false);
                spin.setEnabled(false);
                spun = true;
                wheelMessages += "𝑺𝒑𝒊𝒏 𝑽𝒂𝒍𝒖𝒆: " + spinValue + ".";
            }
            repaint();
        }
        
    }
    private class BuyVowelListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // disable solve and buy vowel buttons
            // only allow contestants to buy a vowel if they have enough money(500)
            solutionField.setEnabled(false);
            buyVowel.setEnabled(false);
            spin.setEnabled(false);
            buyingVowel = true;
            repaint();
        }
        
    }

    /**
     * 
     * An ActionListener implementation for the nextRound button. 
     *
     *
     */
    private class NextRoundListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            getNewClue();
            initDisplayedText();

            for (JButton b: buttons) {
                b.setEnabled(true);
            }
            spin.setEnabled(true);
            buyVowel.setEnabled(true);
            solutionField.setEnabled(true);
            playAgain.setEnabled(false);
            repaint();
        }
        
    }
    
		
	/**
	 * 
	 * An ActionListener implementation for the PlayAgain button. Button only enabled after the player either wins
	 * 	or loses a round. When pressed, everything resets with a new movie title.
	 *
	 *
	 */
	private class PlayAgainListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
            wf = null;
            timer = null;
            try {
                wf = new WheelOfFortune();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            getNewClue();

            buyingVowel = false;
            tossUpRound = false;
            spun = false;
            spinValue = 0;
            roundNumber = 0;
            tossUpRound = true;
            for (JButton b: buzzers) {
                b.setEnabled(true);
            }
            playAgain.setEnabled(false);
            spin.setEnabled(false);
            buyVowel.setEnabled(false);
            displayedText = wf.getClue().getDisplayPhrase();

			repaint();
		}
		
	}

    /**
     * 
     * An ActionListener implementation for the PlayAgain button. Button only enabled after the player either wins
     *  or loses a round. When pressed, everything resets with a new movie title.
     *
     *
     */
    private class QuitButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
        
    }

    /** 
     * 
     * A class that extends TimerTask in order to tick for the tossuprounds
     */
    private class TossUpTick extends TimerTask {

        public void run() {
            for (int i = 97; i < 123 && tossUpRound; i++) {
                if (wf.getClue().check(Character.toLowerCase((char)i)) > 0) {
                    displayedText = wf.getClue().getDisplayPhrase();
                    repaint();
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            // here the clue has been completed
            // cp defaults to 0, no one gets anything
            for (JButton b: buzzers) {
                b.setEnabled(false);
            }
            tossUpRound = false;
            timer = null;
        }
    }
	
	/**************************************************************************************************************/
	/**************************************************************************************************************/
	/**************************************************************************************************************/
	/**************************************************************************************************************/

	
	
	
	private static final long serialVersionUID = 1L;

    private WheelOfFortune wf; 
    private int incorrectGuessCount;
    private String[] dashes;
    
    private String actualText;
    private String displayedText;
    private String wheelMessages;
    private String[] playerInfo;// change this to be an array
    private String currentPlayer;

    private CharacterListener charListener;
    private Container container;
    private JButton[] buttons = new JButton[26];
    private JButton playAgain;
    private JButton quitButton;
    private JButton spin;
    private JButton buyVowel;
    private JButton nextRound;
    private JButton[] buzzers;
    private JTextField solutionField;
    private Image wheelImage;
    private BufferedImage bufImg;

    // game state variables
    private int roundNumber;
    private boolean buyingVowel;
    private boolean tossUpRound;
    private boolean spun;
    private int spinValue;
    private Timer timer;

    
    
    
	/**************************************************************************************************************/
	/**************************************************************************************************************/
	/**************************************************************************************************************/
	/**************************************************************************************************************/

    
    
    /**
     * 
     * @throws IOException WheelOfFortune constructor uses Scanner which throws IOException FileNotFoundException
     * 
     */
    public WoFGUI() throws IOException {
        
        super("𝑾𝒉𝒆𝒆𝒍 𝑶𝒇 𝑭𝒐𝒓𝒕𝒖𝒏𝒆");
        wf = new WheelOfFortune();

        wheelImage = new ImageIcon("wheel_image.jpeg").getImage();

        wheelMessages = "";

        buyingVowel = false;
        tossUpRound = true;
        spun = false;
        spinValue = 0;
        roundNumber = 0;

        getNewClue();
        initDisplayedText();
        // initPlayerInfo();
        setLayout(new FlowLayout());
        initButtons();
        initTextField();
        setVisible(true);
    }
    
    /**
     * Initiates or re-initiates a few fields
     */
    private void getNewClue() {
    	actualText = wf.getNext(roundNumber + 1);
        displayedText = "";
    }
    
    /**
     * Initializes the displayedText variable, which is then displayed on the GUI.
     */
    private void initDisplayedText() {
        displayedText = wf.getClue().getDisplayPhrase();
        // for (int i = 0; i < actualText.length(); i++) {
        //     if (actualText.charAt(i) == ' ') {
        //     	dashes[i] = "      ";
        //     }
        //     else {
        //     	dashes[i] = "_____ ";
        //     }
            
        //     displayedText += dashes[i];
        // }  
    }

    // /**
    //  * Initializes the playerInfo variable, which is displayed on the GUI
    //  */
    // private void initPlayerInfo() {
    //     playerInfo = wf.getPlayerString();
    // }
    
    /**
     * Initializes all the buttons of the GUI, references the private classes written at the top.
     */
    private void initButtons() {
    	container = getContentPane();
    	charListener = new CharacterListener();

        for (int i = 0; i < 26; i++) {
        	// Generate every character in the alphabet and use as labels for buttons
        	char alpha = (char) ((int) 'A' + i);
        	buttons[i] = new JButton(String.valueOf(alpha));
        	container.add(buttons[i]);
        	buttons[i].addActionListener(charListener);
            buttons[i].setEnabled(false);
        }
        buzzers = new JButton[wf.numPlayers()];
        for (int i = 0; i < buzzers.length; i++) {
            buzzers[i] = new JButton("Player " + (i + 1));
            container.add(buzzers[i]);
            buzzers[i].addActionListener(new BuzzerListener());
            buzzers[i].setEnabled(false);
        }
        spin = new JButton("𝕊𝕡𝕚𝕟");
        container.add(spin);
        spin.addActionListener(new SpinListener());
        spin.setEnabled(true);

        buyVowel = new JButton("Buy Vowel");
        container.add(buyVowel);
        buyVowel.addActionListener(new BuyVowelListener());
        buyVowel.setEnabled(true);

        // add next round button here - enabled if the round is over, blocking
        nextRound = new JButton("✌️Next✌️ ✌️Round✌️");
        container.add(nextRound);
        nextRound.addActionListener(new NextRoundListener());
        nextRound.setEnabled(false);

        playAgain = new JButton("P̐̈l̐̈a̐̈y̐̈ ̐̈A̐̈g̐̈a̐̈i̐̈n̐̈\"̐̈");// change to reset the whole game, only enabled when game over
        container.add(playAgain); 
        playAgain.addActionListener(new PlayAgainListener());
        playAgain.setEnabled(false);

        // add quit button here - enable when game over, just System.exit(0) if clicked
        quitButton = new JButton("Q̑̇̈ȗ̇̈ȋ̇̈t̑̇̈");
        container.add(quitButton);
        quitButton.addActionListener(new QuitButtonListener());
        quitButton.setEnabled(true);

    }

    /**
     * Initializes the text fild of the GUI, references the private classes written at the top.
     */
    private void initTextField() {
        container = getContentPane();
        solutionField = new JTextField(20);
        container.add(solutionField);
        solutionField.addActionListener(new SolutionListener());
        solutionField.setEnabled(true);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    /**
     * Overrides the super class's paint method. 
     * repaint() will call this method without recreating the pop up window. 
     */
    public void paint(Graphics g) {
        super.paint(g);

        bufImg = new BufferedImage(wheelImage.getWidth(null), wheelImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bufImg.createGraphics();
        g2.drawImage(wheelImage, 0, 0, null);
        try {
            bufImg = resizeImage(bufImg, 200, 200);
        } catch (Exception e) {
            e.printStackTrace();
        }
        g2.dispose();

        g2 = (Graphics2D) g;

        g2.drawImage(bufImg, null, 500, 175);
        
        g2.drawString(displayedText, 100, 175);

        g2.drawString(wheelMessages, 100, 200);

        currentPlayer = "𝕻𝖑𝖆𝖞𝖊𝖗 " + (wf.getCP() + 1) + "'𝖘 𝖙𝖚𝖗𝖓.";
        g2.drawString(currentPlayer, 100, 225);

        playerInfo = wf.getPlayerString();
        for (int i = 0; i < playerInfo.length; i++) {
            g2.drawString(playerInfo[i], 210, 250 + (i * 25));
        }

        g2.drawString("𝕽𝖔𝖚𝖓𝖉: " + (roundNumber + 1), 100, 250 + (playerInfo.length * 25));

        nextRound.setEnabled(false);

        for (JButton b: buttons) {
            if (wf.getClue().getGuesses().indexOf(Character.toLowerCase(b.getText().charAt(0))) == -1 && spun) {
                b.setEnabled(true);
            } else {
                b.setEnabled(false);
            }
        }

        if (buyingVowel) {
            for (JButton b: buttons) {
                // if it is a vowel, and it hasn't been guessed, enable it
                displayedText += b.getText().charAt(0) + " ";
                if ("aeiou".indexOf(Character.toLowerCase(b.getText().charAt(0))) != -1 && wf.getClue().getGuesses().indexOf(Character.toLowerCase(b.getText().charAt(0))) == -1) {
                    b.setEnabled(true);
                }
            }
        }

        if (tossUpRound) {
            if (timer == null) {
                timer = new Timer();
                timer.schedule(new TossUpTick(), 1000);
            }

            for (JButton b: buzzers) {
                b.setEnabled(true);
            }
        }

        if (isCompleted()) {
            for (JButton b: buttons) {
                b.setEnabled(false);
            }
            spin.setEnabled(false);
            buyVowel.setEnabled(false);
            solutionField.setEnabled(false);
            nextRound.setEnabled(true);
            Ellipse2D.Double leftEye = new Ellipse2D.Double(100, 500, 5, 5);
            g2.draw(leftEye);
            Ellipse2D.Double rightEye = new Ellipse2D.Double(118, 500, 5, 5);
            g2.draw(rightEye);
            Arc2D.Double mouth = new Arc2D.Double(100, 519, 23, 12, 0, -180, Arc2D.OPEN);
            g2.draw(mouth);
            if (roundNumber < 4) {
                roundNumber++;
            }
        }

        if (roundNumber >= 4) {
            g2.drawString("G͛͛͛a͛͛͛m͛͛͛e͛͛͛ ͛͛͛O͛͛͛v͛͛͛e͛͛͛r͛͛͛.͛͛͛", 100, 250 + ((playerInfo.length + 1) * 25));
            // call method to wtite game data to file in wf
            // ask playAgain
            wf.updateStandings();
            playAgain.setEnabled(true);
            nextRound.setEnabled(false);
        }
    }

    /**
     * 
     * @return true if the players have completed the clue
     */
    private boolean isCompleted() {
       	return wf.getClue().completed();
    }
}