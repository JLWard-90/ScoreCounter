import javax.swing.*;//Imports the java swing library to produce a gui
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.File;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

/*
A simple but versatile score counter for a variety of tabletop games.
Written by Jacob Ward
Current version 1.0 updated 21.07.2019
*/

class AboutWindow{
  private String about;
  public AboutWindow(){
    about = new String("A simple but versatile score counter for a variety of tabletop games.\nWritten by Jacob Ward.\nCurrent version: 1.0.0, updated 21.07.2019");
    JFrame dialogue = new JFrame();
    dialogue.setSize(500,200);
    dialogue.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //Closes just this JFrame and not the entire program
    JPanel textPanel = new JPanel();
    JTextArea textarea = new JTextArea(about);
    textarea.setBounds(10,30, 200,200);
    textPanel.add(textarea);
    JPanel buttonPanel = new JPanel();
    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        dialogue.dispatchEvent(new WindowEvent(dialogue, WindowEvent.WINDOW_CLOSING));
      }
    });
    buttonPanel.add(closeButton);
    dialogue.getContentPane().add(BorderLayout.CENTER, textPanel);
    dialogue.getContentPane().add(BorderLayout.SOUTH, buttonPanel);
    dialogue.setVisible(true);
  }
}

class Player { //The player class contains all of the information for each player
  private String name;
  private int score;
  private int playerNumber;
  public Player(String name, int score, int playerNumber){ //This is the constructor. It is necessary to instantiate new instances of the player class
    this.name = name;
    this.score = score;
    this.playerNumber = playerNumber;
  }
  public void printPlayerName(){ //Method returning void to print player name
    System.out.println(this.name);
  }
  public void setPlayerName(String name){//Method setting the name of the player
    this.name = name;
  }
  public String getPlayerName(){//Method to return player name
    return this.name;
  }
  public void printPlayerScore(){//above but for score
    System.out.println(this.score);
  }
  public void setPlayerScore(int newScore){
    this.score = newScore;
  }
  public int getPlayerScore(){//above but for player number
    return this.score;
  }
  public void printPlayerNumber(){
    System.out.println(this.playerNumber);
  }
  public void setPlayerNumber(int newNumber){
    this.playerNumber = newNumber;
  }
  public void changePlayerScore(int scoreMod){
    this.score += scoreMod;
    boolean hasLost = ScoreCounter.gController.CheckifLose(this.score);
    if(hasLost){
      ScoreCounter.gController.OnPlayerLose(this.name);
    }
    boolean hasWon = ScoreCounter.gController.CheckifWin(this.score);
    if(hasWon){
      ScoreCounter.gController.OnPlayerWin(this.name);
    }
  }
}

class PlayersContainer{ //This is juse a simple container in which to store multiple player objects
  private Player[] thePlayers;
  public PlayersContainer(){

  }
  public void populatePlayers(String[] playerNames, int startScore){
    this.thePlayers = new Player[playerNames.length];
    for (int i = 0; i < playerNames.length; i++) {
      this.thePlayers[i] = new Player(playerNames[i],startScore,i+1);
      //System.out.println("populating player");
    }
  }
  public String getThePlayerName(int playerID){
    Player q = thePlayers[playerID];
    return q.getPlayerName();
  }
  public void changeThePlayerScore(int playerID, int update){
    thePlayers[playerID].changePlayerScore(update);
    Player q = thePlayers[playerID];
    //System.out.println(q.getPlayerName());
  }
  public int getPlayerScore(int playerID){
    return thePlayers[playerID].getPlayerScore();
  }
}

class scoreChangeButton{ //This class contains everything that is needed for the simple score change buttons.
  private JButton button;
  private int scoreChange;
  private String name;
  private int playerID;
  public scoreChangeButton(int scoreC, int playerIndex){
    this.scoreChange = scoreC;
    this.playerID = playerIndex;
    if (scoreC < 0){
      this.name = Integer.toString(scoreC);
    } else{
      this.name = "+" + scoreC;
    }
  }
  public void setupTheButton(){
    this.button = new JButton(name);
    this.button.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        //System.out.println("button push");
        OnButtonPush();
       }
     });
  }
  public JButton GetButton(){
    return this.button;
  }
  public int getButtonScoreChange(){
    return this.scoreChange;
  }
  public void OnButtonPush(){
    ScoreCounter.PlayerBox.changeThePlayerScore(this.playerID, this.scoreChange);
    //System.out.println(ScoreCounter.PlayerBox.getPlayerScore(this.playerID));
    ScoreCounter.TheScoreCounters[this.playerID].updateScoreLabel(ScoreCounter.PlayerBox.getPlayerScore(this.playerID));
  }
}

class PlayerScoreCounterGUI { //Using this to contain a separate panel for each player. This will allow for a wider range of layouts and player numbers
  JPanel playerPanel;
  GridBagConstraints gbc;
  GridBagLayout layout;
  JLabel playerNameLabel; //The player name
  JLabel scoreLabel; //Label to display each player's score

  public PlayerScoreCounterGUI(String PlayerName, int startScore, int playerNumber){
    playerPanel = new JPanel(new GridBagLayout());
    gbc = new GridBagConstraints();
    layout = new GridBagLayout();
    playerPanel.setLayout(layout);
    playerPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0;
    gbc.gridy = 0;
    if(ScoreCounter.NumberOfPlayers > 2){
      gbc.gridwidth = 3;
    }else{
      gbc.gridwidth = 2;
    }
    playerNameLabel = new JLabel(PlayerName,SwingConstants.CENTER);
    playerPanel.add(playerNameLabel,gbc);

    //Set up the score label
    scoreLabel = new JLabel(("Score: "+Integer.toString(startScore)),SwingConstants.CENTER);

    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.ipady = 20;
    gbc.gridx = 0;
    gbc.gridy = 1;
    playerPanel.add(scoreLabel,gbc);

    //plus buttons
    gbc.gridwidth = 1;
    gbc.gridx = 0;
    gbc.gridy = 2;
    //Setup the score change buttons for player 1:
    //Add to score:
    scoreChangeButton p1plus1button = new scoreChangeButton(1,playerNumber);
    p1plus1button.setupTheButton();
    playerPanel.add(p1plus1button.GetButton(), gbc);
    gbc.gridx = 1;
    gbc.gridy = 2;
    scoreChangeButton p1plus2button = new scoreChangeButton(2,playerNumber);
    p1plus2button.setupTheButton();
    playerPanel.add(p1plus2button.GetButton(), gbc);
    gbc.gridx = 2;
    gbc.gridy = 2;
    scoreChangeButton p1plus5button = new scoreChangeButton(5,playerNumber);
    p1plus5button.setupTheButton();
    playerPanel.add(p1plus5button.GetButton(), gbc);
    //reduce score:
    gbc.gridx = 0;
    gbc.gridy = 3;
    scoreChangeButton p1minus1button = new scoreChangeButton(-1,playerNumber);
    p1minus1button.setupTheButton();
    playerPanel.add(p1minus1button.GetButton(), gbc);
    gbc.gridx = 1;
    gbc.gridy = 3;
    scoreChangeButton p1minus2button = new scoreChangeButton(-2,playerNumber);
    p1minus2button.setupTheButton();
    playerPanel.add(p1minus2button.GetButton(), gbc);
    gbc.gridx = 2;
    gbc.gridy = 3;
    scoreChangeButton p1minus5button = new scoreChangeButton(-5,playerNumber);
    p1minus5button.setupTheButton();
    playerPanel.add(p1minus5button.GetButton(), gbc);

    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 2;
    JTextField anyScoreChange = new JTextField("Change to score");
    playerPanel.add(anyScoreChange, gbc);

    JButton commitButton = new JButton("OK");
    gbc.gridx = 2;
    gbc.gridy = 4;
    gbc.gridwidth = 1;
    commitButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        //System.out.println("commit button push"+PlayerName);
        String s1=anyScoreChange.getText();
        try{
          int a=Integer.parseInt(s1);
          //System.out.println(Integer.toString(a));
          ScoreCounter.PlayerBox.changeThePlayerScore(playerNumber, a);
          ScoreCounter.TheScoreCounters[playerNumber].updateScoreLabel(ScoreCounter.PlayerBox.getPlayerScore(playerNumber));
        }
        catch(Exception Ex){
          System.out.println("Change in score must be an integer!");
        }
       }
     });
    playerPanel.add(commitButton, gbc);
  }

  public void updateScoreLabel(int newScore){
    this.scoreLabel.setText("Score: "+Integer.toString(newScore));
  }

  public void updatePlayerLabel(String newPlayerName){
    this.playerNameLabel.setText(newPlayerName);
  }

  public JPanel getPlayerPanel(){
    return this.playerPanel;
  }
}

class GameController{ //This is the game controller. It is responsible for storing the win and/or lose conditions and deciding when those conditions have been met.
  int thresholdScoreLose;
  int thresholdScoreWin;
  String loseConditionString;
  String winConditionString;
  int loseConditionInt;
  int winConditionInt;
  int nLoser = 0;
  public GameController(String loseConditionString, int thresholdScoreLose, String winConditionString, int thresholdScoreWin){
    this.thresholdScoreLose = thresholdScoreLose;
    this.thresholdScoreWin = thresholdScoreWin;
    this.loseConditionString = loseConditionString;
    this.winConditionString = winConditionString;
    if(loseConditionString == "lowScoreThreshold"){ //Set the lose condition code
      loseConditionInt = 1;
    } else if(loseConditionString == "highScoreThreshold"){
      loseConditionInt = 2;
    } else {
      loseConditionInt = 0;
    }

    if(winConditionString == "lowScoreThreshold"){
      winConditionInt = 1;
    } else if(winConditionString == "highScoreThreshold"){
      winConditionInt = 2;
    } else{
      winConditionInt = 0;
    }

  }
  public boolean CheckifLose(int Score){
    if(loseConditionInt == 1){
      if(Score <= this.thresholdScoreLose){
        return true;
      } else{
        return false;
      }
    }else if(loseConditionInt == 2){
      if(Score >= this.thresholdScoreLose){
        return true;
      } else{
        return false;
      }
    }else{
      return false;
    }
  }

  public boolean CheckifWin(int score){
    if(winConditionInt==1){
      if(score <= this.thresholdScoreWin){
        return true;
      }else{
        return false;
      }
    } else if(winConditionInt==2){
      if(score >= this.thresholdScoreWin){
        return true;
      }else{
        return false;
      }
    }else{
      return false;
    }
  }

  public boolean CheckIfEndGameOnLose(){
    if(nLoser >= (ScoreCounter.NumberOfPlayers - 1)){
      return true;
    } else {
      return false;
    }
  }
  public void OnGameEnd(){
    System.out.println("Game Over");
    int a=JOptionPane.showConfirmDialog(ScoreCounter.frame,"Game Over. Play again?");
    if(a==JOptionPane.YES_OPTION){
      ScoreCounter.profile.resetCounter();
    }
  }
  public void OnPlayerLose(String playerName){
    nLoser++;
    String stringout = new String(playerName+" loses!");
    JOptionPane.showMessageDialog(ScoreCounter.frame,stringout);
    System.out.println(playerName+" loses!");
    boolean gOver = CheckIfEndGameOnLose();
    if(gOver){
      System.out.println("Game Over.");
      OnGameEnd();
    }
  }
  public void OnPlayerWin(String playerName){
    String stringout = new String(playerName+" wins!");
    JOptionPane.showMessageDialog(ScoreCounter.frame,stringout);
    OnGameEnd();
  }
}

class CounterProfile implements Serializable { //This is the class used to reset the game and will can also be used to save preferences.
  String ident;
  int NumberOfPlayers;
  String[] playerNames;
  int PlayersPerRow;
  int startScore;
  String winCondition;
  int winConditionThresholdScore;
  String LoseCondition;
  int loseConditionThresholdScore;
  public CounterProfile(String identity,int NumberOfPlayers,String[] playerNames,int PlayersPerRow,int startScore,String winCondition,int winConditionThresholdScore,String LoseCondition,int loseConditionThresholdScore){
    this.ident = identity;
    this.NumberOfPlayers = NumberOfPlayers;
    this.playerNames = playerNames;
    this.PlayersPerRow = PlayersPerRow;
    this.startScore = startScore;
    this.winCondition = winCondition;
    this.winConditionThresholdScore = winConditionThresholdScore;
    this.LoseCondition = LoseCondition;
    this.loseConditionThresholdScore = loseConditionThresholdScore;
  }
  public void resetCounter(){
    ScoreCounter.NumberOfPlayers = this.NumberOfPlayers;
    ScoreCounter.playerNames = this.playerNames;
    ScoreCounter.PlayersPerRow = this.PlayersPerRow;
    ScoreCounter.startScore = this.startScore;
    ScoreCounter.gController = new GameController(this.LoseCondition,this.loseConditionThresholdScore,this.winCondition,this.winConditionThresholdScore);
    ScoreCounter.PlayerBox.populatePlayers(ScoreCounter.playerNames, ScoreCounter.startScore);
    ScoreCounter.setupGui();
  }
  void writeProfileToFile(String filepath){
    try{
      FileOutputStream fileOut = new FileOutputStream(filepath);
      ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
      objectOut.writeObject(this);
      objectOut.close();
      System.out.println("The Profile  was succesfully written to a file");
    }
    catch (Exception ex) {
            ex.printStackTrace();
    }
  }

}

class SelectInbuildProfileList{
  JFrame dialogue;
  JPanel panel;
  JButton okButton;
  JButton cancelButton;
  CounterProfile outProfile;
  ProfileContainer profileBox;
  public SelectInbuildProfileList(ProfileContainer theProfiles){
    this.profileBox = theProfiles;
    dialogue = new JFrame("Select profile from list");
    dialogue.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //Closes just this JFrame and not the entire program
    dialogue.setSize(500,600);
    DefaultListModel<String> l1 = new DefaultListModel<>();
    for (int i = 0; i < this.profileBox.listProfiles.size(); i++) {
      l1.addElement(this.profileBox.getProfileWithInt(i).ident);
    }
    JList<String> list = new JList<>(l1);
    list.setBounds(100,100, 75,75);
    dialogue.getContentPane().add(BorderLayout.CENTER,list);

    panel = new JPanel();
    okButton = new JButton("OK");
    okButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        outProfile = profileBox.getProfile(list.getSelectedValue());
        ScoreCounter.profile = outProfile;
        ScoreCounter.frame.dispose();
        ScoreCounter.profile.resetCounter();
        dialogue.dispatchEvent(new WindowEvent(dialogue, WindowEvent.WINDOW_CLOSING));
      }
    });
    cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        dialogue.dispatchEvent(new WindowEvent(dialogue, WindowEvent.WINDOW_CLOSING));
      }
    });
    panel.add(okButton);
    panel.add(cancelButton);
    dialogue.getContentPane().add(BorderLayout.SOUTH, panel);
    dialogue.setVisible(true);
  }
  public CounterProfile getCounterProfile(){
    return this.outProfile;
  }
}

class MenuBar{
  //This will contain the menu bar class so that it is easy to modify and won't get lost.
  //Move the current menu bar code from line ~359 to here.
  JMenuBar mb;
  JMenu m1;//File
  JMenu m2;//Help
  JMenuItem m11;//New
  JMenu m12;//Load profile
  JMenuItem m13;//Save profile
  JMenuItem m14;//Exit
  JMenuItem m21;//About
  JMenuItem m22;//Help (instructions)
  JMenuItem m121; //load profiles from default list
  JMenuItem m122; //load profile from disk
  JFileChooser fileChooser; //For opening files from disk
  public MenuBar(){
    mb = new JMenuBar();
    //top layer of menu bar:
    m1 = new JMenu("FILE");
    m2 = new JMenu("Help");
    mb.add(m1);
    mb.add(m2);
    //2nd layer:
    m11 = new JMenuItem("New");
    m11.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        SetupNewProfile newPdialogue = new SetupNewProfile();
      }
    });
    m12 = new JMenu("Load profile");
    m13 = new JMenuItem("Save profile");
    m13.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        fileChooser = new JFileChooser(); //The default file choose has no text bar... I may have to just make my own.
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        int result = fileChooser.showSaveDialog(ScoreCounter.frame);
        if (result == JFileChooser.APPROVE_OPTION) {
        // user selects a file
        File selectedFile = fileChooser.getSelectedFile();
        ScoreCounter.profile.writeProfileToFile(selectedFile.getAbsolutePath());
      }
      }
    });
    m14 = new JMenuItem("Exit");
    m14.addActionListener(new ActionListener(){//Action to close the app.
      public void actionPerformed(ActionEvent e){
           ScoreCounter.frame.dispatchEvent(new WindowEvent(ScoreCounter.frame, WindowEvent.WINDOW_CLOSING));
       }
    });
    m1.add(m11);
    m1.add(m12);
    m1.add(m13);
    m1.add(m14);
    m21 = new JMenuItem("About");
    m21.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        AboutWindow aboutdialogue = new AboutWindow();
      }
    });
    m22 = new JMenuItem("Help");
    m2.add(m21);
    m2.add(m22);
    m121 = new JMenuItem("Default profiles...");
    m121.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        SelectInbuildProfileList listDialogue = new SelectInbuildProfileList(ScoreCounter.profileBox);
      }
    });
    m122 = new JMenuItem("Load from disk...");
    m122.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        int result = fileChooser.showOpenDialog(ScoreCounter.frame);
        if (result == JFileChooser.APPROVE_OPTION) {
        // user selects a file
        File selectedFile = fileChooser.getSelectedFile();
        System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        //Now load the file:
        try {
            FileInputStream fileIn = new FileInputStream(selectedFile.getAbsolutePath());
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            Object obj = objectIn.readObject();
            objectIn.close();
            CounterProfile loadedProfile = (CounterProfile) obj;
            ScoreCounter.profile = loadedProfile;
            ScoreCounter.frame.dispose();
            ScoreCounter.profile.resetCounter();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        }
      }
    });
    m12.add(m121);
    m12.add(m122);
  }
  public JMenuBar getMenuBar(){
    return this.mb;
  }
  public void loadProfileFromDisk(){ //This will need to change to a public CounterProfile class
    System.out.println("Loading files is not yet implemented..."); //This method will load files containing important profile information from the disk
  }
}

class ProfileContainer{ //A box to keep our profiles in
  ArrayList<CounterProfile> listProfiles;
  public ProfileContainer(){
    this.listProfiles = new ArrayList<>();
  }
  public void populateDefaultProfiles(){
    CounterProfile StarRealmsProfile = new CounterProfile("StarRealmsProfile",2,ScoreCounter.playerNames,2,50,"None",0,"lowScoreThreshold",0);
    CounterProfile fourPlayerInf = new CounterProfile("fourPlayerInf",4,ScoreCounter.playerNames,2,50,"None",0,"None",0);
    CounterProfile SixPlayerTo100Profile = new CounterProfile("SixPlayerTo100Profile",6,ScoreCounter.playerNames,3,0,"highScoreThreshold",100,"None",0);
    this.listProfiles.add(StarRealmsProfile);
    this.listProfiles.add(fourPlayerInf);
    this.listProfiles.add(SixPlayerTo100Profile);
  }
  public CounterProfile getProfile(String ident){
    CounterProfile outprofile;
    boolean profileFound = false;
    outprofile = this.listProfiles.get(0);
    for (int i = 0; i < this.listProfiles.size(); i++) {
      CounterProfile prof = this.listProfiles.get(i);
      if(ident == prof.ident){
        outprofile = prof;
        profileFound = true;
      }
    }
    if(profileFound){
      return outprofile;
    }
    else{
      System.out.println("profile not found!");
      return outprofile;
    }
  }
  public void addProfile(CounterProfile newProfile){
    this.listProfiles.add(newProfile);
  }
  public CounterProfile getProfileWithInt(int i){
    CounterProfile outProfile = this.listProfiles.get(i);
    return outProfile;
  }
}

class SetupNewProfile{
  CounterProfile newProfile;
  String ident;
  int NumberOfPlayers;
  String[] playerNames;
  int PlayersPerRow;
  int startScore;
  String winCondition;
  int winConditionThresholdScore;
  String LoseCondition;
  int loseConditionThresholdScore;
  JFrame dialogue;
  JPanel panel;
  JPanel lowPanel;
  JButton okButton;
  JButton cancelButton;
  int minPlayers = 1;
  int maxPlayers = 6;
  SetupNewProfile(){
    dialogue = new JFrame("Create a new profile");
    dialogue.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //Closes just this JFrame and not the entire program
    dialogue.setSize(500,600);
    panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    //Now for the controls:
    JLabel pnlabel = new JLabel("Player names:");
    JTextField playerName1 = new JTextField("Player 1        ");
    JTextField playerName2 = new JTextField("Player 2        ");
    JTextField playerName3 = new JTextField("Player 3        ");
    JTextField playerName4 = new JTextField("Player 4        ");
    JTextField playerName5 = new JTextField("Player 5        ");
    JTextField playerName6 = new JTextField("Player 6        ");
    //A slider for the number of players
    JLabel nPlayerLabel = new JLabel("Number of players: ");
    JSlider nPlayerControl = new JSlider(minPlayers, maxPlayers,2);
    nPlayerControl.setMajorTickSpacing(1);
    nPlayerControl.setPaintTicks(true);
    nPlayerControl.setPaintLabels(true);
    //2 sets of radio buttons for On/off win condition and lose condition
    JLabel WClabel = new JLabel("Win Condition:");
    JRadioButton wincond1=new JRadioButton("Low score", false);
    JRadioButton wincond2=new JRadioButton("High score", false);
    JRadioButton wincond3=new JRadioButton("None", true);
    ButtonGroup bgwin=new ButtonGroup();//Need a button group so that only one can be selected
    bgwin.add(wincond1);
    bgwin.add(wincond2);
    bgwin.add(wincond3);
    JLabel LClabel = new JLabel("Lose Condition:");
    JRadioButton losecond1=new JRadioButton("Low score", false);
    JRadioButton losecond2=new JRadioButton("High score", false);
    JRadioButton losecond3=new JRadioButton("None", true);
    ButtonGroup bglose=new ButtonGroup();//Need a button group so that only one can be selected
    bglose.add(losecond1);
    bglose.add(losecond2);
    bglose.add(losecond3);

    JLabel thresLabel1 = new JLabel("Threshold: ");
    JLabel thresLabel2 = new JLabel("Threshold: ");
    JTextField winThresholdBox = new JTextField("100  ");
    JTextField loseThresholdBox = new JTextField("0  ");

    JTextField startScoreBoc = new JTextField("50    ");

    JButton OkButton = new JButton("OK");
    OkButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        OnOKButton(nPlayerControl, playerName1,
         playerName2,
         playerName3,
         playerName4,
         playerName5,
         playerName6,
         wincond1,
         wincond2,
         wincond3,
         losecond1,
         losecond2,
         losecond3,
         winThresholdBox,
         loseThresholdBox,
         startScoreBoc);
      }
    });
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        dialogue.dispatchEvent(new WindowEvent(dialogue, WindowEvent.WINDOW_CLOSING));
      }
    });

    gbc.gridx = 1;
    gbc.gridy = 0;
    panel.add(pnlabel,gbc);


    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add(playerName1,gbc);
    gbc.gridy = 2;
    panel.add(playerName2,gbc);
    gbc.gridy = 3;
    panel.add(playerName3,gbc);
    gbc.gridx = 2;
    gbc.gridy = 1;
    panel.add(playerName4,gbc);
    gbc.gridy = 2;
    panel.add(playerName5,gbc);
    gbc.gridy = 3;
    panel.add(playerName6,gbc);

    gbc.ipady = 20;
    gbc.gridx = 0;
    gbc.gridy = 4;
    panel.add(nPlayerLabel,gbc);
    gbc.gridx = 1;
    gbc.gridy = 4;
    gbc.gridwidth = 2;
    panel.add(nPlayerControl,gbc);
    gbc.gridwidth = 1;
    gbc.gridx = 1;
    gbc.gridy = 5;
    panel.add(WClabel,gbc);
    gbc.ipady=5;
    gbc.gridx = 1;
    gbc.gridy = 6;
    panel.add(wincond1,gbc);
    gbc.gridx = 1;
    gbc.gridy = 7;
    panel.add(wincond2,gbc);
    gbc.gridx = 1;
    gbc.gridy = 8;
    panel.add(wincond3,gbc);
    gbc.ipady = 20;
    gbc.gridx = 3;
    gbc.gridy = 5;
    panel.add(LClabel,gbc);
    gbc.ipady=5;
    gbc.gridx = 3;
    gbc.gridy = 6;
    panel.add(losecond1,gbc);
    gbc.gridx = 3;
    gbc.gridy = 7;
    panel.add(losecond2,gbc);
    gbc.gridx = 3;
    gbc.gridy = 8;
    panel.add(losecond3,gbc);

    gbc.gridx = 1;
    gbc.gridy = 9;
    panel.add(winThresholdBox,gbc);
    gbc.gridx = 3;
    panel.add(loseThresholdBox,gbc);
    gbc.gridx = 0;
    panel.add(thresLabel1,gbc);
    gbc.gridx = 2;
    panel.add(thresLabel2,gbc);

    JLabel startScoreLabel = new JLabel("Start Score: ");
    gbc.gridx = 0;
    gbc.gridy = 10;
    panel.add(startScoreLabel,gbc);
    gbc.gridx = 1;
    panel.add(startScoreBoc,gbc);

    dialogue.getContentPane().add(BorderLayout.CENTER,panel);

    lowPanel = new JPanel();
    lowPanel.add(OkButton);
    lowPanel.add(cancelButton);
    //dialogue.add(BorderLayout.SOUTH,lowPanel);
    dialogue.getContentPane().add(BorderLayout.SOUTH, lowPanel);
    dialogue.setVisible(true);
    //Players per row (either 2 or 3)

  }
  int setPlayersPerRow(int nPlayers){
    if(nPlayers > 4){
      PlayersPerRow = 3;
    }
    else{
      PlayersPerRow = 2;
    }
    return PlayersPerRow;
  }
  void OnOKButton(JSlider nPlayerControl,JTextField playerName1,
  JTextField playerName2,
  JTextField playerName3,
  JTextField playerName4,
  JTextField playerName5,
  JTextField playerName6,
  JRadioButton wincond1,
  JRadioButton wincond2,
  JRadioButton wincond3,
  JRadioButton losecond1,
  JRadioButton losecond2,
  JRadioButton losecond3,
  JTextField winThresholdBox,
  JTextField loseThresholdBox,
  JTextField startScoreBoc){
    boolean profileOK = true;
    ident = "New Profile";
    PlayersPerRow = NumberOfPlayers = nPlayerControl.getValue();
    setPlayersPerRow(NumberOfPlayers);
    playerNames = new String[]{ //These are the default player names
      new String("Player 1"),
      new String("Player 2"),
      new String("Player 3"),
      new String("Player 4"),
      new String("Player 5"),
      new String("Player 6")
    };
    playerNames[0] = playerName1.getText();
    playerNames[1] = playerName2.getText();
    playerNames[2] = playerName3.getText();
    playerNames[3] = playerName4.getText();
    playerNames[4] = playerName5.getText();
    playerNames[5] = playerName6.getText();
    //set win condition:
    if(wincond1.isSelected()){
      winCondition = "lowScoreThreshold";
    } else if (wincond2.isSelected()){
      winCondition = "highScoreThreshold";
    } else{
      winCondition = "None";
    }
    if(losecond1.isSelected()){
      LoseCondition = "lowScoreThreshold";
    } else if(losecond2.isSelected()){
      LoseCondition = "highScoreThreshold";
    } else{
      LoseCondition = "None";
    }

    /*try{
      int a=Integer.parseInt(s1);
      System.out.println(Integer.toString(a));
      ScoreCounter.PlayerBox.changeThePlayerScore(playerNumber, a);
      ScoreCounter.TheScoreCounters[playerNumber].updateScoreLabel(ScoreCounter.PlayerBox.getPlayerScore(playerNumber));
    }*/
    try{
      winConditionThresholdScore = Integer.parseInt(winThresholdBox.getText().trim());
    }
    catch(Exception Ex){
      System.out.println("win condition threshold score not an integer");
      profileOK = false;
    }
    try{
      loseConditionThresholdScore = Integer.parseInt(loseThresholdBox.getText().trim());
    }
    catch(Exception Ex){
      System.out.println("lose condition threshold score not an integer");
      profileOK = false;
    }
    try{
      startScore = Integer.parseInt(startScoreBoc.getText().trim());
    }
    catch(Exception Ex){
      System.out.println("Starting score not an integer");
      profileOK = false;
    }


    if(profileOK==true){
      newProfile = new CounterProfile(ident,NumberOfPlayers,playerNames,PlayersPerRow,startScore,winCondition,winConditionThresholdScore,LoseCondition,loseConditionThresholdScore);
      ScoreCounter.profile = newProfile;
      ScoreCounter.frame.dispose();
      ScoreCounter.profile.resetCounter();
      dialogue.dispatchEvent(new WindowEvent(dialogue, WindowEvent.WINDOW_CLOSING));
    } else{
      System.out.println("Invalid profile settings");
    }
  }
}


public class ScoreCounter {
  static GameController gController;
  static PlayersContainer PlayerBox;
  static int NumberOfPlayers = 6;
  static String[] playerNames;
  static int PlayersPerRow = 3;
  static PlayerScoreCounterGUI[] TheScoreCounters;
  static CounterProfile profile;
  static ProfileContainer profileBox;
  static JFrame frame;

  public static JFrame setupGui(){

    frame = new JFrame("Jake's score counter");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(800,600);

    //Creating the MenuBar and adding components
    MenuBar objMB = new MenuBar();//The menu bar is created in a separate class
    JMenuBar mb = objMB.getMenuBar();//This method collects the menu bar
    //Lower panel
    JPanel panel = new JPanel(); // the panel is not visible in output
    JButton button1 = new JButton("Exit");
    JButton button2 = new JButton("Reset");
    button1.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
           frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
       }
    });
    button2.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
           //System.out.println("Reset button pushed");
           ScoreCounter.frame.dispose();
           ScoreCounter.profile.resetCounter();
       }
    });
    panel.add(button1); // Adds Button to content pane of frame
    panel.add(button2);

    //The center panel where all the cool stuff happens
    JPanel bPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    GridBagLayout layout = new GridBagLayout();
    bPanel.setLayout(layout);

    TheScoreCounters = new PlayerScoreCounterGUI[NumberOfPlayers];
    int rowCount = 0;

    for (int i = 0; i < NumberOfPlayers; i++) {
      if(i - (rowCount*PlayersPerRow) > (PlayersPerRow-1)){
        rowCount++;
      }
      TheScoreCounters[i] = new PlayerScoreCounterGUI(playerNames[i],startScore,i);
      gbc.gridy = rowCount;
      gbc.gridx = i - rowCount*PlayersPerRow;
      gbc.insets = new Insets(10,10,10,10);  //top padding
      bPanel.add(TheScoreCounters[i].getPlayerPanel(), gbc);
    }

    //Adding Components to the frame.
    frame.getContentPane().add(BorderLayout.SOUTH, panel);
    frame.getContentPane().add(BorderLayout.NORTH, mb);
    frame.getContentPane().add(BorderLayout.CENTER, bPanel);
    frame.setVisible(true);
    return frame;
  }

  public static boolean gameOver = false; //Has the game ended?
  public static int startScore = 50; //Starting score for all players
  public static void main(String[] args) {

    playerNames = new String[]{ //These are the default player names
      new String("Player 1"),
      new String("Player 2"),
      new String("Player 3"),
      new String("Player 4"),
      new String("Player 5"),
      new String("Player 6")
    };

    profileBox = new ProfileContainer();
    profileBox.populateDefaultProfiles();
    profile = profileBox.getProfile("StarRealmsProfile");
    //profile = StarRealmsProfile;

    PlayerBox = new PlayersContainer();
    PlayerBox.populatePlayers(playerNames, startScore);
    gController = new GameController("lowScoreThreshold", 0, "None", 0);

    profile.resetCounter();
  }


}
