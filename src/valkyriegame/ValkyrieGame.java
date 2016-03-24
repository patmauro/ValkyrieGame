package valkyriegame;

/**
 * @author Patrick Mauro
 * ~~~~~~~~~~~~~~~~~~~~~~~ 
 * 
 * Patrick Mauro
 * Prof. Walter
 * CS102 Spring 2010
 * Final Project : Ride of the USS Valkyrie
 * 
 * File: ValkyrieGame.java
 * -------------------------
 * This program is an enhanced version UFOWorld, rebuilt from the ground-up
 * in a manner that implements acm.graphics and javax.swing. This version is
 * called "Flight of the USS Valkyrie," features lots of graphics, extensive
 * sound effects and music, and a scorekeeping method based on how long the 
 * user took to play a round. The highest possible score is 250, which would
 * be awarded to the player if he or she finished a round in zero seconds. 
 * This is realistically impossible, but the challenge lies in getting as
 * close to 250 as possible, through practice.
 * 
 */

//Imports
import acm.graphics.*;
import java.*;
import acm.program.*;
import acm.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.URL;

public class ValkyrieGame extends GraphicsProgram{
  
  /************************ Named constants ********************************/
  private boolean shooting = false; // indicates if new shot needs to be fired
  private boolean fireshots = false; // signals shot to move upward
  private boolean musicPlaying = false;
  private boolean gameBegin = false;
  private boolean moveRight = false;
  private boolean moveLeft = false;
  private boolean gamePlay = true; // indicates whether game is active
  private boolean onTitleScreen = true;
  private boolean titleMusicPlaying = false;
  private boolean soundsTested = false;
  private boolean clockStarted = false;
  private int timesHit = 0;
  
  /*    Note - the following values determine speed, and seem to vary WILDLY
        across different jdk versions, for whatever reason.
        They may need to be tweaked for proper performance.
        They were originally set to dx = 0.02, dy = 0.02, dz = 0.1
        but have gone as low as 0.001, 0.001 & 0.005 in further tweaks.
  */
  private double dxStart = 0.02, dyStart = 0.02, dzStart = 0.1;
  private double dx = dxStart, dy = dyStart, dz = dzStart;
  
  private int loc;
  private long a;
  private long b;
  private long scores;
  private int valkPos;
  
  //Sounds
  URL sound1 = getClass().getResource("/SF_Phaser01.wav");
  AudioClip sfx = java.applet.Applet.newAudioClip(sound1);
  URL sound2 = getClass().getResource("/boom.wav");
  AudioClip boom = java.applet.Applet.newAudioClip(sound2);
  URL sound3 = getClass().getResource("/mus.wav");
  AudioClip mus = java.applet.Applet.newAudioClip(sound3);
  URL sound4 = getClass().getResource("/Lose.wav");
  AudioClip losemus = java.applet.Applet.newAudioClip(sound4);
  URL sound5 = getClass().getResource("/Win.wav");
  AudioClip winmus = java.applet.Applet.newAudioClip(sound5);
  URL sound6 = getClass().getResource("/SF_Phaser02.WAV");
  AudioClip hitFizzle = java.applet.Applet.newAudioClip(sound6);
  URL sound7 = getClass().getResource("/introMusic.wav");
  AudioClip introMusic = java.applet.Applet.newAudioClip(sound7);
  
  /********************** Instance variables *******************************/
  
  
  //Elements of the USS Valkyrie
  private GCompound valk = new GCompound();
  private GImage valkImage = new GImage("UssValkyrie.gif");
  private GImage right = new GImage("turnRight.gif");
  private GImage left = new GImage("turnLeft.gif");
  //Elements of a UFO
  private GCompound ufo = new GCompound();
  private GImage ufoImage = new GImage("UFOdarker.gif");
  private GImage explosion = new GImage("animatedexplosion.gif");
  //Elements of the HP Bar
  private GCompound hpbar = new GCompound();
  private GLabel hp = new GLabel("UFO's HP: ", 0, 30);
  private GRect bar1 = new GRect(5, 20);
  private GRect bar2 = new GRect(5, 20);
  private GRect bar3 = new GRect(5, 20);
  private GRect bar4 = new GRect(5, 20);
  private GRect bar5 = new GRect(5, 20);
  //Endgame Messages
  private GLabel win = new GLabel("Alien ship destroyed! "
          + "Congratulations, you have saved planet Earth!", 200, 30);
  private GLabel lose = new GLabel("The Alien ship has made it past the "
          + "Valkyrie to Earth. Game Over...", 250, 30);
  private GLabel score;
  private GLabel again = new GLabel("Press New Game to play again. "
          + "Try to beat your score! Or, press quit to exit the game.");
  //The Valkyrie's laser blasts (aka "shots")
  private GRect shot;
  //Buttons used to control the game
  private JButton quitButton = new JButton("Quit"); //quits
  private JButton resetButton = new JButton("New Game"); //Restarts the game
  //creates the Intro Title
  private GCompound notice = new GCompound();
  private GLabel title = new GLabel("RIDE OF THE USS VALKYRIE");
  private GLabel story = new GLabel("You control the United Space Ship "
          + "'Valkyrie' as it desperatly defends the Earth.");
  private GLabel story2 = new GLabel("Extraterrestrial beings are invading "
          + "in a giant ship, with plans to destroy the planet.");
  private GLabel story3 = new GLabel("Destroy the evil beings before they "
          + "pass your ship, and save the Earth!");
  private GLabel instrux = new GLabel("Use the LEFT and RIGHT arrow keys to "
          + "move, and UP to fire a laser blast.");
  private GLabel instrux2 = new GLabel("Score is based on how quickly the "
          + "UFO is destroyed. The higher the better - try to see if "
          + "you can get above 200!");
  private GLabel clickplz = new GLabel("Click anywhere in this window "
          + "(NOT the 'New Game' Button) to begin!!");
  private GLabel warning = new GLabel("NOTE: Please do not resize this "
          + "window. Doing so will disturb the functionality of this game.");
  //Space Background
  private GImage space = new GImage("night-sky.gif");
  
  
  
  /****************** Instance Methods*************************************/
  
  /**
   * Called by acm class when the class start method is invoked
   * in main method.
   * Creates all the objects used in the game.
   */
  @Override
  public void init() {
    //Sets the background
    space.setSize(1.25*getWidth(), 1.25*getHeight());
    space.setLocation(-getWidth()/8, 0);
    add(space);
    
// Adds the UFO!
    ufo.setLocation(getWidth()/2, 0);
    ufo.add(ufoImage);
    
    //Adds the HP bar
    hp.setFont("Arial Bold-20");
    hp.setColor(Color.red);
    hpbar.add(hp);
    bar1.setFilled(true);
    bar2.setFilled(true);
    bar3.setFilled(true);
    bar4.setFilled(true);
    bar5.setFilled(true);
    bar1.setFillColor(Color.RED);
    bar2.setFillColor(Color.RED);
    bar3.setFillColor(Color.RED);
    bar4.setFillColor(Color.RED);
    bar5.setFillColor(Color.RED);
    bar1.setLocation(100, 12);
    bar2.setLocation(110, 12);
    bar3.setLocation(120, 12);
    bar4.setLocation(130, 12);
    bar5.setLocation(140, 12);
    hpbar.add(bar1);
    hpbar.add(bar2);
    hpbar.add(bar3);
    hpbar.add(bar4);
    hpbar.add(bar5);
    
    //Adds the USS Valkyrie
    valk.add(valkImage);
    valkPos = getHeight()-100;
    valk.setLocation(getWidth()/2, valkPos);
    
    //Adds a shot
    shot = new GRect(4, 8);
    shot.setFilled(true);
    shot.setFillColor(Color.GREEN);
    
    
    //Adds JButtons to south border
    add(resetButton, SOUTH);
    add(quitButton, SOUTH);
    
    //Adds the Intro Graphic
    title.setFont("Arial Bold-Italic-45");
    title.setColor(Color.white);
    title.setLocation(60, getHeight()/2-60);
    notice.add(title);
    story.setFont("Arial-15");
    story.setColor(Color.white);
    story.setLocation(90, getHeight()/2-40);
    story2.setFont("Arial-15");
    story2.setColor(Color.white);
    story2.setLocation(87, getHeight()/2-25);
    story3.setFont("Arial-15");
    story3.setColor(Color.white);
    story3.setLocation(130, getHeight()/2-10);
    notice.add(story);
    notice.add(story2);
    notice.add(story3);
    instrux.setFont("Arial-15");
    instrux.setColor(Color.white);
    instrux.setLocation(130, getHeight()/2+50);
    instrux2.setFont("Arial-15");
    instrux2.setColor(Color.white);
    instrux2.setLocation(13, getHeight()/2+65);
    notice.add(instrux);
    notice.add(instrux2);
    clickplz.setFont("Arial-Bold-20");
    clickplz.setColor(Color.white);
    clickplz.setLocation(30, getHeight()-35);
    notice.add(clickplz);
    warning.setFont("Arial-11");
    warning.setColor(Color.white);
    warning.setLocation(130, getHeight()-5);
    notice.add(warning);
    add(notice);
    
    //Register this window as an action, mouse and key listener
    addActionListeners();
    addKeyListeners();
    addMouseListeners();
    
    // Call the loop that makes all animations run
    startEverything();
  }//End of init method
  
  
  /**
   * Starts all animation in the game by creating a continuous loop.
   */
  public void startEverything(){
    //This loop will continue until the user closes the window
    while (true) {
      // Starts the game, adding all the gameplay elements and music loop,
      // and setting the UFO's movement
      if(onTitleScreen &! titleMusicPlaying){
        introMusic.play();
        titleMusicPlaying = true;
      }
      if(gameBegin){
        onTitleScreen = false;
        titleMusicPlaying = false;
        add(ufo);
        add(valk);
        add(hpbar);
        if(musicPlaying ==false){
          mus.loop();
          musicPlaying = true;
        }
        ufo.move(dx, dy/5);
        if(!clockStarted){
          a = System.currentTimeMillis();
          clockStarted = true;
          gamePlay = true;
          fireshots=false;
          shooting=false;
        }
      }
      //Moves the valkyrie to the right at a speed of dz if the moveRight
      //boolean has been activated by the KeyListener.
      if(moveRight){
        valk.move(dz, 0);
        space.move(-dz/6, 0);
      }
      //Moves the valkyrie to the left at a speed of dz if the moveLeft
      //boolean has been set to true by the KeyListener.
      if(moveLeft){
        valk.move(-dz, 0);
        space.move(dz/6, 0);
      }
      //Moves the shot above the AUP's current position if the gamePlay 
      //boolean is true and if the shooting boolean has been set to true by
      //the KeyListener. It then resets shooting to false and sets fireshots
      //to true.
      if (shooting&&gamePlay){
        if((fireshots&&shot.getY()<0)||(!fireshots)){
          shot.setLocation(valk.getX()+22, getHeight()-65);
          add(shot);
          sfx.stop();
          sfx.play();
          fireshots = true;
          shooting = false;               
        }
      }
      //Moves the shot upwards at a speed of 0.2 if fireshots has been set 
      //to true by the previous if statement. Must be within its own boolean
      //or else the shots will never move.
      if(fireshots){
        shot.move(0, -dz);
      }
      //If hit() is true (meaning, the shot has hit the UFO), this method
      //increases the value of the static integer timesHit, moves the black 
      //mask rect within hpbar slightly to the left, covering up one of the 
      //notches representing the UFO's HP. It then moves the shot offscreen 
      //where it can't be seen by the player, creating the illusion that it
      //has truly hit the UFO. If the UFO has been hit 5 times by a valk laser,
      //this method stops the current sound effect, turns off the game's 
      //functionality by switching the gamePlay boolean to "false,"
      //stops the movement of the UFO and the AUP,
      //sets the hpbar mask over the entire text of the HPbar,
      //making it look as though it has disappeared,
      //displays a victory message by configuring and ADDing the win GLabel,
      //ADDs the "explosion" image over the UFO, 
      //and plays the explosion "boom" sound effect.
      if(hit() ==true){
        timesHit++;
        shot.move(-800, 0);
        remove(shot);
        if(timesHit ==1){hpbar.remove(bar5);}
        if(timesHit ==2){hpbar.remove(bar4);}
        if(timesHit ==3){hpbar.remove(bar3);}
        if(timesHit ==4){hpbar.remove(bar2);}
        if(timesHit ==5){
          b = System.currentTimeMillis();
          gamePlay=false;
          fireshots=false;
          shooting=false;
          dx=0; dy=0; dz=0;
          hpbar.remove(bar1);
          mus.stop();
          musicPlaying = false;
          winmus.play();
          boom.play();       
          ufo.remove(ufoImage);
          ufo.add(explosion, -30, -75);
          pause(700);
          ufo.remove(explosion);
          scores = (250-((b-a)/100));
          if(scores<0){
            scores = 0;
          }
          score =new GLabel("Score: "+scores);
          score.setFont("Arial Bold-20");
          score.setColor(Color.white);
          score.setLocation(330, getHeight()/2);
          again.setFont("Arial-15");
          again.setColor(Color.white);
          again.setLocation(90, (getHeight()/2)+40);
          add(score);
          add(again);
          remove(hpbar);
          win.setFont("Arial Bold-20");
          win.setColor(Color.white);
          win.setLocation(50, 70);
          add(win);          
          gameBegin = false;
          clockStarted = false;
        }
        else{
          hitFizzle.stop();
          hitFizzle.play();
        }
      }
      
      // If the UFO hits a vertical edge,
      // this method reverses its x-axis movement.
      if (isAtRightEdge() && dx > 0 || isAtLeftEdge() && dx < 0) {
        dx = -dx;        
      }
      //If the UFO hits the bottom of the screen, this method 
      //stops the  movement
      //and displays "GAME OVER" by ADDing the lose GLabel.
      if (isAtBottomEdge() && dy > 0) {
        b = System.currentTimeMillis();
        gamePlay = false;
        fireshots = false;
        shooting=false;
        dx =0; dy = 0; dz=0;
        mus.stop();
        musicPlaying = false;
        losemus.play();
        scores = (250-((b-a)/100));
        if(scores<0){
          scores = 0;
        }
        score =new GLabel("Score: "+scores);
        score.setFont("Arial Bold-20");
        score.setColor(Color.white);
        score.setLocation(330, getHeight()/2);
        again.setFont("Arial-15");
        again.setColor(Color.white);
        again.setLocation(90, (getHeight()/2)+40);
        add(score);
        add(again);
        remove(hpbar);
        lose.setFont("Arial Bold-20");
        lose.setColor(Color.red);
        lose.setLocation(55, 70);
        add(lose);
        gameBegin = false;
        clockStarted = false;
      }
    }
  }//End of startEverything method
  
  
  /**
   * @return true if a shot hits the ufo
   */
  public boolean hit(){
    if (Math.sqrt(
            (((ufo.getX()+45) - shot.getX())*((ufo.getX()+45) - shot.getX())) + 
            (((ufo.getY()+20) - shot.getY())*((ufo.getY()+20) - shot.getY()))
            ) <= 20){
      return true;   
    }
    else{return false;}
  }//end of hit method
  
  
  /**
   * Method called by system when an ActionEvent occurs
   * @param evt event created by a JButton being clicked.
   */
  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == quitButton) {
      exit();
    }
    if (evt.getSource() == resetButton &! gamePlay){
      resetFromEnd();
    }
  }//end of actionPerformed method
  
  /**
   * Method called by system when a MouseEvent occurs
   * @param evt event created by the mouse being clicked.
   */
  @Override
  public void mouseClicked(MouseEvent evt){
    if(onTitleScreen){
      remove(notice);
      introMusic.stop();
      gameBegin = true;
    }
  }//end of mouseClicked method
  
  
  public void resetFromEnd(){
    //Lets the program know that the Title Screen is activated.
    onTitleScreen = true;
    //removes the messages
    remove(win);
    remove(lose);
    remove(score);
    remove(again);
    //resets the background
    space.setLocation(-getWidth()/8, 0);
    //resets the UFO
    ufo.remove(ufoImage);
    remove(ufo);
    ufo.setLocation(getWidth()/2, 0);
    ufo.add(ufoImage);
    explosion = new GImage("animatedexplosion.gif");
    //resets the Valkyrie
    remove(valk);   
    valk.setLocation(getWidth()/2, valkPos);
    //resets the HP bar
    hpbar.add(bar1);
    hpbar.add(bar2);
    hpbar.add(bar3);
    hpbar.add(bar4);
    hpbar.add(bar5);
    //resets the statistics
    dx = dxStart;
    dy = dyStart;
    dz = dzStart;
    timesHit=0;
    add(notice);
  }//end of resetFromEnd method
  
  
  /**
   * Method called by system when a KeyEvent occurs
   * @param event created by a Virtual Key being pressed
   */
  public void keyPressed(KeyEvent event) {
    if(event.getKeyCode()==KeyEvent.VK_RIGHT
         &&gamePlay
         &! aupIsAtRightEdge()){
      moveRight = true;
      valk.remove(valkImage);
      valk.add(right);
    }
    else if(event.getKeyCode()!=KeyEvent.VK_UP)
    {moveRight = false;}
    
    if (event.getKeyCode() == KeyEvent.VK_LEFT 
          &&gamePlay
          &! aupIsAtLeftEdge()){
      moveLeft = true;
      valk.remove(valkImage);
      valk.add(left);
    }
    else if(event.getKeyCode()!=KeyEvent.VK_UP)
    {moveLeft = false;}
    if (event.getKeyCode() == KeyEvent.VK_UP){
      shooting = true;
    }
  }//End of keyPressed method
  
  
  /**
   * Method called by system when a KeyEvent occurs
   * @param event created by a Virtual Key being released
   */
  public void keyReleased(KeyEvent event) {
    if(event.getKeyCode()==KeyEvent.VK_RIGHT || aupIsAtRightEdge()){
      moveRight = false;
      valk.remove(right);
      valk.add(valkImage);
    }
    if (event.getKeyCode() == KeyEvent.VK_LEFT || aupIsAtLeftEdge()){
      moveLeft = false;
      valk.remove(left);
      valk.add(valkImage);
    }
  }//End of keyReleased method
  
  
  // "IS-AT-N-EDGE" BOOLEANS -
  // Return true if the given object hits the given edge:
  
  
  //Valkyrie Booleans:
  
  /**
   * @return true when valk is at the left edge of the window
   */
  private boolean aupIsAtLeftEdge() {
    return valk.getX()-30 <= 0;
  }//End of aupIsAtLeftEdge method
  
  /**
   * @return true when valk is at the right edge of the window
   */
  private boolean aupIsAtRightEdge() {
    return valk.getX() +80  >= getWidth();
  }//End of aupIsAtRightEdge method
  
  
  //UFO Booleans:
  
  /**
   * @return true when the UFO is at the left edge of the window
   */
  private boolean isAtLeftEdge() {
    return ufo.getX() <= 0;
  }//End of isAtLeftEdge method
  
  /**
   * @return true when the UFO is at the right edge of the window
   */
  private boolean isAtRightEdge() {
    return ufo.getX() + 90 >= getWidth();
  }//End of isAtRightEdge method
  
  /**
   * @return true when the UFO has hit the bottom edge of the window
   */
  private boolean isAtBottomEdge() {    
    return ufo.getY() +ufo.getHeight() >= getHeight();
  }//End of isAtBottomEdge method
  
  
  //End of "IS-AT-N-EDGE" BOOLEANS 
  
  
  /**
   * Execution starts here
   * @param args unused
   */
  public static void main(String[] args) {
    ValkyrieGame vg = new ValkyrieGame();
    vg.start();
  }//End of main method
  
  
}//End of ValkyrieGame class body
